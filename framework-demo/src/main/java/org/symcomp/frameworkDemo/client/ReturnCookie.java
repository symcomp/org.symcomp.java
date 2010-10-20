package org.symcomp.frameworkDemo.client;

import java.util.Date;
import org.symcomp.openmath.*;
import org.symcomp.scscp.*;

public class ReturnCookie extends SCSCPClient {

    public ReturnCookie(String host, int port) {
		super(host, port);
		loglevel = 10;
	}

    public void demo() throws Exception
	{
		//Construct the question
		OpenMathBase question = OpenMathBase.parse("2+3");
		
		//Construct Computation object, adapt return option
		//(Slightly contrived -- will be improved in the next version (hopefully))
		String token = String.format("%03d-%d@%s[%s]%s", computations.size()+1, (new Date()).getTime(), this.serviceName, this.serviceVersion, this.serviceId);
	   	Computation comp = new Computation(this, token, question);
		comp.setReturn(ProcedureCall.OPTION_RETURN.COOKIE);
		
		//Send the request to the server, and wait for the result
		startComputation(comp);
		System.out.println("Waiting for result...");
		while (!comp.getState().equals(ComputationState.READY)) {
            comp.waitForResult();
        }

		//Print the result.
		OpenMathBase res = getResult(token);
		if (res == null) {
			System.out.println("\n'null' returned");
		} else {
			System.out.println("\nRESULT:\n" + res.toXml());
		}
		
		//Done.
		quit();
    }
}

