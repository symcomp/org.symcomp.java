package org.symcomp.frameworkDemo.server;

import org.symcomp.scscp.*;
import org.symcomp.openmath.*;

public class AdditionHandler extends ProcedureCallHandler {
	public String getServiceNameStr() { return "addition"; }
	public String getDescription(OMSymbol oms) { return "addition(lst): Add numbers"; }

	public OpenMathBase handlePayload(OpenMathBase omb)
	throws OpenMathException
	{
		//OMB contains the actual call to addition as 1st argument, so we bypass that.
		OpenMathBase p = ((OMApply) omb).getParams()[0];
		OpenMathBase[] lst;

		//If the first argument is an integer, we treat the arguments as the arguments
		if (p.isInteger()) {
			lst = ((OMApply) omb).getParams();
		} else if (p.isApplication("list1", "list") || 
					p.isApplication("linalg2", "vector")) {
			lst = ((OMApply) p).getParams();
		} else {
			throw new OpenMathException("Invalid arguments to Addition");
		}

		//Construct the answer
		StringBuffer q = new StringBuffer();
		for ( int i = 0; i < lst.length; ++i ) {
			q.append( ( (OMInteger) lst[i]).getStrValue() );
		}
		OMInteger res = new OMInteger(q.toString());

		//Return the result
		return res;
	}
}
