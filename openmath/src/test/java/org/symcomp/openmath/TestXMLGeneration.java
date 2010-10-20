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

import java.io.*;
import java.nio.charset.Charset;

public class TestXMLGeneration {

	static String[] omxmls = {
        "<OMS id=\"123\" cd=\"arith1\" name=\"plus\" />",
        "<OMF dec=\"1.1\" />",
        "<OMA><OMS id=\"123\" cd=\"arith1\" name=\"plus\" /><OMI>1</OMI><OMI>2</OMI></OMA>",
        "<OMATTR><OMATP><OMSTR>Fallerie und Fallera</OMSTR><OMI>1</OMI><OMS id=\"1238\" cd=\"arith1\" name=\"minus\" /><OMI>2</OMI></OMATP><OMS id=\"123\" cd=\"arith1\" name=\"mult\" /></OMATTR>",
        "<OMBIND><OMS cd=\"fns1\" name=\"lambda\"/><OMBVAR><OMV name=\"x\"/><OMV name=\"y\"/></OMBVAR><OMA><OMS cd=\"transc1\" name=\"sin\"/><OMA><OMS cd=\"arith1\" name=\"plus\"/><OMV name=\"x\"/><OMV name=\"y\"/></OMA></OMA></OMBIND>",
        "<OMOBJ><OMA><OMS id=\"123\" cd=\"arith1\" name=\"plus\" /><OMI>1</OMI><OMI>2</OMI></OMA></OMOBJ>",
        "<OMOBJ><OMA><OMS id=\"123\" cd=\"arith1\" name=\"plus\" /><OMI id=\"777\">1</OMI><OMI>2</OMI><OMR href=\"777\" /></OMA></OMOBJ>",
        "<OMOBJ><OMA><OMI id=\"777\">1</OMI><OMI>2</OMI><OMR href=\"777\" /></OMA></OMOBJ>",
        "<om:OMOBJ><om:OMA><om:OMI id=\"777\">1</om:OMI><om:OMI>2</om:OMI><om:OMR href=\"777\" /></om:OMA></om:OMOBJ>",
        "<?xml version=\"1.0\"?><OM:OMOBJ xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openmath.org/OpenMath http://www.openmath.org/standard/relaxng/openmath1.xsd\" xmlns:OM=\"http://www.openmath.org/OpenMath\"> <OM:OMA> <OM:OMS name=\"DMP\" cd=\"polyd1\"/><OM:OMA> <OM:OMS name=\"poly_ring_d\" cd=\"polyd1\"/> <OM:OMS name=\"Z\" cd=\"setname1\"/> <OM:OMI>1</OM:OMI> </OM:OMA> <OM:OMA> <OM:OMS name=\"SDMP\" cd=\"polyd1\"/> <OM:OMA> <OM:OMS name=\"term\" cd=\"polyd1\"/> <OM:OMI> 1 </OM:OMI> <OM:OMI> 2 </OM:OMI> </OM:OMA> <OM:OMA> <OM:OMS name=\"term\" cd=\"polyd1\"/> <OM:OMI> 2 </OM:OMI> <OM:OMI> 0 </OM:OMI> </OM:OMA> </OM:OMA> </OM:OMA> </OM:OMOBJ>",
        "<OMOBJ><OME><OMS id=\"123\" cd=\"error\" name=\"some_name\" /><OMI>1</OMI><OMSTR>Trallala</OMSTR></OME></OMOBJ>",
        "<OME><OMS cd=\"error-cd\" name='eror-symbol' /><OMSTR>So ein Mist!</OMSTR></OME>"
    };

	
	public void testGeneratedXMLEquality0() { assert testGeneratedXMLEquality(omxmls[0]); }
	public void testGeneratedXMLEquality1() { assert testGeneratedXMLEquality(omxmls[1]); }
	public void testGeneratedXMLEquality2() { assert testGeneratedXMLEquality(omxmls[2]); }
	public void testGeneratedXMLEquality3() { assert testGeneratedXMLEquality(omxmls[3]); }
	public void testGeneratedXMLEquality4() { assert testGeneratedXMLEquality(omxmls[4]); }
	public void testGeneratedXMLEquality5() { assert testGeneratedXMLEquality(omxmls[5]); }
	public void testGeneratedXMLEquality6() { assert testGeneratedXMLEquality(omxmls[6]); }
	public void testGeneratedXMLEquality7() { assert testGeneratedXMLEquality(omxmls[7]); }
	public void testGeneratedXMLEquality8() { assert testGeneratedXMLEquality(omxmls[8]); }
	public void testGeneratedXMLEquality9() { assert testGeneratedXMLEquality(omxmls[9]); }
	public void testGeneratedXMLEquality10() { assert testGeneratedXMLEquality(omxmls[10]); }
	public void testGeneratedXMLEquality11() { assert testGeneratedXMLEquality(omxmls[11]); }

