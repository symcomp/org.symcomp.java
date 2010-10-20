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

import org.symcomp.openmath.*;
import org.symcomp.notification.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.*;

/**
 * A class to encapsulate the access to an SCSCP-Server.
 * To use an SCSCP server to compute mathematical expressions that are given in OpenMath form,
 * the SCSCP-protocol needs to be implemented. This class takes care of all the protocol stuff.
 * To connect to an SCSCP-server running on machine www.somewhere.org on port 12345 just create
 * an instance of <code>SCSCPClient</code>:
 * <p>
 * <code>SCSCPClient client = new SCSCPClient("www.somewhere.org", 12345)</code>
 * <p>
 * Then, you can communicate with the client in a non-blocking way by use of
 * <ul>
 * <li><code>compute</code>,</li>
 * <li><code>resultAvailable</code> and</li>
 * <li><code>getResult</code></li>
 * </ul>
 * or in a blocking way by use of <code>computeBlocking</code>.
 * <p>
 * A typical scenario would be:
 * <pre>
 * SCSCPClient client = new SCSCPClient("127.0.0.1", 12345);
 * String token = client.compute("&lt;OMOBJ>...&lt;/OMOBJ>");
 * while (!client.resultAvailable(token))
 *   // do something reasonable with your time
 * String result = client.getResult(token);
 * // Now, use that result.
 * </pre>
 * Or, in the blocking case:
 * <pre>
 * SCSCPClient client = new SCSCPClient("127.0.0.1", 12345);
 * String result = client.computeBlocking("&lt;OMOBJ>...&lt;/OMOBJ>");
 * // Now, use that result.
 * </pre>
 */
public class SCSCPClient extends CASClient implements org.symcomp.scscp.SCSCPConstants {

  //=== Attributes ===
  protected String scscpUri;
  protected Integer scscpPort;
  protected Socket socket;
  protected BufferedReader inn;
  protected PrintWriter out;
  protected String scscpVersion;
  protected Integer loglevel;
  protected String serviceName;
  protected String serviceVersion;
  protected String serviceId;
    protected Integer state = CLIENT_UNINITIALIZED;
    private boolean shallQuit = false;

    static final String[] SUPPORTED_SCSCP_VERSIONS = new String[] { "1.4", "1.3", "1.2" };
  protected List<OpenMathBase.ENCODINGS> supportedEncodings = new ArrayList<OpenMathBase.ENCODINGS>();
  protected OpenMathBase.ENCODINGS activeEncoding;
  protected OpenMathBase.ENCODINGS defaultEncoding = OpenMathBase.ENCODINGS.XML;

    public SCSCPClient() {}

    public SCSCPClient(String uri, String port) {
      this(uri, Integer.parseInt(port));
    }


