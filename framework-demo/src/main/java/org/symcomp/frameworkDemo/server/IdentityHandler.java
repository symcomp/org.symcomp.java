package org.symcomp.frameworkDemo.server;

import org.symcomp.scscp.*;
import org.symcomp.openmath.*;

public class IdentityHandler extends ProcedureCallHandler {
	public OMSymbol getServiceName() { return new OMSymbol("fns1", "identity"); }
	public String getDescription(OMSymbol oms) { return "identity(arg): Return the 1st argument given"; }

	public OpenMathBase handlePayload(OpenMathBase omb)
	throws OpenMathException
	{
		//omb contains the call as 1st argument, so we will bypass that.
		OpenMathBase p = ((OMApply) omb).getParams()[0];

		//Return the result
		return p;
	}
}
