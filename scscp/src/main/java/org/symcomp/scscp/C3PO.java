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

import org.symcomp.openmath.OpenMathBase;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * A class to encapsulate an SCSCP-Server.
 * To set up an SCSCP server that can compute mathematical expressions that are given in OpenMath form,
 * the SCSCP-protocol needs to be implemented. This class takes care of all the protocol stuff.
 * To set up an SCSCP server this class needs to be subclassed and the methods
 * <ul>
 * <li><code>compute</code>,</li>
 * <li><code>getServiceName</code>, and</li>
 * <li><code>getServiceVersion</code></li>
 * </ul>need to be overridden.
 * <p> Whilst the latter are self-explanatory, the <code>compute</code> method does the actual
 * work. Here, some call to an CAS/a Library or something similar should be inserted which
 * ought to translate the incomin OpenMath-xml string to another OpenMath-xml string.
 */
public abstract class C3PO implements Runnable, org.symcomp.scscp.SCSCPConstants {

	List<String> SUPPORTED_SCSCP_VERSIONS = new ArrayList<String>(); // in order of preference

    static JmDNS jmdns = null;

    protected Integer state;
    protected String scscpVersion;
    protected PrintWriter out;
    protected BufferedReader inn;
    protected String serviceId;
	protected LinkedList<String> waitingComputations;
    private String serviceName = null;
    private String serviceVersion = null;
    private String serviceDescription = null;
	protected static Integer loglevel = 1; 

    /**
	 * Construct a C3PO-instance that reads its iputs from <code>inn</code> and writes its
	 * results to <code>out</code>.
     * @param serviceId the name of the SCSCP Server
     * @param out the PrintWriter to which the SCSCP Server prints its output.
     * @param inn the BufferedReader from which the SCSCP Server reads its input.
	 *
	 */
	public C3PO(String serviceId, PrintWriter out, BufferedReader inn) {
        this.SUPPORTED_SCSCP_VERSIONS.add("1.4");
        this.SUPPORTED_SCSCP_VERSIONS.add("1.3");
        this.SUPPORTED_SCSCP_VERSIONS.add("1.2");

        this.serviceId = serviceId;
        this.out = out;
		this.inn = inn;
		this.waitingComputations = new LinkedList<String>();
    } // init

    public C3PO() {}

    /**
     * Does exactly the same as <code>spawn</code> but the listening on port
     * <code>port</code> is not done in a seperate thread, so that <code>breed</code>
     * blocks.
     * @param clazz the actual class whiss shall be instantiated
     * @param port the port on which the server shall listen
     */
    public static void breed(Class clazz, int port) throws Exception {
		SCSCPSpawner.breed(clazz, port);
    }

    /**
     * Listens on Port <code>port</code>, then for each incoming connection, it
     * instantiates Class <code>clazz</code> and starts it run-loop in a new thread.
     * <br>
     * <code>spawn</code> immediately returns, internally instanziating
     * <code>ScscspSpawner</code> and starting it th a new thread.
     * @param clazz the actual class whiss shall be instantiated
     * @param port the port on which the server shall listen
     */
    public static void spawn(Class clazz, int port) throws Exception {
		SCSCPSpawner.spawn(clazz, port);
    }

    /**
	 * The run-loop which is called for a session.
	 */
	public void run() {
		//initiate conversation with client

		try {
			state = STATE_NEGOTIATING;
			if (!negotiate()) {
				log(1, "C3PO: negotiate() failed.");
				cleanup();
				return;
			}

			state = STATE_WAITING;
			log(3, "C3PO: communicate()-ing");
			communicate();
			log(3, "C3PO: communicate() done, run() will finish soon.");
		} catch (Exception e) {
			//One example where we end up here is if the connection was closed.
			log(1, "C3PO::run(), caught exception: " + e.getMessage());
		}

		log(3, "C3PO: calling cleanup() ...");
		cleanup();
		log(3, "Done. Thread ends here.");
 	} // run

    /**
	 * The "destructor" that is called when communication is done, for any reason.
	 * This should be used in subclasses to clean up behind themselves.
	 */
	protected void cleanup() {}

