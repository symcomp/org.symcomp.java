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

package org.symcomp.openmath;

public class OMBinaryTest {

	public void testCreating() throws Exception {
		OpenMathBase b1 = new OMBinary("SGVsbG8sIHdvcmxkIQ==");
		
		OpenMathBase b2 = OpenMathBase.parse("%SGVsbG8sIHdvcmxkIQ==%");
		assert b2.equals(b1);

		OpenMathBase b3 = OpenMathBase.parse("<OMB>SGVsbG8sIHdvcmxkIQ==</OMB>");
		assert b3.equals(b1);
	}

	public void testRendering() throws Exception {
		OpenMathBase b1 = new OMBinary("SGVsbG8sIHdvcmxkIQ==");
		
		assert b1.toPopcorn().equals("%SGVsbG8sIHdvcmxkIQ==%");
		assert b1.toXml().equals("<OMB>SGVsbG8sIHdvcmxkIQ==</OMB>");
	}

	public void testBase64Enc() throws Exception {
		OMBinary b1 = new OMBinary("SGVsbG8sIHdvcmxkIQ==");
		String x = new String(b1.getByteValue());
		assert x.equals("Hello, world!");
	}

	public void testBase64Dec() throws Exception {
		String x = new String("Bye bye, world!");
		OpenMathBase b1 = new OMBinary(x.getBytes());
		assert b1.toPopcorn().equals("%QnllIGJ5ZSwgd29ybGQh%");
	}
	
	public void testBinaryRepr() throws Exception {
		OpenMathBase b1 = new OMBinary("SGVsbG8sIHdvcmxkIQ==");
		char[] c = b1.toBinary();
		OpenMathBase b2 = OpenMathBase.parseBinary(new String(c)).deOMObject();
		
		assert b1.equals(b2);
	}

}