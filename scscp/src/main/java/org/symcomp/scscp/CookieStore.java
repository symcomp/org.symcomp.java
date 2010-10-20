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
import java.util.Hashtable;
import java.util.Random;

/**
 * The CookieStore is simply a Hashtable used to store (remote) objects.
 * It is used by the <code>SCSCPServer</code> and <code>SCSCP2RemoteObjectsHandler</code>,
 * there should be no need to handle it manually.
 */
public class CookieStore extends Hashtable<String,OpenMathBase> {
	private Random rng;
	private int counter;

	public CookieStore() {
		super();
		rng = new Random();
		counter = 0;
	}

	protected String randomKey() {
		/**
		 *	From the documentation of java.util.Random: The algorithms implemented
		 *	by class Random use a protected utility method that on each invocation
		 *	can supply up to 32 pseudorandomly generated bits.
		 *
		 *	So we do this several times in order to get something that is not too
		 *	easily guessable.
		 */
		StringBuffer r = new StringBuffer("scscp://localhost:26133/");
		while(r.length() < 128) {
			r.append(Long.toHexString(rng.nextLong()));
		}
		return r.toString();
	}

	/**
     * Stores under a random key.
	 * Returns OMReference containing key where the object was stored.
	 */
	public OMReference store(OpenMathBase o) {
		String key;
		do {
			key = randomKey();
		} while ( this.get(key) != null );

		this.put(key, o);

		return new OMReference(key);
	}

	/**
     * Tries to retrieve.
	 * Returns the OpenMathBase object if the specified reference was in the store,
	 * or null otherwise.
	 */
	public OpenMathBase retrieve(OMReference r) {
		String key = r.getHref();
		Object val = this.get(key);
		if (val == null) return null; else return (OpenMathBase) val;
	}

	/**
     * Tries to remove.
	 * Returns true if something was removed, false otherwise.
	 */
	public boolean unbind(OMReference r) {
		String key = r.getHref();
		return ( (this.remove(key)) != null );
	}

};

