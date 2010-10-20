//---------------------------------------------------------------------------
//  Copyright 2006-2009 
//    Dan Roozemond, d.a.roozemond@tue.nl, (TU Eindhoven, Netherlands)
//    Peter Horn, horn@math.uni-kassel.de (University Kassel, Germany)
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//---------------------------------------------------------------------------

package org.symcomp.scscp;

import org.symcomp.openmath.*;
import java.util.*;
import java.io.PrintWriter;
import java.io.BufferedReader;

/**
 * A class to wrap the functionality of an SCSCP-server.
 * <br>
 * Whilst its superclass <code>C3PO</code> implements the transport layer of the
 * SCSCP protocol, you can subclass <code>SCSCPServer</code> to simplify the
 * development of an actual SCSCP compliant server.
 * <br>
 * A typical subclass looks like this (remove the _ before the @):
 * <pre>
	_@SCSCPServerInfo(
		serviceName="MyBrilliantSCSCPServer",
		serviceVersion="0.0.1",
		serviceDescription="Solves every problem"
	)
    public class MySCSCPServer extends SCSCPServer {

		public static void MySCSCPServer(String[] args) throws Exception {
			System.out.println(getServiceName() + " " + getServiceVersion() + " starting...");
			breed(MySCSCPServer.class, 26134); // blocks
		}

		MySCSCPServer(String serviceId, java.io.PrintWriter out, java.io.BufferedReader inn) {
			super(MySCSCPServer.class, serviceId, out, inn);
			this.addHandler(new ProblemSolver());
			// ... further Handlers go here
		}
	}
    </pre>
 *
 */

public class SCSCPServer extends C3PO {

    /*
       Note on the cookies: For now, every SCSCP Server has its own cookie store.
       The downside is that cookies are lost between sessions,
       the upside is that noone has access to anybody else's cookies.

       The cookie store is passed along with ProcedureCall objects.
    */
    protected CookieStore cookies;

    /**
     * Keeps the handler for the different Request types.
     */
    private Hashtable<String, ProcedureCallHandler> handlers;

	/**
     * Keeps track of terminated computations
     */
	protected HashSet<String> terminatedComputations;
	
    public SCSCPServer() {
        super();
    }

    /**
     * Creating an instance of the SCSCP server. You may of course create a server yourself,
     * but in general the inherited <code>spawn</code> and <code>breed</code> methods
     * can make your life easier.
     *
     * @param claz The class itself, necessary due to the stupid inheritance-model of Java.
     * @param serviceId The id of the instance
     * @param out The socket to which the SCSCP server sends it output
     * @param inn The socket from which the SCSCP server reads its input
     */
    public SCSCPServer(Class claz, String serviceId, java.io.PrintWriter out, java.io.BufferedReader inn) {
        super(serviceId, out, inn);
        this.cookies = new CookieStore();
		this.terminatedComputations = new HashSet<String>();

        // default Handlers
        this.handlers = new Hashtable<String, ProcedureCallHandler>();
        addHandler(new SCSCP2HandlersHandler(this.handlers));
        addHandler(new SCSCP2RemoteObjectsHandler(this.cookies));

        // fiddle the information out of the annotation
        SCSCPServerInfo ssi = (SCSCPServerInfo) claz.getAnnotation(SCSCPServerInfo.class);
        if (null == ssi) {
            throw new RuntimeException("You must annotate your class '"+this.getClass().getCanonicalName()+
                    "' with Annotation 'SCSCPServerInfo' to use it.");
        }
        String sn = ssi.serviceName();
        String sv = ssi.serviceVersion();
        String sd = ssi.serviceDescription();
        if (null == sn || null == sv || null == sd) {
            throw new RuntimeException("You must give all values for your class '"+
                    this.getClass().getCanonicalName()+"' with Annotation 'SCSCPServerInfo' to use it.");
        }

        addHandler(new SCSCP2ServDescHandler(sn, sv, sd));

		//Say something
		log(4, "SCSCPServer (" + claz.toString() + ") constructed.");
    }

