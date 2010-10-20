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

public class ProgrammaticalCreationTest  {

  public void testOMString() throws Exception {
    OpenMathBase o1 = OpenMathBase.parse("<OMSTR>Muff</OMSTR>");
    OpenMathBase o2 = new OMString("Muff");
    assert o1.equals(o2);
    OpenMathBase o3 = new OMString("Mupf");
    assert !o1.equals(o3);
    assert !o2.equals(o3);
  }

  public void testOMInteger() throws Exception {
    OpenMathBase o1 = OpenMathBase.parse("<OMI>42</OMI>");
    OpenMathBase o2 = new OMInteger(42);
    assert o1.equals(o2);
    OpenMathBase o3 = new OMInteger(43);
    assert !o1.equals(o3);
    assert !o2.equals(o3);
  }

  public void testOMSymbol() throws Exception {
    OpenMathBase o1 = OpenMathBase.parse("<OMS cd=\"muffcd1\" name=\"symbol1\" />");
    OpenMathBase o2 = new OMSymbol("muffcd1", "symbol1");
    assert o1.equals(o2);
    OpenMathBase o3 = new OMSymbol("muffcd1", "symbol2");
    assert !o1.equals(o3);
    assert !o2.equals(o3);
    o3 = new OMSymbol("muffcd2", "symbol1");
    assert !o1.equals(o3);
    assert !o2.equals(o3);
  }

  public void testOMApply() throws Exception {
    OpenMathBase o1 = OpenMathBase.parse("<OMA><OMS cd=\"muffcd1\" name=\"symbol1\" /><OMI>1</OMI><OMF dec=\"2.1\" /></OMA>");
    OpenMathBase o2 = new OMApply(new OMSymbol("muffcd1", "symbol1"), new OpenMathBase[] { new OMInteger((Integer) 1), new OMFloat(2.1) });
    assert o1.equals(o2);
    OpenMathBase o3 = new OMApply(new OMSymbol("muffcd1", "symbol1"), new OpenMathBase[] { new OMInteger((Integer) 1), new OMFloat(2.0) });
    assert !o1.equals(o3);
    assert !o2.equals(o3);
  }

  public void testOMError() throws Exception {
    OpenMathBase o1 = OpenMathBase.parse("<OME><OMS cd=\"error-cd\" name=\"error-symbol\" /><OMSTR>So ein Mist!</OMSTR></OME>");
    OpenMathBase o2 = new OMError(new OMSymbol("error-cd", "error-symbol"), new OpenMathBase[] { new OMString("So ein Mist!") } );
    assert o1.equals(o2);
    OpenMathBase o3 = new OMError(new OMSymbol("error-cd", "error-symbol"), new OpenMathBase[] { new OMString("String des not matter!") } );
    assert !o1.equals(o3);
    assert !o2.equals(o3);
    o3 = new OMError(new OMSymbol("error-cd", "eror-symbol"), new OpenMathBase[] { new OMString("So ein Mist!") } );
    assert !o1.equals(o3);
    assert !o2.equals(o3);
  }

  public void testOMATTR() throws Exception {
    OpenMathBase o1 = OpenMathBase.parse("<OMATTR><OMATP><OMS cd=\"cd1\" name=\"nam1\"/><OMI>1</OMI></OMATP><OMSTR>Muff</OMSTR></OMATTR>");
        assert o1.getAt(new OMSymbol("cd1", "nam1")).equals(new OMInteger((Integer) 1));
    OpenMathBase o2 = new OMString("Muff");
    o2.putAt(new OMSymbol("cd1", "nam1"), new OMInteger((Integer) 1));
    assert o1.equals(o2);
    o2.putAt(new OMSymbol("cd1", "nam2"), new OMInteger((Integer) 2));
    assert !o1.equals(o2);
    o1.putAt(new OMSymbol("cd1", "nam2"), new OMInteger((Integer) 2));
    assert o1.equals(o2);
    o1 = new OMObject(o1);
    o2 = new OMObject(o2);
    assert o1.equals(o2);

  }

  public void testOMVariable() throws Exception {
    OpenMathBase o1 = OpenMathBase.parse("<OMV name=\"hund\" id=\"17\" />");
    OpenMathBase o2 = new OMVariable("hund");
    assert !o1.equals(o2);
    o2.setId("17");
    assert o1.equals(o2);
    o2.putAt(new OMSymbol("cd1", "nam2"), new OMString("Huhu!"));
    assert !o1.equals(o2);
    o1.putAt(new OMSymbol("cd1", "nam2"), new OMString("Huhu!"));
    assert o1.equals(o2);
  }

  public void testApllyAny() throws Exception {
    OpenMathBase oms = new OMSymbol("cd1", "nam1");
    Object[] params = {new OMSymbol("cd1", "nam2"), new OMInteger("42")};
    OpenMathBase puff = oms.apply2any(params);
    assert ("cd1.nam1(cd1.nam2, 42)").equals(puff.toPopcorn());
  }

}