	/**
	 * Private Method. Contains the state-machine that does the SCSCP-protocol talking
	 */
 	private void communicate() {
		String payload = "";
	  	String todo;
		boolean block;
		Pattern termpatt = Pattern.compile(PI_SCSCP_TERMPATT);
		Pattern infopatt = Pattern.compile(PI_SCSCP_INFOPATT);

		while (true) {
			//Receive data: Block if there are no computations left to do.
			// If, in that case, null is returned, something is wrong.
			block = (waitingComputations.size() == 0);
			todo = receive( block );
			if (block && (todo == null)) break;
		
			log(5, "todo = " + todo); 

			//If there are computations waiting, and there is no data on the line
			// we do a computation. Otherwise, we empty the line first.
			if (waitingComputations.size() > 0 && todo == null) {
				log(2, "doComputation()...");
				doComputation();
			}

			while (todo != null && todo.length() > 0) {
				// 1 - if the string contains ANY <?scscp quit ?> just terminate.
				if (todo.indexOf(PI_SCSCP_QUIT) != -1) {
					send (PI_SCSCP_QUIT);
					return;
				}

				// 1.1hack - if the string contains ANY <?scscp kill ?> just die.
				if (todo.indexOf(PI_SCSCP_KILL) != -1) {
					send (PI_SCSCP_KILL);
					return;
				}

				//STATE_WAITING: Remove all whitespace outside <?scscp ?> thingies.
				if (state.equals(STATE_WAITING)) {
					int i = todo.indexOf(PI_SCSCP_LEFT);
					if (i == -1) {
						todo = "";
					} else if (i != 0) {
						todo = todo.substring(i);
					}
				}

				//STATE_WAITING: Handle PI's.
				//WARNING -- We assume PIs end in a newline; otherwise they get deleted.
				if (state.equals(STATE_WAITING)) {
					boolean handled = false;
					int i;
					Matcher m;

					//<?scscp start ?>
					if ( (i = todo.indexOf(PI_SCSCP_START)) == 0) {
						todo = todo.substring(i+PI_SCSCP_START.length());
						state = STATE_INMESSAGE;
						handled = true;
					}

					//<?scscp terminate ?>
					m = termpatt.matcher(todo);
					if (m.matches() && m.start() == 0) {
						todo = todo.substring(m.end());
						receivedTerminateRequestInternal(m.group(1));
						handled = true;
					}

					//<?scscp info ?>
					m = infopatt.matcher(todo);
					if (m.matches() && m.start() == 0) {
						todo = todo.substring(m.end());
						receivedInfo(m.group(1));
						handled = true;
					}

					//Clean up, if needed.
					if (!handled) {
						int j = todo.indexOf(PI_SCSCP_RIGHT);
						if (j != -1) {
							todo = todo.substring(j + PI_SCSCP_RIGHT.length());
						} else {
							todo = "";
						}
					}
				}

				//STATE_INMESSAGE: We are consuming the input up to the <?scscp end ?>
				if (state.equals(STATE_INMESSAGE)) {
					int i;
					if ( (i = todo.indexOf(PI_SCSCP_CANCEL)) != -1) {
						payload = "";
						state = STATE_WAITING;
						todo = todo.substring(i + PI_SCSCP_CANCEL.length());
					} else {
						i = todo.indexOf(PI_SCSCP_END);
						if (i == -1) {
							payload += todo;
							todo = "";
						} else {
							if (i != 0) payload += todo.substring(0, i);
							state = STATE_MSG_COMPLETED;
							todo = todo.substring(i + PI_SCSCP_END.length());
						}
					}
				}

				// STATE_MSG_COMPLETED: Above the scscp-message was completed -- evaluate!
				if (state.equals(STATE_MSG_COMPLETED)) {
					waitingComputations.offer(payload);
					log(2, String.format("[C3PO] %d computations waiting...", waitingComputations.size()));
					state = STATE_WAITING;
					payload = "";
				}

			}//while (todo.length() > 0)
	
		}//while (receive / computations)

		log(2, "C3PO: communicate() done, I suppose I'll be finishing now.");

  	} // communicate