    /**
     * This method is called from within <code>C3PO</code>s <code>run<code> command.
     * In this implementation the request is parsed to a tree of <code>OpenMathBase</code>
     * objects, deconstructed, and the appropriate handler for the request is called.
     * @param input A request as an XML encoded OpenMath string.
     * @return The answer as an XML encoded OpenMath string, or the empty string
	 *    if the procedure call had option_return_nothing set.
     */
    public String compute(String input) {
        Computation comp = null;
		OpenMathBase.ENCODINGS requestEncoding = OpenMathBase.ENCODINGS.XML;
        try {
			comp = new Computation();
			comp.setCookieStore(cookies);
			comp.receivedMessage(input);
			if (comp.getRequestEncoding() != null) requestEncoding = comp.getRequestEncoding();

			//Throw this error early -- so computations are not done for nothing
			if (comp.getProcedureCall().hasReturn(ProcedureCall.OPTION_RETURN.COOKIE_PERSISTENT)) {
				throw new OpenMathException("Persistent cookies are not supported.");
			}

			//See if the procedure call was possibly terminated earlier
			String callID = comp.getToken();
			if (callID != null && terminatedComputations.remove(callID)) {
	            log(2, String.format("computation with callID %s was cancelled.", callID));
	
				OpenMathBase err = new OMError(
					new OMSymbol("scscp1", "error_system_specific"), 
					new OpenMathBase[] { new OMString("Computation terminated, as requested.") }
				);
				comp.error(err);
				return requestEncoding.render(comp.getProcedureDone().getOMObject());
			}

            //Find handler
			OMSymbol hd = comp.getProcedureCall().getServiceName();
            log(2, "Trying to resolve " + hd.toPopcorn());

            ProcedureCallHandler pch = findHandler(hd);
            if (pch == null) {
				OpenMathBase err = new OMError(
					new OMSymbol("error", "unhandled_symbol"),
					new OpenMathBase[] { hd }
				);
				comp.error(err);
				return requestEncoding.render(comp.getProcedureDone().getOMObject());
			}
			
            log(3, "Calling handler for " + hd.toPopcorn());
			ProcedureDone pcc = pch.handle(comp.getProcedureCall());

			if (pcc == null) {
				throw new OpenMathException("ProcedureCallHandler returned null.");
			} 

            log(3, "Handler returned; " + (pcc.isProcedureCompleted() ? "Completed" : "Terminated") );
            return requestEncoding.render(pcc.getOMObject());
        } catch (Exception whoops) {
            log(1, "Exception thrown in compute:");
			log(1, "===================================================");
            logStackTrace(1, whoops);
			log(1, "===================================================");

			try {
				comp.error(new OMError(
					new OMSymbol("scscp1", "error_system_specific"),
					new OpenMathBase[] { new OMString("runtime error (" + whoops.getMessage() + ")") }
				));
				return requestEncoding.render(comp.getProcedureDone().getOMObject());

			} catch (Exception e2) {
				log(1, "Problem in compute: " + e2.getMessage());
				throw new RuntimeException("Uncaught problem in compute: " + e2.getMessage());
			}
        }
    }

	/**
     * Keep track of terminated computations
     */
    @Override
	protected void receivedTerminateRequestInternal(String callID) {
		terminatedComputations.add(callID);
		super.receivedTerminateRequestInternal(callID);
	}

    /**
     * Adds a handler for a request. The kind of request is determined inside
     * the <tt>ProcedureCallHandler</tt> provided.
     * @param pch The new handler.
     */
	public void addHandler(ProcedureCallHandler pch) {
		OMSymbol[] names = pch.getServiceNames();
        for (OMSymbol name : names) {
            String nm = name.fullname().toLowerCase();
            if (this.handlers.get(nm) != null) {
                log(1,"[!!] Replacing handler for " + name.toXml());
            } else {
                log(3,"Adding handler for " + name.toXml());
            }
            handlers.put(nm, pch);
        }
	}

    /**
     * Find the appropriate Handler for a given service
     * @param servicename The name of the service
     * @return a <code>ProcedureCallHandler</code> that can handle the given servicename
     */
	public ProcedureCallHandler findHandler(OMSymbol servicename) {
		return handlers.get(servicename.fullname().toLowerCase());
	}
	

}
