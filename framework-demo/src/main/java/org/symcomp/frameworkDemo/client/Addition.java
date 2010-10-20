package org.symcomp.frameworkDemo.client;

import org.symcomp.openmath.*;
import org.symcomp.scscp.*;

public class Addition extends SCSCPClient {

    public Addition(String host, int port) {
		super(host, port);
		loglevel = 10;
	}

    public void demo() {
		//Construct the question
		OMSymbol hd = new OMSymbol("scscp_transient_1", "addition");
		OMInteger[] ints = { new OMInteger(2), new OMInteger(3) };
		OMApply arg = new OMApply(
			new OMSymbol("list1", "list"),
			ints
		);
		OMApply[] args = { arg };

		OpenMathBase question = new OMApply(hd, args);
		
		//Send the request to the server, and wait for the result
		System.out.println("Sending request:");
		System.out.println(question.toXml());;
		String token = compute(question);
		System.out.println("Waiting for result...");
		while (!resultAvailable(token)) { 
			try { Thread.sleep(80); } catch(Exception e) {} 
		}

		//Print the state
		Integer st = getComputationState(token);
		if (st == ComputationState.WAITING) System.out.println("ComputationState: WAITING");
		if (st == ComputationState.COMPUTING) System.out.println("ComputationState: COMPUTING");
		if (st == ComputationState.READY) System.out.println("ComputationState: READY");
		if (st == ComputationState.ERRONEOUS) System.out.println("ComputationState: ERRONEOUS");

		//Print the result.
		OpenMathBase res = getResult(token);
		if (res == null) {
			System.out.println("'null' returned");
		} else {
			System.out.println(res.toXml());
		}
    }
}