	/**
	 * Private method to do the SCSCP-version-negotiation
     * @return true if the negotiation was successful.
	 */
  	Boolean negotiate() {
		//First, send the "HELO"
		StringBuffer vrsns = new StringBuffer();
		for (String s : this.SUPPORTED_SCSCP_VERSIONS) {
            if (vrsns.length() > 0) vrsns.append(" ");
			vrsns.append(s);
        }
        send(String.format(PI_SCSCP_HELLO, this.getServiceName(), this.getServiceVersion(), serviceId, vrsns.toString()));

		//Wait for response
  		String inputLine;
  		if (this.SUPPORTED_SCSCP_VERSIONS.size() == 0) {
  			throw new RuntimeException("I don't support any SCSCP-versions. That doesn't seem reasonable.");
  		}
        scscpVersion = this.SUPPORTED_SCSCP_VERSIONS.get(0);
		inputLine = receive();
		if (inputLine == null) {
			//Session died on us, apparently.
			return false;
		}
		
		//See if we have a compatible version
		log(4, "negotiate(): Received '" + inputLine + "'");
        Pattern p = Pattern.compile(PI_SCSCP_VERPATT);
        java.util.regex.Matcher matcher = p.matcher(inputLine);
		if (!matcher.matches()) {
			send(PI_SCSCP_QUIT);
			send("I'm afraid you should send something like <?scscp version=\"${scscpVersion}\" ?>");
			return false;
		}
        String rv = (inputLine.split("\""))[1];
		if(!(SUPPORTED_SCSCP_VERSIONS.contains(rv))) {
			//Negotiation failed -- print some debug info:
			log(4, "negotiate(): Could not get a compatible version. We have: ");
			for(int i = 0; i < SUPPORTED_SCSCP_VERSIONS.size(); ++i) log(4, " * " + SUPPORTED_SCSCP_VERSIONS.get(i));
			log(4, "and they wanted " + rv);

			//And say something back.
			send(PI_SCSCP_QUIT);
	        String s = String.format(PI_SCSCP_INFO, "I'm afraid you asked for a version that I don't support.");
	        send(s);
			return false;
		} 
		
		//Negotiation successfull -> confirm
		scscpVersion = rv;
        String cv = String.format(PI_SCSCP_VERSION, rv);
        send(cv);
		log(4, "negotiate(): We have a compatible version: '" + cv + "'");
		
		//Say what encodings we support -- just to be sure only from version 1.4.
		//This is technically not needed (clients should ignore unknown pi's), but 
		//  some clients may still be confused.
		if (rv.compareTo("1.3") > 0) {
			StringBuffer encs = new StringBuffer();
			for (OpenMathBase.ENCODINGS s : OpenMathBase.ENCODINGS.values()) {
	            if (encs.length() > 0) encs.append(" ");
				encs.append(s.toString());
	        }
	        send(String.format(PI_SCSCP_ENCODINGS_SUPPORTED, encs));
		}
		
		return true;
  	} // negotiate

	/**
     * Private method is called from within <code>C3PO</code>s <code>run<code> command,
     * at a time suitable to execute a computation. It then calls <code>compute</code> for 
     * the first element of the waitingComputations queue and puts the result back on the line.
     */
    private void doComputation() {
		if (waitingComputations.size() == 0) return;

		String ret = compute(waitingComputations.poll());

		if (ret == null) {
			throw new RuntimeException("Could not compute. Sad thing.");
		}

		if (ret.length() > 0) {
			send(PI_SCSCP_START);
			send(ret);
			send(PI_SCSCP_END);
		}
	}

	/**
	 * Public method that does the actual computation.
	 * <p> This method needs to be overridden in any subclass.
	 * @param input a String that contains an OpenMath-xml string
	 * @return the result of the computation
	 */
  	public abstract String compute(String input);

	/**
	 * Method that is called when a terminate request is received; simply prints a message
	 * <p> This method may be overridden in a subclass.
	 * @param callID the callID of the call that could be terminated
	 */
  	protected void receivedTerminateRequest(String callID) {
		log(2, String.format("[C3PO] Terminate received for callID %s", callID));
	}

	/**
	 * Method that is called when a terminate request is received; simply prints a message
	 * <p> This method may be overridden in a subclass, but one needs to call 
     * super.receivedTerminateRequestInternal if that's done.
	 * @param callID the callID of the call that could be terminated
	 */
  	protected void receivedTerminateRequestInternal(String callID) {
		receivedTerminateRequest(callID);
	}

	/**
	 * Method that is called when scscp info is received; simply prints a message.
	 * <p> This method may be overridden in a subclass.
	 * @param s the string of info received
	 */
  	protected void receivedInfo(String s) {
		log(2, String.format("[C3PO] Info received: '%s'", s));
	}

