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

import java.util.*;
import org.symcomp.openmath.*;

/**
 * A class to encapsulate SCSCP Procedure Completed and Terminated messages
 */
public class ProcedureDone {
	protected ProcedureCall call;
	protected CookieStore cookies;
	protected OpenMathBase result;
	protected Map<String, OpenMathBase[]> infos;
	
	//Should be procedure_completed or procedure_terminated
	public static final Integer PROCEDURE_COMPLETED = 1;
	public static final Integer PROCEDURE_TERMINATED = 2;
	protected Integer which;

	/* Constructors */
	public ProcedureDone(ProcedureCall call, Integer which, OpenMathBase result) {
		this.which = which;
		this.call = call;	
		this.cookies = call.getCookieStore();
		this.result = result;
		this.infos = new HashMap<String, OpenMathBase[]>();

		OMSymbol cid = new OMSymbol("scscp1", "call_id");
        this.infos.put(cid.toXml(), call.getOption(cid));
	}
	
	public ProcedureDone(ProcedureCall call, OpenMathBase msgin) {
		this.call = call;
		this.cookies = call.getCookieStore();
		
		OpenMathBase msg = msgin.deOMObject();
		if (msg.isApplication("scscp1", "procedure_completed")) {
			which = PROCEDURE_COMPLETED;
		} else if (msg.isApplication("scscp1", "procedure_terminated")) {
			which = PROCEDURE_TERMINATED;
		} else {
			System.out.println(msg.toPopcorn());
			throw new RuntimeException("ProcedureDone on something that's not a procedure_completed, and not a procedure_terminated");
		}

		this.result = ((OMApply) msg).getParam(0);
		this.infos = msg.getAttributions();
	}
	
	public static ProcedureDone constructProcedureCompleted(ProcedureCall call, OpenMathBase result) {
		return new ProcedureDone(call, PROCEDURE_COMPLETED, result);
	}
	public static ProcedureDone constructProcedureTerminated(ProcedureCall call, OpenMathBase result) {
		return new ProcedureDone(call, PROCEDURE_TERMINATED, result);
	}
	
	/* What type of message is it */
	public Integer which() { return which; }
	public boolean isProcedureCompleted() { return which == PROCEDURE_COMPLETED; }
	public boolean isProcedureTerminated() { return which == PROCEDURE_TERMINATED; }

	/* Setting and getting the info-things */
	public void setInfo(OMSymbol k, OpenMathBase v) {
		infos.put(k.toXml(), new OpenMathBase[] { k, v });
	}
	public OpenMathBase[] getInfo(OMSymbol k) { return infos.get(k.toXml()); }
	public void removeInfo(OMSymbol k) { infos.remove(k.toXml()); }
	
	/* Using the cookie store */
	public CookieStore getCookieStore() { return this.cookies; }
	public void setCookieStore(CookieStore s) { this.cookies = s; }

	/* Convert to OpenMath */
	public OMObject getOMObject() 
	throws OpenMathException {
		
		OMSymbol head;
		if (which == PROCEDURE_COMPLETED) head = new OMSymbol("scscp1", "procedure_completed");
		else if (which == PROCEDURE_TERMINATED) head = new OMSymbol("scscp1", "procedure_terminated");
		else throw new OpenMathException("ProcedureDone: Neither -Completed, nor -Terminated.");

		OMApply constr;
		if (which == PROCEDURE_COMPLETED && call.hasReturn(ProcedureCall.OPTION_RETURN.COOKIE)) {
			//Allocate the reference automatically
			assert cookies != null;
			OMReference r = cookies.store(this.result);
			constr = new OMApply(head, new OpenMathBase[] { r } );
		} else if (which == PROCEDURE_COMPLETED && call.hasReturn(ProcedureCall.OPTION_RETURN.NOTHING)) {
			//Return the empty procedure completed message.
			constr = new OMApply(head, new OpenMathBase[] {} );
		} else if (which == PROCEDURE_COMPLETED && call.hasReturn(ProcedureCall.OPTION_RETURN.COOKIE_PERSISTENT)) {
			//Request was to return a persistent cookie -- but we don't do that.
			throw new OpenMathException("We don't support persistent cookies (@ProcedureDone)");
		} else {
			//Just return the object -- either is a problem, or hasReturnObject, or has nothing,
			//   in which case this is the default.
			if (this.result != null) {
				constr = new OMApply(head, new OpenMathBase[] { this.result } );
			} else {
				constr = new OMApply( head,	new OpenMathBase[] {} );
			}
		}


		//infos
		constr.setAttributions(infos);

		//object
		return new OMObject(constr);
	}
	
	/* Get the result of the computation */
	public OpenMathBase getResult() {
		return this.result;
	}
}
