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

import java.util.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

/**
 * A class implementing spawning and breeding of an SCSCP server, i.e making
 * sure it keeps running. This is called from <code>C3PO</code> and hence from
 * <code>SCSCPServer</code>, and is intended to be used from there, by using
 * e.g. <code>SCSCPServer.spawn</code>.
 */
public class SCSCPSpawner implements Runnable {

    static Map<Integer, List<C3PO>> ports = new HashMap<Integer, List<C3PO>>();
    private Class clazz;
    private int port;
	
    private SCSCPSpawner(Class clazz, int port) {
        this.clazz = clazz;
        this.port = port;

        if (ports.get(port) != null) {
            log(0, "BEWARE: spawn may be called just once per port.");
        }
        ports.put(port, new ArrayList<C3PO>());
    }


    private String idGen(int len) {
        StringBuffer r = new StringBuffer();
        Random rng = new Random();
        while(r.length() < len) {
            r.append(Long.toHexString(rng.nextLong()));
        }
        return r.toString();
    }

	/**
	 * Basic logging, quick hack. Uses C3PO.log, and his (static) loglevel.
	 */
	protected static void log(Integer l, String msg) {
		C3PO.log(l, msg);
	}

    public static void spawn(Class clazz, int port) throws Exception {
        spawn(clazz, port, false);
    }

    public static void breed(Class clazz, int port) throws Exception {
        spawn(clazz, port, true);
    }

    private static void spawn(Class clazz, int port, boolean blocking) throws Exception {
        SCSCPSpawner spawner = new SCSCPSpawner(clazz, port);

        // create unique ID
        InetAddress addr = null;
        addr = InetAddress.getLocalHost();

        // Get hostname and other information
        assert addr != null;
        String hostname = addr.getCanonicalHostName();
        String serviceName;
        String serviceVersion;
        SCSCPServerInfo ssi = (SCSCPServerInfo) clazz.getAnnotation(SCSCPServerInfo.class);
        if (null == ssi) {
            throw new RuntimeException("You must annotate your class '"+clazz.getCanonicalName()+"' with Annotation 'SCSCPServerInfo' to use spawn.");
        }
        serviceName = ssi.serviceName();
        serviceVersion = ssi.serviceVersion();
        String id = String.format("%s[%s]@%s", serviceName, serviceVersion, hostname);

		// Announce to Rendevouz
        boolean ok = C3PO.announceRendevouz(port, id, "This is an SCSCP Server.");
		log(2, "Announcing to ZeroConf: " + (ok ? "SUCCESS" : "FAILURE"));

		// Start the socket listener
        if (blocking) {
            spawner.run();
        } else {
            Thread runner = new Thread(spawner);
            runner.start();
        }
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            List<C3PO> clients = ports.get(this.port);
            Constructor c = clazz.getConstructor( String.class, PrintWriter.class, BufferedReader.class  );

			log(1, String.format("You may now SCSCP-connect on port %s.", port));
            while (true) {
				log(4, "Waiting for connection...");

                Socket clientSocket = serverSocket.accept();

				log(2, String.format("Client %s connected.", clientSocket.getRemoteSocketAddress()));

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader inn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				log(4, "Starting C3PO...");

                C3PO c3po = (C3PO) c.newInstance(idGen(48), out, inn);
                clients.add(c3po);
                Thread t = new Thread(c3po);
                t.start();

				log(4, "C3PO thread started.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
