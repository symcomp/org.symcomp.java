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

/**
 * This class implements the standard SCSCP2 call
 * <ul>
 * <li><code>get_service_description</code>.
 * </ul>
 */
public class SCSCP2ServDescHandler extends ProcedureCallHandler {
	protected String CASName;
	protected String CASVersion;
	protected String CASDesc;

	public SCSCP2ServDescHandler(String CASName, String CASVersion, String CASDesc) {
		this.CASName = CASName;
		this.CASVersion = CASVersion;
		this.CASDesc = CASDesc;
	}

	public OMSymbol[] getServiceNames() {
		return new OMSymbol[] { 
			new OMSymbol("scscp2", "get_service_description")
		};
	}
	public String getDescription(OMSymbol oms) { return "Support for some of the standard SCSCP2 calls"; }

    public ProcedureDone handle(ProcedureCall pc)
	throws OpenMathException {
		OpenMathBase ret = new OMApply(
			new OMSymbol("scscp2", "service_description"),
			new OpenMathBase[] { 
				new OMString(CASName), 
				new OMString(CASVersion), 
				new OMString(CASDesc)
			}
		);

		/* return result */
		return ProcedureDone.constructProcedureCompleted( pc, ret );
	}
}
