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

public class SyntacticEqualityTest {

    public void testSyntacticalDifferences0() { assert testEquality("<OMF dec='1.1' />", "<OMF   dec=' 1.1 '/>"); }
    public void testSyntacticalDifferences1() { assert !testEquality("<OMF dec='1.100001' />", "<OMF dec='1.1' />"); }
    public void testSyntacticalDifferences2() { assert testEquality("<OMI>1</OMI>", "<OMI>          1  </OMI>"); }
    public void testSyntacticalDifferences3() { assert !testEquality("<OMI>1</OMI>", "<OMI>2</OMI>"); }
    public void testSyntacticalDifferences4() { assert testEquality("<OMS cd='muff' name='puff' />", "<OMS name='puff' cd='muff' />"); }
    public void testSyntacticalDifferences5() { assert !testEquality("<OMS cd='mupf' name='puff' />", "<OMS name='puff' cd='muff' />"); }
    public void testSyntacticalDifferences6() { assert !testEquality("<OMS cd='muff' name='pufff' />", "<OMS name='puff' cd='muff' />"); }
    public void testSyntacticalDifferences7() { assert testEquality("<OMF dec='1.1' />", "<OMF hex='3ff199999999999a'/>"); }
    public void testSyntacticalDifferences10() { assert testEquality(
            "<OMATTR><OMATP><OMS cd='cd1' name='nam1'/><OMI>1</OMI> </OMATP><OMSTR>Muff</OMSTR> </OMATTR>",
            "<OMATTR><OMATP><OMS name='nam1' cd='cd1' /><OMI> 1</OMI></OMATP><OMSTR>Muff</OMSTR></OMATTR>"); }
    public void testSyntacticalDifferences11() { assert !testEquality(
            "<OMATTR><OMATP><OMS cd='cd1' name='nam1'/><OMI>1</OMI></OMATP><OMSTR>Muff</OMSTR></OMATTR>",
            "<OMATTR><OMATP><OMS cd='cd1' name='nam2'/><OMI>1</OMI></OMATP><OMSTR>Muff</OMSTR></OMATTR>"); }
    public void testSyntacticalDifferences12() { assert !testEquality(
            "<OMATTR><OMATP><OMS cd='cd1' name='nam1'/><OMI>1</OMI></OMATP><OMSTR>Muff</OMSTR></OMATTR>",
            "<OMATTR><OMATP><OMS cd='cd1' name='nam1'/><OMI>1</OMI></OMATP><OMSTR>Mupf</OMSTR></OMATTR>"); }
    public void testSyntacticalDifferences20() { assert testEquality(
            "<OMATTR><OMATP><OMS cd='cd1' name='nam1'/><OMF dec='1.2' /><OMS cd='cd2' name='nam2'/><OMI>1</OMI></OMATP><OMSTR>Mupf</OMSTR></OMATTR>",
            "<OMATTR><OMATP><OMS cd='cd2' name='nam2'/><OMI>1</OMI><OMS cd='cd1' name='nam1'/><OMF dec='1.2' /></OMATP><OMSTR>Mupf</OMSTR></OMATTR>"); }

	boolean testEquality(String o1, String o2) {
        OpenMathBase oo1 = null;
        OpenMathBase oo2 = null;
        try {
            oo1 = OpenMathBase.parse(o1);
            oo2 = OpenMathBase.parse(o2);
        } catch (Exception e) {
            throw new RuntimeException("Parsing failed");
        }
		return oo1.equals(oo2);
	}

	
}