    /**
   * Creates an SCSCP-Client that connects to a server on the machine at <code>uri</code>
   * port <code>port</code>.
   *
   * <p>Directly after the creation of the object, the version-negotiation
   * (phase 1 of the SCSCP-protocol) is done.
   * @param uri The IP-address or the hostname of the machine where the SCSCP-server is running
   * @param port The port on which the SCSCP-Server is listening.
   */
  public SCSCPClient(String uri, Integer port) {
    loglevel = 0;

    setSupportedEncodings("");

        log (1, "#### Starting SCSCP-Client class for '"+uri+":"+port+"'");
        this.scscpUri = uri;
        this.scscpPort = port;
    computations = new HashMap<String,Computation>();
    waitingComputations = new ArrayBlockingQueue<Computation>(16383);
        log (1, "   # opening new sockets on " + scscpUri + ":" + scscpPort);
        try {
            socket = new Socket(uri, port);
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            this.inn = new BufferedReader(new InputStreamReader(is));
            this.out = new PrintWriter(os, true);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't open Socket " + scscpUri + ":" + scscpPort);
        }
        String vs = null;
        try {
            vs = receive();
        } catch (IOException e) {
            throw new RuntimeException("Reading from Socket failed.");
        }
        Pattern p = Pattern.compile(PI_SCSCP_HELOPATT);
        java.util.regex.Matcher matcher = p.matcher(vs);
    if (!matcher.matches()) {
      send(PI_SCSCP_QUIT);
      send("Didn't get expected system-identification-string\n" +
        "Format: <?scscp service_name=\"XXX\" service_version=\"YYY\" service_id=\"ZZZ\" scscp_versions=\"AAA\" ?>\n"+
        "Aborting.");
      throw new RuntimeException("Wrong SCSCP-system-id-string.");
    }
        String[] match = vs.split("\"");
     this.serviceName = match[1];
    this.serviceVersion = match[3];
    this.serviceId = match[5];
     this.scscpVersion = null;
     String[] offeredVersions = match[7].split(" ");
version_outer:
     for (String v : SUPPORTED_SCSCP_VERSIONS) {
            for (String o : offeredVersions) {
                if(o.equals(v)) {
                    this.scscpVersion = v;
                    Formatter formatter = new Formatter();
                    send(formatter.format(PI_SCSCP_VERSION, v).toString());
          break version_outer;
                 }
       }
     }
     if (null == this.scscpVersion) {
        send(PI_SCSCP_QUIT);
            Formatter formatter = new Formatter();
            send(formatter.format(PI_SCSCP_INFO, "I'm afraid we don;t talk the same language.").toString());
      throw new RuntimeException("No matching version found.");
     }
    log (1, "   # Got service-name: '" + this.serviceName + "'; version: '" + this.serviceVersion + "'; id: '" + this.serviceId + "'");
    log (1, "   # Got scscp-version: '" + this.scscpVersion + "'");
        state = CLIENT_IDLE;
        resultThread = new Thread(this);
        resultThread.start();
    }

  //=== Methods for external use ===

  /**
   * Starts a Computation
   *
   * <p>The Computation <code>comp</code> is sent
   * to the attaced SCSCP-server. The function call returns immediately, returning a token
   * which can be used to get the result using <code>getResult</code>.
   * @param comp a Computation
   * @return A string that is used to identify the call.
   */
  public String startComputation(Computation comp) {
        log (3, "   # Computation object constructed. Inserting into queue...");
        this.computations.put(comp.getToken(), comp);
        waitingComputations.offer(comp);
        log (3, "   # Computation inserted into waitingComputations!");
    return comp.getToken();
  } // compute



  /**
   * Computes an OpenMath-expression, non-blocking.
   *
   * <p>The OpenMath expression represented by the parameter <code>omCommand</code> is wrapped
   * in a Computation and sent to the attached SCSCP-server. The function call returns
   * immediately, returning a token which can be used to get the result using <code>getResult</code>.
   * @param omCommand an OpenMath expression
   * @return A string that is used to identify the call.
   */
  public String compute(OpenMathBase omCommand) {
    log (3, "   # Constructing Computation object...");
    String token = String.format("%03d-%d@%s[%s]%s", computations.size()+1, (new Date()).getTime(), this.serviceName, this.serviceVersion, this.serviceId);
       Computation comp = new Computation(this, token, omCommand);
    return startComputation(comp);
  } // compute

  /**
   * Computes an OpenMath-expression, blocking.
   *
   * <p>The OpenMath expression represented by the parameter <code>omCommand</code> is sent
   * to the attaced SCSCP-server. The function call returns after the computation has finished
   * returning an xml-string
   * <p> Note, that this computation may take extremely long, so only use this call if there is
   * really nothing you want to do in the meantime.
   * @param omCommand an OpenMath expression
   * @return An OpenMathBase object containing the OpenMath object computed by the SCSCP-Server.
   */
  public OpenMathBase computeBlocking(OpenMathBase omCommand) {
    String token = this.compute(omCommand);
    Computation comp = computations.get(token);
    while (!comp.getState().equals(ComputationState.READY)) {
            comp.waitForResult();
        }
        return this.getResult(token);
  } // computeBlocking

