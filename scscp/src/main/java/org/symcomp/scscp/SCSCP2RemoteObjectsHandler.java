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
 * This class implements the standard SCSCP2 calls
 * <ul>
 * <li><code>store_session</code>,
 * <li><code>retrieve</code>, and
 * <li><code>unbind</code>
 * </ul>
 * the <code>store_persistent</code>-call is at the moment unsupported, 
 * as it requires more permanent store of objects, e.g. on disk.
 */
public class SCSCP2RemoteObjectsHandler extends ProcedureCallHandler {
	protected CookieStore cookies;

	public SCSCP2RemoteObjectsHandler(CookieStore cookies) {
		this.cookies = cookies;
	}

	public OMSymbol[] getServiceNames() {
		return new OMSymbol[] { 
			new OMSymbol("scscp2", "store_session"),
			new OMSymbol("scscp2", "retrieve"),
			new OMSymbol("scscp2", "unbind")
		};
	}
	public String getDescription(OMSymbol oms) { 
		return "Support for some of the standard SCSCP2 calls"; 
	}

	protected ProcedureDone HandleStoreSession(ProcedureCall pc)
	throws OpenMathException
	{
		OpenMathBase o = pc.getArgument(0);
		OMReference r = cookies.store(o);
		return ProcedureDone.constructProcedureCompleted(pc, r);
	}

    protected ProcedureDone HandleRetrieve(ProcedureCall pc)
	throws OpenMathException
	{
		/* Extract reference sought */
		OMReference ref;
		try {
			ref = ((OMReference) pc.getArgument(0));
		} catch (Exception e) {
			throw new OpenMathException("Argument to scscp2.retrieve must be an OMR");
		}

		System.out.println("Trying to resolve " + ref.toXml());

		OpenMathBase o = cookies.retrieve(ref);
		if (o == null) throw new OpenMathException("Key not found");

		return ProcedureDone.constructProcedureCompleted(pc, o);
	}
	protected ProcedureDone HandleUnbind(ProcedureCall pc)
	throws OpenMathException
	{
		OMReference ref;
		try {
			ref = ((OMReference) pc.getArgument(0));
		} catch (Exception e) {
			throw new OpenMathException("Argument to scscp2.unbind must be an OMR");
		}

		boolean b = cookies.unbind(ref);
		if (!b) throw new OpenMathException("Unbind failed.");

		return ProcedureDone.constructProcedureCompleted(pc, null);
	}

    public ProcedureDone handle(ProcedureCall pc)
	throws OpenMathException {
		String sn = pc.getServiceName().fullname();
		if ( sn.equals("scscp2.store_session") ) {
			return HandleStoreSession(pc);
		} else if ( sn.equals("scscp2.retrieve")) {
			return HandleRetrieve(pc);
		} else if (sn.equals("scscp2.unbind")) {
			return HandleUnbind(pc);
		} else {
			throw new OpenMathException("SCSCP2RemoteObjectsHandler: Unsupported symbol: " + sn);
		}
	}
}
