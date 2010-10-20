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

import java.net.*;
import java.util.HashMap;

import org.symcomp.openmath.*;

/**
 * The class used to implement the handlers inside an SCSCP server.
 * <br>
 * To create your own handler this class needs to be subclassed and the methods
 * <ul>
 * <li><code>handlePayload</code>,
 * <li><code>getDescription</code>,
 * <li><code>getServiceNameStr</code>,
 * </ul> need to be overriden. 
 *
 * In a more advanced scenario you may want to override <code>handle</code> 
 * (instead of <code>handlePayload</code>), and/or <code>getServiceNames</code> or
 * <code>getServiceName</code> (instead of <code>getServiceNameStr</code>)
 */
public abstract class ProcedureCallHandler {
	/**
     * The method that subclasses should override and do something sensible in.
     * @param payload the input
     * @return the output
     */
	public OpenMathBase handlePayload(OpenMathBase payload)
	throws OpenMathException {
		return null;
	}

	/**
     * The method called by the server. In <code>ProcedureCallHandler</code>
     * it simply calls <code>handlePayload</code> and wraps its response in 
     * a ProcedureCompleted object.
     * @param pc the Procedure Call containing the query
     * @return a Procedure Completed message
     */
    public ProcedureDone handle(ProcedureCall pc) throws OpenMathException {
        OpenMathBase payload = pc.getPayload();
        OpenMathBase result = this.handlePayload(payload);
		if (result.isError()) {
			return ProcedureDone.constructProcedureTerminated(pc, result);
		} else {
        	return ProcedureDone.constructProcedureCompleted(pc, result);
		}
    }

    /**
     * A concise description of this handler. 
     * @param smb Since ProcedureCallHandlers may handle more than one
     *   service (by implementing <code>handle</code>), different descriptions
     *   may be required for different services. Hence this parameter.
     * @return a String describing the service.
     */
	public abstract String getDescription(OMSymbol smb);


	/**
     * Method used by the server to check whether a handler supports a particular
     * service.
     * In <code>ProcedureCallHandler</code>, this method simply returns 
     * an array with one element: the result of <code>getServiceName()</code>.
	 *@return the service names handled by this handler
     */
	public OMSymbol[] getServiceNames() {
		OMSymbol[] r = new OMSymbol[1];
		r[0] = getServiceName();
		return r;
	}

	/**
     * In <code>ProcedureCallHandler</code>, this method simply returns 
     * an OMSymbol whose CD is scscp_transient_1, and whose name is
     * <code>getServiceNameStr()</code>.
	 *@return the single service name handled by this handler
     */
	public OMSymbol getServiceName() {
		return new OMSymbol("scscp_transient_1", getServiceNameStr());
	}

	/**
     * In <code>ProcedureCallHandler</code>, this method returns null.
	 *@return the single service name handled by this handler as string, if available.
     */
	public String getServiceNameStr() {
		return null; 
	}

}
