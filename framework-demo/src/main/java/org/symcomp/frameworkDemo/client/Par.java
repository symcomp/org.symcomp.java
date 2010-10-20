package org.symcomp.frameworkDemo.client;

import org.symcomp.openmath.*;
import org.symcomp.scscp.*;
import java.util.Random;

public class Par extends SCSCPClient {
    protected Random m_rnd;
	protected final int NUMBER_OF_NUMBERS = 200;
	protected final int NUMBER_OF_DIGITS = 35;

    public Par(String host, int port) {
		super(host, port);

		loglevel = 10;
		m_rnd = new Random();
	}

	protected OMInteger randomInt(int nbytes) {
		char[] b = new char[nbytes];
		for (int i = 0; i < nbytes; ++i) b[i] = (char) ('0' + m_rnd.nextInt(10));
		return new OMInteger(new String(b));
	}

    public void demo() {
		//Construct the integers
		OMInteger[] ints = new OMInteger[NUMBER_OF_NUMBERS];
		for (int i = 0; i < NUMBER_OF_NUMBERS; ++i) ints[i] = randomInt(NUMBER_OF_DIGITS);
		System.out.println("Random numbers generated.");

		//Construct the question
		OMSymbol hd = new OMSymbol("spsd", "map");
		OMString[] systems = { 
		  new OMString("MuPAD"), 
		  new OMString("MuPADPro"), 
		  new OMString("GAP"), 
		  new OMString("Magma")
		};
		OpenMathBase arg1 = new OMApply(
			new OMSymbol("list1", "list"),
			systems
		);
		OpenMathBase arg2 = new OMApply(
			new OMSymbol("list1", "list"),
			ints
		);
		OpenMathBase arg3 = new OMSymbol("integer2", "euler");
		OpenMathBase[] args = { arg1, arg2, arg3 };

		//Wrap the question into an SCSCP-request
		OpenMathBase req = hd.apply(args);

		//Send the request to the server, and wait for the result
		System.out.println("Sending request:");
		String token = compute(req);
		System.out.println("Waiting for result...");
		while (!resultAvailable(token)) { 
			try { Thread.sleep(84); } catch(Exception e) {} 
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
			System.out.println("Suppressed result.");
		}
    }
}