  /**
   * Retrieves the result of a computation.
   *
   * <p> Using the <code>token</code> returned by <code>compute()</code>, this function is used
   * to retrieve the actual result. If the result is not yet ready, <code>null</code> is
   * returned.
   * <p> If a non-token is given, an Exception is raised.
   * @param token A string that was created using <code>compute()</code>
   * @return An OpenMathBase tree containing the OpenMath-xml computed by the SCSCP-Server.
   */
  public OpenMathBase getResult(String token) {
    Computation comp = computations.get(token);
    if (comp != null && comp.getState().equals(ComputationState.READY)) {
      return comp.getResult().deOMObject();
    } else if (comp  != null && comp.getState().equals(ComputationState.ERRONEOUS)) {
      return comp.getResult().deOMObject();
    } else if (comp != null) {
      return null;
    }
    throw new RuntimeException("Undefined Token '"+token+"'.");
  } // getResult

  /**
   * Checks whether a given computation has finished.
   *
   * <p> Using the <code>token</code> returned by <code>compute()</code>, this function is used
   * to find out, whether the SCSCP-server has finished its computation.
   * <p> If a non-token is given, an Exception is raised.
   * @param token a string that was created using <code>compute()</code>
   * @return ready  <code>true</code> if the computation is ready, <code>false</code> otherwise.
   */
  public Boolean resultAvailable(String token) {
    Computation comp = computations.get(token);
    if (comp != null && (comp.getState().equals(ComputationState.READY) || comp.getState().equals(ComputationState.ERRONEOUS))) {
      return true;
    } else if (comp != null && !comp.getState().equals(ComputationState.READY)) {
      return false;
    }
    throw new RuntimeException("Undefined Token '"+token+"'.");
  }

  /**
     * Get the state of a particular computation.
     *
     * <p>Using the <code>token</code> returned by <code>compute()</code>, this function can be
     * used to find out what state this computation is in.
   * <p> If a non-token is given, an Exception is raised.
   * @param token A string that was created using <code>compute()</code>
   * @return The state of the Computation
     */
     public Integer getComputationState(String token) {
    Computation c = computations.get(token);
    if (c != null) {
      return c.getState();
    }
    throw new RuntimeException("Undefined Token '"+token+"'.");
    }

    public void quit() {
        this.shallQuit = true;
        waitingComputations.offer(new Computation());
    }

    private void quitt(Integer qstate) {
        try {
            send(PI_SCSCP_QUIT);
      socket.close();
        } catch(Exception e) { }
        this.state = qstate;
        log(1, "QUIT");
    }

    //=== internally used Methods ===

  /**
   * Private method to receive data from the SCSCP server
   * @param timeout A timeout in milliseconds to wait for the read.
     * @return A String that came in via the socket
     * @throws java.io.IOException if the sockets spits
     */
  private String receive(int timeout) throws IOException {
    if (socket.getSoTimeout() != timeout) socket.setSoTimeout(timeout);
        String text = null;
     try {
      text = inn.readLine();
    } catch (SocketTimeoutException e) {}
        if (text != null) log(4, "<--- "+text);
        return text;
    } // receive

  /**
   * Private method to receive data from the SCSCP server
     * @return A String that came in via the socket
     * @throws java.io.IOException if the sockets spits
     */
  private String receive() throws IOException {
    return receive(0);
    } // receive

  /**
   * Private method to send data to the SCSCP server
     * @param text Data sent down the Socket
     */
  private void send(String text) {
        log(4, "---> "+text);
    out.println(text);
  } // send

