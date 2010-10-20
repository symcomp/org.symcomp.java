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

import java.util.Date;
import java.io.Reader;
import java.io.StringReader;

import org.symcomp.openmath.*;

/**
 * A Computation is a container that holds the request, the state and some meta-data of a computation.
 *
 * <p> It is internally used in SCSCPClient. There is no need to manually deal with these objects.
 *
 */
public class Computation {

	protected ProcedureCall procedureCall;
	protected ProcedureDone procedureDone;

    protected OpenMathBase result;
	protected OpenMathBase request;

	protected CookieStore cookies;
    protected String token;
	protected Date receivedAt;
	protected Date startedAt;
	protected Date finishedAt;
	protected OpenMathBase.ENCODINGS requestEncoding;
	protected Integer state = ComputationState.WAITING;
    protected CASClient system;
    protected final Integer lock = 0;

    public Computation() { this.token = "0"; }

	public Computation(CASClient system, String token, OpenMathBase command) {
		this.system = system;
		this.token = token;
		this.request = command;
	}
	
	public void setCookieStore(CookieStore c) { cookies = c; }
	public void setSystem(CASClient s) { system = s; }

	public void computing() {
        this.state = ComputationState.COMPUTING;
		this.startedAt = new Date();
	}




    public void finished(OpenMathBase result) {
        this.finishedAt = new Date();
        this.setResult(result);
        this.state = ComputationState.READY;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

	public void finished(String result) {
		this.finishedAt = new Date();
		this.setResult(result);
        this.state = ComputationState.READY;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

	public void error(OpenMathBase result) {
		this.finishedAt = new Date();
		this.setResult(result);
        this.state = ComputationState.ERRONEOUS;
        synchronized (lock) {
            lock.notifyAll();
        }
	}

	public void error(String result) {
		this.finishedAt = new Date();
		this.setResult(result);
        this.state = ComputationState.ERRONEOUS;
        synchronized (lock) {
            lock.notifyAll();
        }
	}

    public void waitForResult() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ignored) { }
        }
    }



	public boolean isReady() {
        return !this.state.equals(ComputationState.WAITING) && !this.state.equals(ComputationState.COMPUTING);
    }

    public String getToken() {
		return token;
	}
    public void setToken(String token) {
		this.token = token;
	}

	public Date getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(Date receivedAt) {
		this.receivedAt = receivedAt;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	public Date getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
        synchronized (lock) {
		    this.state = state;
            state.notifyAll();
        }
	}
	
	public OpenMathBase.ENCODINGS getRequestEncoding() {
		return requestEncoding;
	}

    public CASClient getSystem() {
        return this.system;
    }

	/**
	 * Set the return type of the Procedure call: cookie, option or nothing
	 */
	public void setReturn(ProcedureCall.OPTION_RETURN which) {
		getProcedureCall().setReturn(which);
	}

	/**
	 * Request the return type of the Procedure call
	 */
	public void getReturn() {
		getProcedureCall().getReturn();
	}

	/**
	 * Check whether the Procedure call has a particular return type
	 */
	public void hasReturn(ProcedureCall.OPTION_RETURN which) {
		getProcedureCall().hasReturn(which);
	}
	

	/**
	 * Get the computation as (OpenMath encoded) procedure call
     * @return the message
     */
	public ProcedureCall getProcedureCall() {
		if (procedureCall == null) {
			try {
				procedureCall = new ProcedureCall(this.token, this.request);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		procedureCall.setCookieStore(cookies);
		return procedureCall;
	}

	/**
	 * Get the result as (OpenMath encoded) ProcedureDone
     * @return the result message
     * @throws org.symcomp.openmath.OpenMathException if something went wrong
     */
	public ProcedureDone getProcedureDone()
	throws OpenMathException {

		if (this.procedureDone == null) {
			if (this.result == null) throw new RuntimeException("result not set.");

			if (this.state.equals(ComputationState.READY)) {
				procedureDone = new ProcedureDone(getProcedureCall(), ProcedureDone.PROCEDURE_COMPLETED, result);
			} else if (this.state.equals(ComputationState.ERRONEOUS)) {
				procedureDone = new ProcedureDone(getProcedureCall(), ProcedureDone.PROCEDURE_TERMINATED, result);
			} else {
				throw new RuntimeException("this.state != READY or ERRONEOUS, still trying to getResponseMessage");
			}
		}

		procedureDone.setCookieStore(cookies);
		return procedureDone;
	}




	public OpenMathBase getResult() {
		return result;
	}

    public void setResult(String result) {
        try {
            this.setResult(OpenMathBase.parse(result));
        } catch(Exception e) {
            this.setResult(new OMObject(new OMString(e.getMessage())));
        }
	}

    public void setResult(OpenMathBase result) {
		this.result = result;
	}

	public OpenMathBase getRequest() {
		return request;
	}

    public void setRequest(OpenMathBase request) {
		this.request = request;
	}



	public void setProcedureCall(ProcedureCall p) {
		this.procedureCall = p;
		this.setRequest(p.getPayload());
		this.receivedAt = new Date();
		this.state = ComputationState.WAITING;
	}

	public void setProcedureDone(ProcedureDone p) {
		this.procedureDone = p;
		if (p.which.equals(ProcedureDone.PROCEDURE_COMPLETED)) finished(p.getResult());
		if (p.which.equals(ProcedureDone.PROCEDURE_TERMINATED)) error(p.getResult());

	}

	/**
	 * Calls {@link #setProcedureCall(ProcedureCall p)} or
	 * {@link #setProcedureDone(ProcedureDone p)}, depending on the type of message b is.
     * @param b0 the message, enc the encoding the message was in (or null)
     * @throws org.symcomp.openmath.OpenMathException if the message has the wrong format
     */
	public void receivedMessage(OpenMathBase b0, OpenMathBase.ENCODINGS enc)
	throws OpenMathException {
		OpenMathBase b = b0.deOMObject();
		if (b.isApplication("scscp1", "procedure_call")) {
			setProcedureCall(new ProcedureCall(cookies, b));
			requestEncoding = enc;
		} else if (b.isApplication("scscp1", "procedure_completed")) {
			setProcedureDone(new ProcedureDone(this.getProcedureCall(), b));
		} else if (b.isApplication("scscp1", "procedure_terminated")) {
			setProcedureDone(new ProcedureDone(this.getProcedureCall(), b));
		} else {
			System.out.println(b.toPopcorn());
			throw new OpenMathException("Invalid message received");
		}
	}
	
	/**
	 * Calls {@link #setProcedureCall(ProcedureCall p)} or
	 * {@link #setProcedureDone(ProcedureDone p)}, depending on the type of message b is.
     * @param b0 the message
     * @throws org.symcomp.openmath.OpenMathException if the message has the wrong format
     */
	public void receivedMessage(OpenMathBase b0)
	throws OpenMathException {
		receivedMessage(b0, null);
	}	

	/**
	 * Calls {@link #setProcedureCall(ProcedureCall p)} or
	 * {@link #setProcedureDone(ProcedureDone p)}, depending on the type of message b is.
     * @param b0 the message as a string
     * @throws org.symcomp.openmath.OpenMathException if the message has the wrong format
     */
	public void receivedMessage(String m) 
	throws OpenMathException {
		Object[] o = OpenMathBase.sniffEncoding(new StringReader(m));
		OpenMathBase.ENCODINGS enc = (OpenMathBase.ENCODINGS) o[0];
		Reader r = (Reader) o[1];
		
		OpenMathBase b = enc.parse(r);
		receivedMessage(b, enc);
	}

}
