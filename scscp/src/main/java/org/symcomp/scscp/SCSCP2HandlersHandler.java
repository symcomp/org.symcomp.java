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
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This class implements the standard SCSCP2 calls
 * <ul>
 * <li><code>get_allowed_heads</code>,
 * <li><code>is_allowed_head</code>, and
 * <li><code>get_transient_cd</code>.
 * </ul>
 * The <code>get_signature</code>-call is at the moment unimplemented,
 * as we still need to do that.
 */
public class SCSCP2HandlersHandler extends ProcedureCallHandler {
	protected Hashtable<String, ProcedureCallHandler> allHandlers;

	public SCSCP2HandlersHandler(Hashtable<String, ProcedureCallHandler> allHandlers) {
		this.allHandlers = allHandlers;
	}

	public OMSymbol[] getServiceNames() {
		return new OMSymbol[] {
			new OMSymbol("scscp2", "get_allowed_heads"),
			new OMSymbol("scscp2", "get_transient_cd"),
			new OMSymbol("scscp2", "is_allowed_head")
		};
	}
	public String getDescription(OMSymbol oms) { return "Support for some of the standard SCSCP2 calls"; }

	protected OpenMathBase HandleGetAllowedHeads(ProcedureCall pc)
	throws OpenMathException
	{
		ArrayList<OpenMathBase> tmpParams = new ArrayList<OpenMathBase>();

		//First, we add the CD Group SCSCP, as that is (or should be) supported
		//(we filter out any references to scscp[0-9]+ below
		OMApply scscpgrp = new OMApply(
			new OMSymbol("metagrp", "CDGroupName"),
			new OpenMathBase[] { new OMString("scscp") }
		);
		tmpParams.add( scscpgrp );

		//Then we walk through all handlers that the server has
		for(Enumeration<ProcedureCallHandler> E = allHandlers.elements(); E.hasMoreElements() ; ) {
			OMSymbol[] smbs = E.nextElement().getServiceNames();
			for(int i = 0; i < smbs.length; ++i) {
				//We ignore default methods, i.e .those in scscp1 or scscp2
				if ( smbs[i].getCd().matches("scscp[0-9]+") ) continue;

				//The remaining methods go into the return array
				tmpParams.add(smbs[i]);
			}
		}

		//Done.
		OpenMathBase[] t2 = new OpenMathBase[tmpParams.size()];
		tmpParams.toArray(t2);

		return new OMApply(new OMSymbol("scscp2", "symbol_set"), t2);
	}

	protected OpenMathBase HandleGetTransientCD(ProcedureCall pc)
	throws OpenMathException
	{
		//Extract CDName first
		OpenMathBase arg = pc.getArgument(0);
		String CDName;
		if (!arg.isApplication("meta", "CDName")) {
			throw new OpenMathException("Argument to scscp2.get_transient_cd must be a meta.CDName");
		}
		try {
			CDName = ((OMString) ((OMApply) arg).getParams()[0]).getValue();
		} catch (Exception e) {
			throw new OpenMathException("meta.CDName must be an OMSTRING");
		}


		// Construct reply by traversing all handlers and selecting those
		// that are in the specified CD
		int countdefs = 0;
		ArrayList<OpenMathBase> tmpParams = new ArrayList<OpenMathBase>();

		//The CDName, CDDate, and Description
		tmpParams.add( new OMApply(
			new OMSymbol("meta", "CDName"),
			new OpenMathBase[] { new OMString(CDName) }
		));
		String dt = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
		tmpParams.add( new OMApply(
			new OMSymbol("meta", "CDDate"),
			new OpenMathBase[] { new OMString(dt) }
		));
		tmpParams.add( new OMApply(
			new OMSymbol("meta", "Description"),
			new OpenMathBase[] { new OMString("Transient CD created by service provider") }
		));

		//Walk through the handlers
		for(Enumeration<ProcedureCallHandler> E = allHandlers.elements(); E.hasMoreElements() ; ) {
			ProcedureCallHandler hnd = E.nextElement();
			OMSymbol[] smbs = hnd.getServiceNames();
			for(int i = 0; i < smbs.length; ++i) {
				if (!(smbs[i].getCd().equals(CDName))) continue;

				++countdefs;


				OMApply nm = new OMApply(
					new OMSymbol("meta", "Name"),
					new OpenMathBase[] { new OMString(smbs[i].getName()) }
				);
				OMApply desc = new OMApply(
					new OMSymbol("meta", "Description"),
					new OpenMathBase[] { new OMString(hnd.getDescription(smbs[i])) }
				);

				tmpParams.add( new OMApply(
					new OMSymbol("meta", "CDDefinition"),
					new OpenMathBase[] { nm, desc }
				));

			}
		}

		//Throw an error if the specified CD did not show up in our handlers.
		if (countdefs == 0) {
            return ProcedureDone.constructProcedureTerminated(pc,
				new OMError(
					new OMSymbol("scscp2", "no_such_transient_cd"),
					new OpenMathBase[] { new OMString(CDName) }
				)
			).getOMObject();
		}


		//Done.
		OpenMathBase[] t2 = new OpenMathBase[tmpParams.size()];
		tmpParams.toArray(t2);

		return new OMApply(new OMSymbol("meta", "CD"), t2);
	}

	protected OpenMathBase HandleIsAllowedHead(ProcedureCall pc)
	throws OpenMathException
	{
		// Extract requested symbol first
		OpenMathBase arg = pc.getArgument(0);

		if (!(arg.isSymbol())) {
			throw new OpenMathException("Argument to scscp2.is_allowed_head must be a symbol");
		}

		if ( allHandlers.containsKey( ((OMSymbol) arg).fullname().toLowerCase() ) ) {
			return new OMSymbol("logic1", "true");
		} else {
			return new OMSymbol("logic1", "false");
		}
	}

    public ProcedureDone handle(ProcedureCall pc)
	throws OpenMathException {
		String sn = pc.getServiceName().fullname();
		if ( sn.equals("scscp2.get_allowed_heads") ) {
			return ProcedureDone.constructProcedureCompleted(pc, HandleGetAllowedHeads(pc));
		} else if (sn.equals("scscp2.get_transient_cd") ) {
			return ProcedureDone.constructProcedureCompleted(pc, HandleGetTransientCD(pc));
		} else if (sn.equals("scscp2.is_allowed_head") ) {
			return ProcedureDone.constructProcedureCompleted(pc, HandleIsAllowedHead(pc));
		} else {
			throw new OpenMathException("SCSCP2HandlersHandler: Unsupported symbol: " + sn);
		}
	}
}