  //=== Method overridden from Runnable ===
  /**
   * Private method, does the actual background-communication. Inherited from Runnable.
   */
  public void run() {
        NotificationCenter.getNC().sendNotification(this, "RUNNING", null);
        // initialization
    log (1, "   # started waiting-for-result-thread ");
    // run-loop
        try {
      Pattern encPatt = Pattern.compile(PI_SCSCP_ENCODINGS_SUPPORTED_PATT);

      //Infinite loop, with the following ingredients:
      //  * See if we should quit
      //  * Read from the socket:
      //    & handle any available data
      //  * If there is no current computation,
      //    -> try to find a new one
      //    & push it down the line.
            Integer cstate = STATE_FINISHED;
      StringBuffer result = new StringBuffer();
            while (true) {
        /* See if we should quit */
                if (this.shallQuit) {
                    log(4, "Servus!");
                    quitt(CLIENT_QUIT);
                    break;
                }

        /* Do this to check that the connection is still alive */
                out.print(' '); out.flush();

        /* Read from the socket */
        String inputLine;
        try {
                  inputLine = receive(1000);
        } catch (Exception e) {
          log(1, "  # Exception during receive: " + e.getMessage());
                    //e.printStackTrace();
                    quitt(CLIENT_DEAD);
                    break;
        }

        /* Handle the data */
                // 1 - if the string contains ANY <?scscp quit ?> just terminate.
                if (inputLine != null && inputLine.indexOf(PI_SCSCP_QUIT) != -1) {
                    log (1, "   # Found <?scscp quit ?>-symbol, terminating.");
                    quitt(CLIENT_QUIT);
                    break;
                }
        // 1b - supported encodings is something we want to work with
        if (inputLine != null && !(cstate.equals(STATE_INMESSAGE))) {
              java.util.regex.Matcher matcher = encPatt.matcher(inputLine);
          if (matcher.matches()) setSupportedEncodings(matcher.group(1));
        }
                // 2 - if we are in trashing mode, just search for a <?scscp begin ?>
                if (inputLine != null && cstate.equals(STATE_WAITING)) {
                    Integer i = inputLine.indexOf(PI_SCSCP_START);
                    if (i != -1) {
                        log (1, "   # Found <?scscp start ?>-symbol, beginning to scan the result.");
                        if (i+PI_SCSCP_START.length() == inputLine.length()) {
                            inputLine = "";
                        } else {
                            inputLine = inputLine.substring(i+PI_SCSCP_START.length());
                        }
                        cstate = STATE_INMESSAGE;
                    }
                }
                // 3 - we are waiting and consuming the input up to the <?scscp end ?>
                if (inputLine != null && cstate.equals(STATE_INMESSAGE)) {
                    if (-1 != inputLine.indexOf(PI_SCSCP_CANCEL)) {
                        log (1, "   # Found <?scscp cancel ?>-symbol. Canceling this session.");
                        result = new StringBuffer("");
                        cstate = STATE_WAITING;
                    } else {
                        Integer i = inputLine.indexOf(PI_SCSCP_END);
                        if (i == -1) {
                            result.append(inputLine);
                        } else {
                            log (1, "   # Found <?scscp end ?>-symbol, computation done.");
                            if (i>0) result.append(inputLine, 0, i-1);
                            cstate = STATE_MSG_COMPLETED;
                            send(PI_SCSCP_ACK);
                        }
                    }
                }

        //Maybe just above we finished the computation,
        //in which case we should save the result
        if (cstate.equals(STATE_MSG_COMPLETED)) {
          String sresult = result.toString();
                  log (1, "   # computed result: " + sresult);
          currentComputation.receivedMessage(OpenMathBase.parse(sresult));

                  HashMap<String, Object> data = new HashMap<String, Object>();
                  data.put("token", currentComputation.getToken());
                  data.put("result", currentComputation.getResult());

                  NotificationCenter.getNC().sendNotification(this, "ComputationFinished", data);
                  currentComputation = null;

          cstate = STATE_FINISHED;
          this.state = CLIENT_IDLE;
        }

        /* See if there is a new computation we should fire off */
        if (cstate.equals(STATE_FINISHED)) {
          //Try to get a new computation -- but don't wait too long.
                  currentComputation = ((ArrayBlockingQueue<Computation>) waitingComputations).poll(1, TimeUnit.SECONDS);

          if (currentComputation != null) {
                    this.state = CLIENT_COMPUTING;
            result = new StringBuffer("");

                    // pipe next command into CAS.
            //(Everything that goes into waitingComputations should be a procedure call)
                    currentComputation.computing();
                    log(4, "   # Converting to "+ activeEncoding.toString() + "...");
                    String cmd = activeEncoding.render(currentComputation.getProcedureCall().getOMObject());

                    log(4, "   # ProcedureCall converted to "+ activeEncoding.toString()+". Sending...");
                    send(PI_SCSCP_START);
                    send(cmd);
                    send(PI_SCSCP_END);
                    log(4, "   # Sent.");
                    log(2, "   # Computing...");
            cstate = STATE_WAITING;
          }

        }

            } //End of the big while loop.
        } catch (Exception e) {
            log (1, "[SCSCPClient] I died :(");
            quitt(CLIENT_DEAD);
        }
    log (1, "   # Died.");
        NotificationCenter.getNC().sendNotification(this, "DEAD", null);
    } // run

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getSCSCPUri() {
        return scscpUri;
    }