	/**
	 * Private method to receive data from the SCSCP client.
	 * @param block whether the read is allowed to block
     * @return a chunk of data read from the InputStream, possibly null
     */
  	private String receive(boolean block) {
        String text = null;
        try {
			if (!block && !(inn.ready())) {
				text = null;
			} else if (!block) {
				//It might happen that, despite the socket being ready, readLine blocks.
				// for instance if there is some text, but no newline on the line.
				//Therefore, we invent this bad workaround.
				int limit = 2048;
				inn.mark(limit);
				char[] buf = new char[limit];
				inn.read(buf, 0, buf.length);
				String peeked = new String(buf);
				inn.reset();
				if (peeked.indexOf("\r") > 0 || peeked.indexOf("\n") > 0) {
					text = inn.readLine();
				} else {
					text = null;
				}
			} else {
	            text = inn.readLine();
			}
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
		return text;
  	} // receive

	/**
	 * Private method to (blockingly) receive data from the SCSCP client.
     * @return a chunk of data read from the InputStream
     */
  	private String receive() { return receive(true); }

	/**
	 * Private method to send data to the SCSCP client.
     * @param text the String to print into the PrintWriter
     */
  	private void send(String text) {
  		out.println(text);
		out.flush();
  	} // send

	/**
	 * Returns the name of the SCSCP-server.
	 * <p> This string is needed for the &lt;?scscp ?>-hello-message.
     * The value must be set via the ScscspServerInfo Annotation
	 * @return  the name of the SCSCP server
	 */
	public String getServiceName() {
        getInfosFromAnnotation();
        return serviceName;
    }

	/**
	 * Returns the version of the SCSCP-server.
	 * <p> This string is needed for the &lt;?scscp ?>-hello-message.
     * The value must be set via the ScscspServerInfo Annotation
	 * @return the version-number of the SCSCP server
	 */
    public String getServiceVersion() {
        getInfosFromAnnotation();
        return serviceVersion;
    }

    /**
     * Returns the description of the SCSCP-server.
     * The value must be set via the ScscspServerInfo Annotation
     * @return the description of the SCSCP server
     */
    public String getServiceDescription() {
        getInfosFromAnnotation();
        return serviceDescription;
    }


    /**
     * Used to get the values out of the SCSCPServerInfo Annotation
     */
    private void getInfosFromAnnotation() {
        if (null == serviceName) {
            SCSCPServerInfo ssi = (SCSCPServerInfo) this.getClass().getAnnotation(SCSCPServerInfo.class);
            if (null == ssi) {
                throw new RuntimeException("You must annotate your class '"+this.getClass().getCanonicalName()+
                        "' with Annotation 'SCSCPServerInfo' to use it.");
            }
            serviceName = ssi.serviceName();
            serviceVersion = ssi.serviceVersion();
            serviceDescription = ssi.serviceDescription();
            if (null == serviceName || null == serviceVersion || null == serviceDescription) {
                throw new RuntimeException("You must give all values for your class '"+
                        this.getClass().getCanonicalName()+"' with Annotation 'SCSCPServerInfo' to use it.");
            }
        }

    }

    /**
     * Used to announce the existence of this Server to the world
     * @param port The Port on which the server runs
     * @param name the name of the server
     * @param description some description
     * @return true if everything worked
     */
    public static boolean announceRendevouz(int port, String name, String description) {
       try {
           if (C3PO.jmdns == null) {
             C3PO.jmdns = JmDNS.create();
           }
           ServiceInfo info = ServiceInfo.create("_scscp._tcp.local.", name, port, description);
           jmdns.registerService(info);
       } catch (IOException e) {
           return false;
       }
       return true;
   }

	/**
	 * for logging, 0 is no output, 5 is a lot. Loglevel is static, so this has effect
	 *  troughtout all running C3PO's in the current application.
	 */
	public static void setLoglevel(Integer l) {
		loglevel = l;
	}
	

   /**
	 * just for logging, quick hack, to be replaced by something better.
	 */
	protected static void log(Integer l, String msg) {
		if (loglevel >= l) {
			System.out.println(String.format("[%s] %s", l, msg));
		}
	} 
	
   /**
	 * just for logging, quick hack, to be replaced by something better.
	 */
	protected static void logStackTrace(Integer l, Exception e) {
		if (loglevel >= l) {
			e.printStackTrace();
		}
	} 
}
