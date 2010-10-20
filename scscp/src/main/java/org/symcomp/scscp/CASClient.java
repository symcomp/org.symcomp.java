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

import java.util.Queue;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.symcomp.openmath.*;

public class CASClient implements Runnable {

	protected String id;
	protected Map<String, Computation> computations;
	protected Queue waitingComputations;
	protected Computation currentComputation;
	protected Thread resultThread;

	public String compute(OpenMathBase omCommand) {
		throw new RuntimeException("Implement me!");
	}

    public Computation getComputation(String token) {
        return this.computations.get(token);
    }

    public List<Computation> getComputations() {
        return new ArrayList<Computation>(computations.values());
    }

    public Integer getNumberOfComputations() {
        return computations.size();
    }

    public OpenMathBase getResult(String token) {
		Computation comp = computations.get(token);
		if (comp == null) {
			throw new RuntimeException("Undefined Token ${token}.");
		}
		if (comp.getState().equals(ComputationState.READY) || comp.getState().equals(ComputationState.ERRONEOUS)) {
			return comp.getResult();
		}
        return null;
    }

	public Boolean resultAvailable(String token) throws Exception {
		Computation comp = computations.get(token);
		if (comp == null) {
			throw new Exception("Undefined Token ${token}.");
        }
		if(comp.getState().equals(ComputationState.READY) || comp.getState().equals(ComputationState.ERRONEOUS)) {
			return true;
		}
		return false;
	}


    public Boolean isIdle() {
        if (null == currentComputation && waitingComputations.size() == 0)
            return true;
        return false;
    }

    public void run() {
		throw new RuntimeException("Implement me!");
	}

}