    public Integer getSCSCPPort() {
        return scscpPort;
    }

    public String getSCSCPVersion() {
        return scscpVersion;
    }

    public Integer getState() {
        return state;
    }

  /**
   * encoding business
   */
  protected void setSupportedEncodings(String str) {
    String[] encs = str.split(" ");

    //Put encodings in list
    supportedEncodings.clear();
    for (String es : encs) {
      OpenMathBase.ENCODINGS e = OpenMathBase.ENCODINGS.get(es);
      if (e == null) {
        log(1, "  # Unrecognized encoding: " + es);
      } else {
        log(2, "  # Supported encoding: " + es);
        supportedEncodings.add(e);
      }
    }

    //Set a suitable activeEncoding
    if (activeEncoding == null && supportedEncodings.size() == 0) {
      //No supported encodings (known) -> revert to default
      activeEncoding = defaultEncoding;
    } else if (activeEncoding == null && supportedEncodings.size() > 0) {
      //Revert to first encoding
      activeEncoding = supportedEncodings.get(0);
    } else if (activeEncoding != null &&  supportedEncodings.size() > 0) {
      //Check if activeEncoding is in the supportedEncodings; if not, revert to first of list.
      if (supportedEncodings.indexOf(activeEncoding) == -1) {
        activeEncoding = supportedEncodings.get(0);
      }
    }
    log(2, "  # activeEncoding set to " + activeEncoding.toString());
  }

  public List<OpenMathBase.ENCODINGS> getSupportedEncodings() {
    return supportedEncodings;
  }
  public OpenMathBase.ENCODINGS getActiveEncoding() {
    return activeEncoding;
  }
  /**
   * Private method to send data to the SCSCP server
     * @param enc The requested encoding
     * @param force Whether the encoding should be set even if it was not (known to be) supported.
   * @return Whether the required enc was acivated.
     */
  public boolean setActiveEncoding(OpenMathBase.ENCODINGS enc, boolean force) {
    boolean valid = (supportedEncodings.indexOf(enc) != -1);
    if (valid) {
      activeEncoding = enc;
      return true;
    } else if (!valid && force) {
      log(2, "  # Setting non-supported activeEncoding");
      activeEncoding = enc;
      return true;
    } else if (!valid && !force) {
      log(2, "  # Refusing to set non-supported activeEncoding");
      return false;
    } else {
      //this is no case
      return false;
    }
  }

    /**
   * just for logging, quick hack, to be replaced by something better.
   */
  protected void log(Integer l, String msg) {
    if (loglevel >= l) {
      System.out.println(String.format("[%s] %s", l, msg));
    }
  } // log

  public void setLoglevel(Integer l) {
    loglevel = l;
  }

}
