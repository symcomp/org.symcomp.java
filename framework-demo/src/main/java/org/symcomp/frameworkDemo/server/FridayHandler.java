package org.symcomp.frameworkDemo.server;

import org.symcomp.scscp.*;
import org.symcomp.openmath.*;
import java.util.*;

public class FridayHandler extends ProcedureCallHandler {
	public String getServiceNameStr() { return "is_it_friday"; }
	public String getDescription(OMSymbol oms) { return "is_it_friday()"; }

	public OpenMathBase handlePayload(OpenMathBase omb) 
	throws OpenMathException
	{
		OMSymbol res;
		Calendar c = new GregorianCalendar();
		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
			res = new OMSymbol("logic1", "true");
		} else {
			res = new OMSymbol("logic1", "false");
		}

		return res;
	}
}