	public void testInvariance0() { assert testInvariance(omxmls[0]); }
	public void testInvariance1() { assert testInvariance(omxmls[1]); }
	public void testInvariance2() { assert testInvariance(omxmls[2]); }
	public void testInvariance3() { assert testInvariance(omxmls[3]); }
	public void testInvariance4() { assert testInvariance(omxmls[4]); }
	public void testInvariance5() { assert testInvariance(omxmls[5]); }
	public void testInvariance6() { assert testInvariance(omxmls[6]); }
	public void testInvariance7() { assert testInvariance(omxmls[7]); }
	public void testInvariance8() { assert testInvariance(omxmls[8]); }
	public void testInvariance9() { assert testInvariance(omxmls[9]); }
	public void testInvariance10() { assert testInvariance(omxmls[10]); }
	public void testInvariance11() { assert testInvariance(omxmls[11]); }

	public void testFileIO() { 
		for (int i = 0; i < omxmls.length; ++i) {
			assert testFileIO(omxmls[i]);
		}
	}
	
	public boolean testGeneratedXMLEquality(String o1) {
        OpenMathBase oo1 = null;
        OpenMathBase oo2 = null;
        try {
            oo1 = OpenMathBase.parse(o1);
            oo2 = OpenMathBase.parse(oo1.toXml());
        } catch (Exception e) {
            throw new RuntimeException("Parsing failed!");
        }
		return oo1.equals(oo2);
	}

	public boolean testInvariance(String o1) {
        OpenMathBase oo1 = null;
        OpenMathBase oo2 = null;
        try {
            oo1 = OpenMathBase.parse(o1);
            oo2 = OpenMathBase.parse(oo1.toXml());
        } catch (Exception e) {
            throw new RuntimeException("Parsing failed!");
        }
		return oo1.toXml().equals(oo2.toXml());
	}
	
	public boolean testFileIO(String o1) {
		
		OpenMathBase om, om2;
		File tfile;
		
		try {
			om = OpenMathBase.parse(o1);
		} catch (Exception e) {
			throw new RuntimeException("Parsing failed!");
		}
		
		try {
	        tfile = File.createTempFile("test", "om_xml");
	        DataOutputStream dos = new DataOutputStream(new FileOutputStream(tfile));
	        Writer out = new OutputStreamWriter(dos, Charset.forName("ISO-8859-1"));
	        om.toXml(out);
	        out.close();
		} catch (Exception e) {
			throw new RuntimeException("Writing failed!");
		}

		try {
	        DataInputStream dis = new DataInputStream(new FileInputStream(tfile));
	        Reader in = new InputStreamReader(dis,Charset.forName("ISO-8859-1"));
	        om2 = OpenMathBase.parse(in);
		} catch (Exception e) {
			throw new RuntimeException("Reading failed!");
		}
		
        if (!(om.equals(om2))) return false;

		tfile.delete();
		return true;
	}
	
}