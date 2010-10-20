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

public class ParserTest {

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
              "<?xml version='1.0'?> <OM:OMOBJ xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.openmath.org/OpenMath http://www.openmath.org/standard/relaxng/openmath1.xsd' xmlns:OM='http://www.openmath.org/OpenMath'> <OM:OMA> <OM:OMS name='DMP' cd='polyd1'/><OM:OMA> <OM:OMS name='poly_ring_d' cd='polyd1'/> <OM:OMS name='Z' cd='setname1'/> <OM:OMI>1</OM:OMI> </OM:OMA> <OM:OMA> <OM:OMS name='SDMP' cd='polyd1'/> <OM:OMA> <OM:OMS name='term' cd='polyd1'/> <OM:OMI> 1 </OM:OMI> <OM:OMI> 2 </OM:OMI> </OM:OMA> <OM:OMA> <OM:OMS name='term' cd='polyd1'/> <OM:OMI> 2 </OM:OMI> <OM:OMI> 0 </OM:OMI> </OM:OMA> </OM:OMA> </OM:OMA> </OM:OMOBJ>",
            "<OMOBJ><OME><OMS id=\"123\" cd=\"error\" name=\"some_name\" /><OMI>1</OMI><OMSTR>Trallala</OMSTR></OME></OMOBJ>"
    };


	public void testParsing0() throws Exception { assert null != OpenMathBase.parse(omxmls[0]); }
	public void testParsing1() throws Exception { assert null != OpenMathBase.parse(omxmls[1]); }
	public void testParsing2() throws Exception { assert null != OpenMathBase.parse(omxmls[2]); }
	public void testParsing3() throws Exception { assert null != OpenMathBase.parse(omxmls[3]); }
	public void testParsing4() throws Exception { assert null != OpenMathBase.parse(omxmls[4]); }
	public void testParsing5() throws Exception { assert null != OpenMathBase.parse(omxmls[5]); }
	public void testParsing6() throws Exception { assert null != OpenMathBase.parse(omxmls[6]); }
	public void testParsing7() throws Exception { assert null != OpenMathBase.parse(omxmls[7]); }
	public void testParsing8() throws Exception { assert null != OpenMathBase.parse(omxmls[8]); }
	public void testParsing9() throws Exception { assert null != OpenMathBase.parse(omxmls[9]); }
	public void testParsing10() throws Exception { assert null != OpenMathBase.parse(omxmls[10]); }

	public void testMuff() throws Exception {
        OMVariable v = new OMVariable("X");
        OMSymbol s = new OMSymbol("hans", "wurst");
        v.setId("lalala");
        s.setId("hurz");
        OpenMathBase o = (v.apply(new OpenMathBase[] {s}));
        OpenMathBase o2 = OpenMathBase.parse("<OMA><OMV id=\"lalala\" name=\"X\" /><OMS cd=\"hans\" name=\"wurst\" id=\"hurz\" /></OMA>");
        assert o.equals(o2);
    }

    //public void testSpeed() {
    //    OpenMathBase.saxParseFile("/Users/hornp/Desktop/test.om");
    //}

}