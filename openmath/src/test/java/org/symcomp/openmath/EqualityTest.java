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

public class EqualityTest {

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

	
	public void testEquality0() { assert testEquality(omxmls[0], omxmls[0]); }
	public void testEquality1() { assert testEquality(omxmls[1], omxmls[1]); }
	public void testEquality2() { assert testEquality(omxmls[2], omxmls[2]); }
	public void testEquality3() { assert testEquality(omxmls[3], omxmls[3]); }
	public void testEquality4() { assert testEquality(omxmls[4], omxmls[4]); }
	public void testEquality5() { assert testEquality(omxmls[5], omxmls[5]); }
	public void testEquality6() { assert testEquality(omxmls[6], omxmls[6]); }
	public void testEquality7() { assert testEquality(omxmls[7], omxmls[7]); }

	public void testEquality8() { assert !testEquality(omxmls[0], omxmls[1]); }
	public void testEquality9() { assert !testEquality(omxmls[0], omxmls[3]); }
	public void testEquality10() { assert !testEquality(omxmls[0], omxmls[6]); }
	public void testEquality11() { assert !testEquality(omxmls[3], omxmls[6]); }
	public void testEquality12() { assert !testEquality(omxmls[4], omxmls[5]); }
	public void testEquality13() { assert !testEquality(omxmls[5], omxmls[3]); }
	public void testEquality14() { assert !testEquality(omxmls[6], omxmls[3]); }
	public void testEquality15() { assert !testEquality(omxmls[7], omxmls[6]); }
	public void testEquality16() { assert testEquality(omxmls[7], omxmls[8]); }
	public void testEquality17() { assert testEquality(omxmls[9], omxmls[9]); }
	public void testEquality18() { assert !testEquality(omxmls[9], omxmls[8]); }
	public void testEquality19() { assert testEquality(omxmls[10], omxmls[10]); }
	public void testEquality20() { assert !testEquality(omxmls[10], omxmls[6]); }

    public void testEquality21() { assert testEquality("  <!-- Comment to get rid of printed or warning-output   -->  <OMOBJ xmlns=\"http://www.openmath.org/OpenMath\">    <OMI>      15    </OMI>  </OMOBJ><?scscp end ?>", "<OMOBJ><OMI>15</OMI></OMOBJ>"); }
    public void testEquality22() { assert !testEquality("<OMOBJ><OMI>15</OMI></OMOBJ>", "<OMOBJ><OMI>13</OMI></OMOBJ>"); }

    public void testEqualityOME() { assert testEquality("<OME><OMS cd=\"error-cd\" name=\"eror-symbol\" /><OMSTR>So ein Mist!</OMSTR></OME>", "<OME><OMS cd='error-cd' name='eror-symbol' /><OMSTR>So ein Mist!</OMSTR>  </OME>"); }
    public void testEqualityOME2() { assert !testEquality("<OME><OMS cd=\"error-cd\" name=\"error-symbol\" /><OMSTR>So ein Mist!</OMSTR></OME>", "<OME><OMS cd=\"error-cd\" name=\"eror-symbol\" /><OMSTR>So ein Mist!</OMSTR>  </OME>"); }

	boolean testEquality(String o1, String o2) {
        OpenMathBase oo1 = null;
        OpenMathBase oo2 = null;
        try {
            oo1 = OpenMathBase.parse(o1);
            oo2 = OpenMathBase.parse(o2);
        } catch (Exception ignore) { }
        return oo1.equals(oo2);
	}

    public void testOverloadedOmsEquality() {
        assert new OMSymbol("hund", "katze").equals("hund.katze");
        assert !(new OMSymbol("hund", "katz").equals("hund.katze"));
        assert !(new OMSymbol("hundd", "katze").equals("hund.katze"));
        assert !(new OMSymbol("hundd", "katz").equals("hund.katze"));
        assert !(new OMSymbol("fred", "jupiter").equals("fredjupiter"));
        assert new OMSymbol("fred", "jupiter").equals(" fred.jupiter");
        assert !(new OMSymbol("fred", "jupiter").equals(" fred .jupiter"));
        assert !(new OMSymbol("fred", "jupiter").equals("fred. jupiter"));
        assert new OMSymbol("fred", "jupiter").equals("fred.jupiter ");
    }

}