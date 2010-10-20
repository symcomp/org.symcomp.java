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

/**
 * A class to encapsulate an SCSCP Procedure Call, used by both servers and clients.
 */
public class ProcedureCall {

	/* Basic properties */
	protected CookieStore cookies;
	protected Map<String, OpenMathBase[]> options; //key: omsymbol.toXml ; val = { omsymbol(cd, name), ... }
	protected OMSymbol serviceName;
	protected OpenMathBase[] arguments;

	/* Constants */
	public enum OPTION_RETURN { 
		NOTHING ("nothing"),
		COOKIE ("cookie"),
		OBJECT ("object"),
		COOKIE_PERSISTENT ("cookie_persistent");
		
		private final String str;
		public String toString() { return str; } 
		
	    OPTION_RETURN(String s) { this.str = s; }
	};

	/**
     * Constructor, intended to be used by clients.
     * @param callID a String with the callID
     * @param serviceName an OMSymbol carrying the name of the service to be queried
     * @param arguments a list of OpenMath encoded arguments.
     */
	public ProcedureCall(String callID, OMSymbol serviceName, OpenMathBase[] arguments) {
		this.options = new HashMap<String,OpenMathBase[]>();
		this.cookies = null;
		setOption(new OMSymbol("scscp1", "call_id"), new OMString(callID));
		this.serviceName = serviceName;
		this.arguments = arguments;
	}

	/**
     * Constructor, intended to be used by clients.
     * @param callID a String with the callID
     * @param argument an OpenMath encoded object representing the call. 
     *   This argument may either be an OMObject whose first argument is an OMApply a, 
     *   or an OMApply a. The head symbol of a will be the serviceName of the resulting
     *   procedure call, and the arguments of a will be the arguments of the call.
     */
    public ProcedureCall(String callID, OpenMathBase argument) 
	throws OpenMathException {
		
		OpenMathBase omb = argument.deOMObject();

		try {
			if (omb.isApplication() && ((OMApply) omb).getHead().isSymbol()) {
				//Can construct procedure call from omb immediately
				this.serviceName = (OMSymbol) ((OMApply) omb).getHead();
				this.arguments = ((OMApply) omb).getParams();
			} else if (omb.isSymbol()) {
				//Construct procedure call with head omb and no arguments
				this.serviceName = (OMSymbol) omb;
				this.arguments = new OpenMathBase[] {};
			} else {
				//Wrap omb in fns1.identity
				this.serviceName = new OMSymbol("fns1", "identity");
				this.arguments = new OpenMathBase[] { omb };
			}
		} catch (Exception e) {
		    throw new OpenMathException("Could not construct procedure call (is there a head OMA?) " + e.getMessage());
		}	
	
        this.options = new HashMap<String,OpenMathBase[]>();
        this.cookies = null;
		setOption(new OMSymbol("scscp1", "call_id"), new OMString(callID));

        this.serviceName = serviceName;
        this.arguments = arguments;
    }

	/**
     * Constructor, intended to be used by servers.
     * @param cookies the cookie store that is being passed around by the SCSCP server
     * @param msgin an OpenMath encoded message containing the procedure call.
     */
	public ProcedureCall(CookieStore cookies, OpenMathBase msgin)
		throws OpenMathException
	{
		this.cookies = cookies;
		OpenMathBase msg = msgin.deOMObject();

		/* Get options */
		this.options = msg.getAttributions();

		/* See if we got the callID correctly */
		if (this.getOption(new OMSymbol("scscp1", "call_id")) == null) {
			throw new OpenMathException("no call_id given");
		}

		/* Get the procedure name and the arguments */
		OMApply pc = (OMApply) msg;
		OMSymbol oms = (OMSymbol) pc.getHead();
		if (!(oms.getCd().equals("scscp1") && oms.getName().equals("procedure_call"))) {
			throw new OpenMathException("Not a procedure call");
		}

		OMApply theOMA;

		try {
			theOMA = (OMApply) pc.getParams()[0];
			this.serviceName = (OMSymbol) theOMA.getHead();
		} catch (Exception e) {
			throw new OpenMathException("The 1st argument of a procedure call must be an OMA, whose 1st argument is an OpenMath Symbol.");
		}

		this.arguments = new OpenMathBase[theOMA.getParams().length];
		for (int i = 0; i < theOMA.getParams().length; ++i) {
			this.arguments[i] = (theOMA.getParams())[i];
		}
 	}


	/* Setting and getting options */
	public void setOption(OMSymbol k, OpenMathBase v) {
		options.put(k.toXml(), new OpenMathBase[] { k, v });
	}
	public OpenMathBase[] getOption(OMSymbol k) { return options.get(k.toXml()); }
	public void removeOption(OMSymbol k) { options.remove(k.toXml()); }
		
	/* Getting properties */
	public OpenMathBase[] getCallID() { return getOption(new OMSymbol("scscp1", "call_id")); }
	public String getCallIDStr() {
		OpenMathBase c = (getCallID())[1];
		if (c.isString()) return ((OMString) c).getValue();
		return null;
	}
	public OMSymbol getServiceName() { return serviceName; }
	
	/* Using the cookies */
	public CookieStore getCookieStore() { return cookies; }
	public void setCookieStore(CookieStore s) { this.cookies = s; }

	/* Return Object/Cookie/Nothing business */
	public void setReturn(OPTION_RETURN which) {
		//which should be one of "object", "cookie", "nothing", "cookie_persistent"
		for (OPTION_RETURN s : OPTION_RETURN.values()) {
			removeOption(new OMSymbol("scscp1", "option_return_" + s.toString()));
		}
		setOption(new OMSymbol("scscp1", "option_return_" + which.toString()), new OMString(""));
	}
	public boolean hasReturn(OPTION_RETURN which) {
		return (getOption(new OMSymbol("scscp1", "option_return_" + which.toString())) != null);
    }
	public OPTION_RETURN getReturn() {
		for (OPTION_RETURN s : OPTION_RETURN.values()) {
			if (hasReturn(s)) return s;
		}
		return null;
	}
	public void ensureReturnOption(OPTION_RETURN deflt) {
		for (OPTION_RETURN s : OPTION_RETURN.values()) {
			if (hasReturn(s)) return;
		}
		setReturn(deflt);
	}

	/**
     * Get an argument of the procedure call
     * @param i the index of the argument, 0 &lt;=i &lt; n
     * @return the argument as OpenMathBase object
     */
	public OpenMathBase getArgument(int i) {
		return arguments[i];
	}

	/**
     * Convert to OpenMath Object
     * @return the procedure call as OMObject
     */
	public OMObject getOMObject() {
		OMApply payload = new OMApply(
			new OMSymbol("scscp1", "procedure_call"),
			new OpenMathBase[] { new OMApply(serviceName, arguments) }
		);

		//Options
		ensureReturnOption(OPTION_RETURN.OBJECT);
		for(Iterator<Map.Entry<String,OpenMathBase[]> > i = options.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry<String, OpenMathBase[]> me = (Map.Entry<String,OpenMathBase[]>) i.next();
			payload.putAt( me.getValue()[0], me.getValue()[1] );
		}

		//Object
		return new OMObject(payload);
	}

	/**
     * Get the payload of the procedure call
     * @return the call as OMApply a, so that the head of a is the serviceName and
     *   the arguments of a are the arguments of the procedure call.
     */
    public OpenMathBase getPayload() {
        return new OMApply(serviceName, arguments);
    }
}
