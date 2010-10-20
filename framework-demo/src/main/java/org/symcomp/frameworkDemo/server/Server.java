package org.symcomp.frameworkDemo.server;

import org.symcomp.scscp.*;

@SCSCPServerInfo(serviceName="JavaFrameworkDemo", serviceVersion="1.6-SNAPSHOT", serviceDescription="Demoes the SCIEnce Java Framework that can be used to create SCSCP compatible applications")
public class Server extends SCSCPServer {

    public Server(String serviceId, java.io.PrintWriter out, java.io.BufferedReader inn) {
        super(Server.class, serviceId, out, inn);
        this.addHandler(new AdditionHandler());
        this.addHandler(new OEISHandler());
        this.addHandler(new FridayHandler());
        this.addHandler(new IdentityHandler());
    }
}
