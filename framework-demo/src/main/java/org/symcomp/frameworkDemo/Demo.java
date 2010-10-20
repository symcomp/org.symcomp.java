package org.symcomp.frameworkDemo;

import org.symcomp.openmath.*;
import org.symcomp.frameworkDemo.server.*;
import org.symcomp.frameworkDemo.client.*;

public class Demo {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage:\n  client <whichclient> <host> <port>\nor\n  server <port>\n");
			System.out.println("Here whichclient may be one of: \"addition\", \"par\", \"wpar\".");
            return;
        }
        
        if (args[0].equals("server")) {
            //Server demo
            int port = (new Integer(args[1])).intValue();
            
			org.symcomp.scscp.SCSCPServer.setLoglevel(4);
            Server.spawn(Server.class, port);   // returns immediately
            Server.breed(Server.class, port+1); // blocks
        } else if (args[0].equals("client")) {
            //Client demo
			String whichcl = args[1];
			String host = args[2];
			int port = (new Integer(args[3])).intValue();
			whichcl = whichcl.toLowerCase();
	   	
			if (whichcl.equals("addition")) {
				(new Addition(host,port)).demo();
			} else if (whichcl.equals("par")) {
				(new Par(host,port)).demo();
			} else if (whichcl.equals("returncookie")) {
				(new ReturnCookie(host,port)).demo();
            } else if (whichcl.equals("wpar")) {
                (new WupsiPar(host,port)).demo();
			} else {
            	System.out.println("Unknown client demo: " + whichcl);
			}

			return;
        } else {
            System.out.println("Unknown action: " + args[1]);
            return;
        }
    }
}