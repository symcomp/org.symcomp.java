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
import org.symcomp.openmath.popcorn.PopcornHelper;

public class PopcornTest {

	/*
	OMS cds and names: [\i-[:]][\c-[:]]*, with \i = [_:A-Za-z] and \c = [-._:A-Za-z0-9]
	*/

    static String[][] eqtests = {
        { "1 + 2", "<OMA><OMS cd='arith1' name='plus' /><OMI>1</OMI><OMI>2</OMI></OMA>"},
        { "1 + 2 + 3*$x", "<OMA><OMS cd='arith1' name='plus' /><OMI>1</OMI><OMI>2</OMI><OMA><OMS cd='arith1' name='times' /><OMI>3</OMI><OMV name='x' /></OMA></OMA>"},
        { "1.2*$x:A-6*#A", "<OMA><OMS cd='arith1' name='minus' /><OMA><OMS cd='arith1' name='times' /><OMF dec='1.2' /><OMV id='A' name='x' /></OMA><OMA><OMS cd='arith1' name='times' /><OMI>6</OMI><OMR href='#A' /></OMA></OMA>"},
        { "lambda[$x -> 1//3*$x]", "<OMBIND><OMS cd='fns1' name='lambda' /><OMBVAR><OMV name='x' /></OMBVAR><OMA><OMS cd='arith1' name='times' /><OMA><OMS cd='nums1' name='rational' /><OMI>1</OMI><OMI>3</OMI></OMA><OMV name='x' /></OMA></OMBIND>"},
        { "(1|7){\"haa\"->2,\"hoo\"->3}", "<OMATTR><OMATP><OMSTR>haa</OMSTR><OMI>2</OMI><OMSTR>hoo</OMSTR><OMI>3</OMI></OMATP><OMA><OMS cd='complex1' name='complex_cartesian' /><OMI>1</OMI><OMI>7</OMI></OMA></OMATTR>"},
        { "aa.bb(1)", "<OMA><OMS cd='aa' name='bb' /><OMI>1</OMI></OMA>"},
		{ "[1, 2, 3]", "list1.list(1,2,3)" },
		{ "{1, 2, 3}", "set1.set(1,2,3)" },
		{ "$a", "<OMV name=\"a\"/>" },
		{ "#a", "<OMR href=\"#a\"/>" },
		{ "##a##", "<OMR href=\"a\"/>" },
		{ "\"x\\\\y\\\"z\"", "<OMSTR>x\\y\"z</OMSTR>"},
		{ "1 + \"\\\\abc-\\\"blah\\\"\"", "<OMA><OMS cd=\"arith1\" name=\"plus\"/><OMI>1</OMI><OMSTR>\\abc-\"blah\"</OMSTR></OMA>"}
	};

	static String[][] neqtests = { 
        { "1*2", "<OMA><OMS cd='arith1' name='plus' /><OMI>1</OMI><OMI>2</OMI></OMA>"},
        { "1+2+3*$y", "<OMA><OMS cd='arith1' name='plus' /><OMI>1</OMI><OMI>2</OMI><OMA><OMS cd='arith1' name='times' /><OMI>3</OMI><OMV name='x' /></OMA></OMA>"},
        { "1.2*$x:B-6*#B", "<OMA><OMS cd='arith1' name='plus' /><OMA><OMS cd='arith1' name='times' /><OMF dec='1.2' /><OMV id='A' name='x' /></OMA><OMA><OMS cd='arith1' name='times' /><OMI>-6</OMI><OMR href='A' /></OMA></OMA>"},
        { "lambda[$x -> 2//3 * $x]", "<OMBIND><OMS cd='fns1' name='lambda' /><OMBVAR><OMV name='x' /></OMBVAR><OMA><OMS cd='arith1' name='times' /><OMA><OMS cd='nums1' name='rational' /><OMI>1</OMI><OMI>3</OMI></OMA><OMV name='x' /></OMA></OMBIND>"},
        { "(1|7){\"hii\"->2, \"hoo\"->3}", "<OMATTR><OMATP><OMSTR>haa</OMSTR><OMI>2</OMI><OMSTR>hoo</OMSTR><OMI>3</OMI></OMATP><OMA><OMS cd='complex1' name='complex_cartesian' /><OMI>1</OMI><OMI>7</OMI></OMA></OMATTR>"},
		{ "[1,2,3]", "list1.list(1,2,3,4)" },
		{ "{1,2,3}", "set1.set(1,2,3,4)" }
    };

	public void testPopParseEq() throws Exception {
		int cntfail = 0;
		
		for(int i = 0; i < eqtests.length; ++i) {
			OpenMathBase o1 = null, o2 = null;
			try {
				o1 = OpenMathBase.parse(eqtests[i][0]);
			} catch (Exception e) {
				System.out.println("testPopParseEq failed to parse '" + eqtests[i][0] + "'");
				++cntfail;
				continue;
			}
			try {
				o2 = OpenMathBase.parse(eqtests[i][1]);
			} catch (Exception e) {
				System.out.println("testPopParseEq failed to parse '" + eqtests[i][1] + "'");
				++cntfail;
				continue;
			}
			
			if (!(o1.equals(o2))) {
				System.out.println("testPopParseEq[" + i + "]: ");
				System.out.println("found:     " + o1.toXml());
				System.out.println("should be: " + o2.toXml());
				++cntfail;
				continue;
			}
			
			String p1 = o1.toPopcorn();
			if (!(eqtests[i][0].equals(p1))) {
				System.out.println("in:         "  + eqtests[i][0]);
				System.out.println("->xml->pop: "  + p1);
				++cntfail;
				continue;
			}
		}
		
		if (cntfail != 0) throw new RuntimeException(String.format("testPopParseEq had %d failures", cntfail));
	}

	public void testPopParseNeq() throws Exception {
		int cntfail = 0;
		
		for(int i = 0; i < neqtests.length; ++i) {
			OpenMathBase o1 = OpenMathBase.parse(neqtests[i][0]);
			OpenMathBase o2 = OpenMathBase.parse(neqtests[i][1]);
			if (o1.equals(o2)) {
				System.out.println("testPopParseNeq[" + i + "]: ");
				System.out.println("found:         " + o1.toXml());
				System.out.println("should not be: " + o2.toXml());
				++cntfail;
			}
		}
		
		if (cntfail != 0) throw new RuntimeException(String.format("testPopParseNeq had %d failures", cntfail));
	}


    public void testPopp0() throws Exception {
        String s1 = "<OME><OMS cd=\"errorcd\" name=\"errorname\"/><OMI>1</OMI></OME>";
        String s2 = "errorcd.errorname!(1)";
        OpenMathBase o1 = OpenMathBase.parse(s1);
        OpenMathBase o2 = OpenMathBase.parsePopcorn(s2);
        assert o1.equals(o2);
        assert o2.toXml().equals(s1);
        assert s2.equals(o1.toPopcorn());
        String s3 = "errorcd.errorname!(2)";
        String s4 = "errorcd.errorname2!(2)";
        OpenMathBase o3 = OpenMathBase.parsePopcorn(s3);
        OpenMathBase o4 = OpenMathBase.parsePopcorn(s4);
        assert !o3.equals(o4);
        assert !o3.equals(o1);
        assert !o3.equals(o2);
        assert !o4.equals(o1);
        assert !o4.equals(o2);
    }

    public void testPopp1() throws Exception {
        String s1 = "<OME><OMI>42</OMI><OMI>1</OMI><OMSTR>lalala</OMSTR></OME>";
        String s2 = "42!(1, \"lalala\")";
        OpenMathBase o1 = OpenMathBase.parse(s1);
        OpenMathBase o2 = OpenMathBase.parsePopcorn(s2);
        assert o1.equals(o2);
        assert o2.toXml().trim().equals(s1.trim());
        assert s2.equals(o1.toPopcorn());
    }

    public void testParseAndRenderInv() throws Exception {
        boolean ok = true;
        for (String s : new String[] {"1:a", "1 + 3:x", "#x", "$x", "##aa##", "xy.z:a", "xa.hh:u(1, 2:s, 3) + $a:yy",
            "1 + 1", "1//1", "1|1", "(1|2)//(3|4)", "1//2|3//4"} ) {
            String s2 = OpenMathBase.parse(s).toPopcorn();
            if (!s2.equals(s)) {
                System.out.println("--> "+s+" != "+s2);
                ok = false;
            }
        }
        assert ok;
    }

	public void testPrec1() throws Exception {
		OpenMathBase x1; 
		OpenMathBase x2;
		
		x1 = OpenMathBase.parse("1-2+3");
		x2 = OpenMathBase.parse("<OMA><OMS cd=\"arith1\" name=\"plus\"/><OMA><OMS cd=\"arith1\" name=\"minus\"/><OMI>1</OMI><OMI>2</OMI></OMA><OMI>3</OMI></OMA>");
		assert x1.equals(x2);

		x1 = OpenMathBase.parse("(1-2)+3)");
		assert x1.equals(x2);

		x1 = OpenMathBase.parse("1-(2+3)");
		x2 = OpenMathBase.parse("<OMA><OMS cd=\"arith1\" name=\"minus\"/><OMI>1</OMI><OMA><OMS cd=\"arith1\" name=\"plus\"/><OMI>2</OMI><OMI>3</OMI></OMA></OMA>");
		assert x1.equals(x2);
	}

	public void testPrec2() throws Exception {
		OpenMathBase x1; 
		OpenMathBase x2;
		
		x1 = OpenMathBase.parse("1/2*3");
		x2 = OpenMathBase.parse("<OMA><OMS cd=\"arith1\" name=\"times\"/><OMA><OMS cd=\"arith1\" name=\"divide\"/><OMI>1</OMI><OMI>2</OMI></OMA><OMI>3</OMI></OMA>");
		assert x1.equals(x2);

		x1 = OpenMathBase.parse("(1/2)*3)");
		assert x1.equals(x2);

		x1 = OpenMathBase.parse("1/(2*3)");
		x2 = OpenMathBase.parse("<OMA><OMS cd=\"arith1\" name=\"divide\"/><OMI>1</OMI><OMA><OMS cd=\"arith1\" name=\"times\"/><OMI>2</OMI><OMI>3</OMI></OMA></OMA>");
		assert x1.equals(x2);
	}
	
	public void testPrec3() throws Exception {
		String x1, x2;

		x1 = OpenMathBase.parse("(1/2)/3").toPopcorn();
		x2 = OpenMathBase.parse("1/(2/3)").toPopcorn();
		assert !(x1.equals(x2));

		x1 = OpenMathBase.parse("(1^2)^3").toPopcorn();
		x2 = OpenMathBase.parse("1^(2^3)").toPopcorn();
		assert !(x1.equals(x2));

	}
	
	public void testPrec4() throws Exception {
		String[][] tests = {
			{"-$x^2", "<OMA><OMS cd=\"arith1\" name=\"unary_minus\"/><OMA><OMS cd=\"arith1\" name=\"power\"/><OMV name=\"x\"/><OMI>2</OMI></OMA></OMA>"},
			{"-($x^2)", "<OMA><OMS cd=\"arith1\" name=\"unary_minus\"/><OMA><OMS cd=\"arith1\" name=\"power\"/><OMV name=\"x\"/><OMI>2</OMI></OMA></OMA>"},
			{"(-$x)^2", "<OMA><OMS cd=\"arith1\" name=\"power\"/><OMA><OMS cd=\"arith1\" name=\"unary_minus\"/><OMV name=\"x\"/></OMA><OMI>2</OMI></OMA>"},
			{"1 + 2 - $x^3", "<OMA><OMS cd=\"arith1\" name=\"minus\"/><OMA><OMS cd=\"arith1\" name=\"plus\"/><OMI>1</OMI><OMI>2</OMI></OMA><OMA><OMS cd=\"arith1\" name=\"power\"/><OMV name=\"x\"/><OMI>3</OMI></OMA></OMA>"},
			{"1 + 2 + 4*$x^3", "<OMA><OMS cd=\"arith1\" name=\"plus\"/><OMI>1</OMI><OMI>2</OMI><OMA><OMS cd=\"arith1\" name=\"times\"/><OMI>4</OMI><OMA><OMS cd=\"arith1\" name=\"power\"/><OMV name=\"x\"/><OMI>3</OMI></OMA></OMA></OMA>"},
			{"1 + 2 - 4*$x^3 + 5", "<OMA><OMS cd=\"arith1\" name=\"plus\"/><OMA><OMS cd=\"arith1\" name=\"minus\"/><OMA><OMS cd=\"arith1\" name=\"plus\"/><OMI>1</OMI><OMI>2</OMI></OMA><OMA><OMS cd=\"arith1\" name=\"times\"/><OMI>4</OMI><OMA><OMS cd=\"arith1\" name=\"power\"/><OMV name=\"x\"/><OMI>3</OMI></OMA></OMA></OMA><OMI>5</OMI></OMA>"}
		};
		
		int cntfail = 0;
		
		for(int i = 0; i < tests.length; ++i) {
			String s = OpenMathBase.parse(tests[i][0]).toXml();
			if (!s.equals(tests[i][1])) {
				System.out.println("testPrec4[" + i + "]: ");
				System.out.println("in:        " + tests[i][0]);
				System.out.println("found:     " + s);
				System.out.println("should be: " + tests[i][1]);
				++cntfail;
				continue;
			}
		}

		if (cntfail != 0) throw new RuntimeException(String.format("testPrec4 had %d failures", cntfail));
	}


	/* Testing printing to and reading from files 
	 * And invariance while we're at it.
	 */
	public boolean testFileIO(String o1) {
		
		OpenMathBase om, om2;
		File tfile;
		
		try {
			om = OpenMathBase.parse(o1);
		} catch (Exception e) {
			throw new RuntimeException("Parsing failed!");
		}
		
		try {
	        tfile = File.createTempFile("test", "om_popcorn");
	        DataOutputStream dos = new DataOutputStream(new FileOutputStream(tfile));
	        Writer out = new OutputStreamWriter(dos, Charset.forName("ISO-8859-1"));
	        om.toPopcorn(out);
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
		
        if (!(om.equals(om2))) {
			System.out.println("om = '" + om.toPopcorn() + "'");
			System.out.println("om2 = '" + om2.toPopcorn() + "'");
			System.out.println("om = '" + om.toXml() + "'");
			System.out.println("om2 = '" + om2.toXml() + "'");
			return false;
		}

		tfile.delete();
		return true;
	}
	
	public void testFileIO() {
		for (int i = 0; i < eqtests.length; ++i) {
			assert testFileIO(eqtests[i][0]);
		}
		for (int i = 0; i < neqtests.length; ++i) {
			assert testFileIO(neqtests[i][0]);
		}
	}

        public void testInfix() throws Exception {
            String[][] tests = {
                {"<OMA><OMS cd=\"prog1\" name=\"block\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1; 2"},
                {"<OMA><OMS cd=\"prog1\" name=\"assign\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 := 2"},
                {"<OMA><OMS cd=\"logic1\" name=\"implies\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 ==> 2"},
                {"<OMA><OMS cd=\"logic1\" name=\"equivalent\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 <=> 2"},
                {"<OMA><OMS cd=\"logic1\" name=\"or\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 or 2"},
                {"<OMA><OMS cd=\"logic1\" name=\"and\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 and 2"},
                {"<OMA><OMS cd=\"relation1\" name=\"lt\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 < 2"},
                {"<OMA><OMS cd=\"relation1\" name=\"leq\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 <= 2"},
                {"<OMA><OMS cd=\"relation1\" name=\"gt\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 > 2"},
                {"<OMA><OMS cd=\"relation1\" name=\"geq\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 >= 2"},
                {"<OMA><OMS cd=\"relation1\" name=\"neq\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 != 2"},
                {"<OMA><OMS cd=\"interval1\" name=\"interval\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 .. 2"},
                {"<OMA><OMS cd=\"arith1\" name=\"plus\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1 + 2"},
                {"<OMA><OMS cd=\"arith1\" name=\"minus\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1-2"},
                {"<OMA><OMS cd=\"arith1\" name=\"times\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1*2"},
                {"<OMA><OMS cd=\"arith1\" name=\"divide\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1/2"},
                {"<OMA><OMS cd=\"arith1\" name=\"power\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1^2"},
                {"<OMA><OMS cd=\"complex1\" name=\"complex_cartesian\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1|2"},
                {"<OMA><OMS cd=\"nums1\" name=\"rational\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "1//2"},
                {"<OMA><OMS cd=\"list1\" name=\"list\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "[1, 2]"},
                {"<OMA><OMS cd=\"set1\" name=\"set\"/><OMI>1</OMI><OMI>2</OMI></OMA>", "{1, 2}"}
            };
            for (int i=0; i<tests.length; i++) {
                boolean success = true;
                String xml = tests[i][0];
                String pop = tests[i][1];
                OpenMathBase omx = OpenMathBase.parse(xml);
                OpenMathBase omp = OpenMathBase.parse(pop);
                success &= omx.equals(omp);
                success &= omx.toPopcorn().equals(pop);
                success &= omp.toXml().equals(""+xml);
                if (!success) {
                    System.out.println(xml + " -> " + pop);
                    System.out.println(""+xml);
                    System.out.println(omx.toPopcorn());
                }
                assert success;
            }
        }

        public void testEvilNames() throws Exception {
            String[][] tests = {
                { "<OMS cd=\"a-b\" name=\"a:b:c-12ö3\"/>", "'a-b'.'a:b:c-12ö3'" },
                { "<OMV name=\"a-b:pö\"/>", "$'a-b:pö'" },
                { "<OMR href=\"#a-b:pö\"/>", "#'a-b:pö'" },
                { "<OMI id=\"a-bü.ø.\">3</OMI>", "3:'a-bü.ø.'" }
            };
            for (int i=0; i<tests.length; i++) {
                boolean success = true;
                String xml = tests[i][0];
                String pop = tests[i][1];
                OpenMathBase omx = OpenMathBase.parse(xml);
                OpenMathBase omp = OpenMathBase.parse(pop);
                success &= omx.equals(omp);
                success &= omx.toPopcorn().equals(pop);
                success &= omp.toXml().equals(""+xml);
                if (!success) {
                    System.out.println(xml + " -> " + pop);
                    System.out.println(omp.toXml());
                    System.out.println(omx.toPopcorn());
                }
                assert success;
            }
        }
        
        public void testShortcuts() throws Exception {
          assert OpenMathBase.parse("one").equals(new OMSymbol("alg1", "one"));
          assert OpenMathBase.parse("LaTeX_encoding").equals(new OMSymbol("altenc", "LaTeX_encoding"));
          assert OpenMathBase.parse("MathML_encoding").equals(new OMSymbol("altenc", "MathML_encoding"));
          assert OpenMathBase.parse("abs").equals(new OMSymbol("arith1", "abs"));
          assert OpenMathBase.parse("product").equals(new OMSymbol("arith1", "product"));
          assert OpenMathBase.parse("root").equals(new OMSymbol("arith1", "root"));
          assert OpenMathBase.parse("sum").equals(new OMSymbol("arith1", "sum"));
          assert OpenMathBase.parse("unary_minus").equals(new OMSymbol("arith1", "unary_minus"));
          assert OpenMathBase.parse("extended_gcd").equals(new OMSymbol("arith3", "extended_gcd"));
          assert OpenMathBase.parse("bigfloat").equals(new OMSymbol("bigfloat1", "bigfloat"));
          assert OpenMathBase.parse("bigfloatprec").equals(new OMSymbol("bigfloat1", "bigfloatprec"));
          assert OpenMathBase.parse("defint").equals(new OMSymbol("calculus1", "defint"));
          assert OpenMathBase.parse("diff").equals(new OMSymbol("calculus1", "diff"));
          assert OpenMathBase.parse("int").equals(new OMSymbol("calculus1", "int"));
          assert OpenMathBase.parse("nthdiff").equals(new OMSymbol("calculus1", "nthdiff"));
          assert OpenMathBase.parse("partialdiff").equals(new OMSymbol("calculus1", "partialdiff"));
          assert OpenMathBase.parse("int2flt").equals(new OMSymbol("coercions", "int2flt"));
          assert OpenMathBase.parse("Bell").equals(new OMSymbol("combinat1", "Bell"));
          assert OpenMathBase.parse("Fibonacci").equals(new OMSymbol("combinat1", "Fibonacci"));
          assert OpenMathBase.parse("Stirling1").equals(new OMSymbol("combinat1", "Stirling1"));
          assert OpenMathBase.parse("Stirling2").equals(new OMSymbol("combinat1", "Stirling2"));
          assert OpenMathBase.parse("binomial").equals(new OMSymbol("combinat1", "binomial"));
          assert OpenMathBase.parse("multinomial").equals(new OMSymbol("combinat1", "multinomial"));
          assert OpenMathBase.parse("argument").equals(new OMSymbol("complex1", "argument"));
          assert OpenMathBase.parse("complex_cartesian").equals(new OMSymbol("complex1", "complex_cartesian"));
          assert OpenMathBase.parse("complex_polar").equals(new OMSymbol("complex1", "complex_polar"));
          assert OpenMathBase.parse("conjugate").equals(new OMSymbol("complex1", "conjugate"));
          assert OpenMathBase.parse("imaginary").equals(new OMSymbol("complex1", "imaginary"));
          assert OpenMathBase.parse("real").equals(new OMSymbol("complex1", "real"));
          assert OpenMathBase.parse("acceleration").equals(new OMSymbol("dimensions1", "acceleration"));
          assert OpenMathBase.parse("area").equals(new OMSymbol("dimensions1", "area"));
          assert OpenMathBase.parse("charge").equals(new OMSymbol("dimensions1", "charge"));
          assert OpenMathBase.parse("concentration").equals(new OMSymbol("dimensions1", "concentration"));
          assert OpenMathBase.parse("current").equals(new OMSymbol("dimensions1", "current"));
          assert OpenMathBase.parse("density").equals(new OMSymbol("dimensions1", "density"));
          assert OpenMathBase.parse("displacement").equals(new OMSymbol("dimensions1", "displacement"));
          assert OpenMathBase.parse("energy").equals(new OMSymbol("dimensions1", "energy"));
          assert OpenMathBase.parse("force").equals(new OMSymbol("dimensions1", "force"));
          assert OpenMathBase.parse("mass").equals(new OMSymbol("dimensions1", "mass"));
          assert OpenMathBase.parse("momentum").equals(new OMSymbol("dimensions1", "momentum"));
          assert OpenMathBase.parse("pressure").equals(new OMSymbol("dimensions1", "pressure"));
          assert OpenMathBase.parse("resistance").equals(new OMSymbol("dimensions1", "resistance"));
          assert OpenMathBase.parse("speed").equals(new OMSymbol("dimensions1", "speed"));
          assert OpenMathBase.parse("temperature").equals(new OMSymbol("dimensions1", "temperature"));
          assert OpenMathBase.parse("time").equals(new OMSymbol("dimensions1", "time"));
          assert OpenMathBase.parse("velocity").equals(new OMSymbol("dimensions1", "velocity"));
          assert OpenMathBase.parse("voltage").equals(new OMSymbol("dimensions1", "voltage"));
          assert OpenMathBase.parse("volume").equals(new OMSymbol("dimensions1", "volume"));
          assert OpenMathBase.parse("decide").equals(new OMSymbol("directives1", "decide"));
          assert OpenMathBase.parse("disprove").equals(new OMSymbol("directives1", "disprove"));
          assert OpenMathBase.parse("evaluate_to_type").equals(new OMSymbol("directives1", "evaluate_to_type"));
          assert OpenMathBase.parse("explore").equals(new OMSymbol("directives1", "explore"));
          assert OpenMathBase.parse("find").equals(new OMSymbol("directives1", "find"));
          assert OpenMathBase.parse("look_up").equals(new OMSymbol("directives1", "look_up"));
          assert OpenMathBase.parse("prove").equals(new OMSymbol("directives1", "prove"));
          assert OpenMathBase.parse("prove_in_theory").equals(new OMSymbol("directives1", "prove_in_theory"));
          assert OpenMathBase.parse("response").equals(new OMSymbol("directives1", "response"));
          assert OpenMathBase.parse("Tuple").equals(new OMSymbol("ecc", "Tuple"));
          assert OpenMathBase.parse("unexpected_symbol").equals(new OMSymbol("error", "unexpected_symbol"));
          assert OpenMathBase.parse("unhandled_symbol").equals(new OMSymbol("error", "unhandled_symbol"));
          assert OpenMathBase.parse("unsupported_CD").equals(new OMSymbol("error", "unsupported_CD"));
          assert OpenMathBase.parse("field").equals(new OMSymbol("field1", "field"));
          assert OpenMathBase.parse("is_subfield").equals(new OMSymbol("field1", "is_subfield"));
          assert OpenMathBase.parse("subfield").equals(new OMSymbol("field1", "subfield"));
          assert OpenMathBase.parse("field_by_poly").equals(new OMSymbol("field3", "field_by_poly"));
          assert OpenMathBase.parse("fraction_field").equals(new OMSymbol("field3", "fraction_field"));
          assert OpenMathBase.parse("free_field").equals(new OMSymbol("field3", "free_field"));
          assert OpenMathBase.parse("field_by_poly_map").equals(new OMSymbol("field4", "field_by_poly_map"));
          assert OpenMathBase.parse("field_by_poly_vector").equals(new OMSymbol("field4", "field_by_poly_vector"));
          assert OpenMathBase.parse("domainofapplication").equals(new OMSymbol("fns1", "domainofapplication"));
          assert OpenMathBase.parse("image").equals(new OMSymbol("fns1", "image"));
          assert OpenMathBase.parse("lambda").equals(new OMSymbol("fns1", "lambda"));
          assert OpenMathBase.parse("left_inverse").equals(new OMSymbol("fns1", "left_inverse"));
          assert OpenMathBase.parse("range").equals(new OMSymbol("fns1", "range"));
          assert OpenMathBase.parse("right_inverse").equals(new OMSymbol("fns1", "right_inverse"));
          assert OpenMathBase.parse("apply_to_list").equals(new OMSymbol("fns2", "apply_to_list"));
          assert OpenMathBase.parse("function").equals(new OMSymbol("fns3", "function"));
          assert OpenMathBase.parse("specification").equals(new OMSymbol("fns3", "specification"));
          assert OpenMathBase.parse("character_table").equals(new OMSymbol("gp1", "character_table"));
          assert OpenMathBase.parse("character_table_of_group").equals(new OMSymbol("gp1", "character_table_of_group"));
          assert OpenMathBase.parse("declare_group").equals(new OMSymbol("gp1", "declare_group"));
          assert OpenMathBase.parse("element_set").equals(new OMSymbol("gp1", "element_set"));
          assert OpenMathBase.parse("is_abelian").equals(new OMSymbol("gp1", "is_abelian"));
          assert OpenMathBase.parse("arrowset").equals(new OMSymbol("graph1", "arrowset"));
          assert OpenMathBase.parse("digraph").equals(new OMSymbol("graph1", "digraph"));
          assert OpenMathBase.parse("edgeset").equals(new OMSymbol("graph1", "edgeset"));
          assert OpenMathBase.parse("graph").equals(new OMSymbol("graph1", "graph"));
          assert OpenMathBase.parse("source").equals(new OMSymbol("graph1", "source"));
          assert OpenMathBase.parse("target").equals(new OMSymbol("graph1", "target"));
          assert OpenMathBase.parse("vertexset").equals(new OMSymbol("graph1", "vertexset"));
          assert OpenMathBase.parse("right_inverse_multiplication").equals(new OMSymbol("group2", "right_inverse_multiplication"));
          assert OpenMathBase.parse("GL").equals(new OMSymbol("group3", "GL"));
          assert OpenMathBase.parse("GLn").equals(new OMSymbol("group3", "GLn"));
          assert OpenMathBase.parse("SL").equals(new OMSymbol("group3", "SL"));
          assert OpenMathBase.parse("SLn").equals(new OMSymbol("group3", "SLn"));
          assert OpenMathBase.parse("alternatingn").equals(new OMSymbol("group3", "alternatingn"));
          assert OpenMathBase.parse("centralizer").equals(new OMSymbol("group3", "centralizer"));
          assert OpenMathBase.parse("free_group").equals(new OMSymbol("group3", "free_group"));
          assert OpenMathBase.parse("normalizer").equals(new OMSymbol("group3", "normalizer"));
          assert OpenMathBase.parse("symmetric_groupn").equals(new OMSymbol("group3", "symmetric_groupn"));
          assert OpenMathBase.parse("are_conjugate").equals(new OMSymbol("group4", "are_conjugate"));
          assert OpenMathBase.parse("conjugacy_class_representatives").equals(new OMSymbol("group4", "conjugacy_class_representatives"));
          assert OpenMathBase.parse("conjugacy_classes").equals(new OMSymbol("group4", "conjugacy_classes"));
          assert OpenMathBase.parse("left_coset").equals(new OMSymbol("group4", "left_coset"));
          assert OpenMathBase.parse("left_coset_representative").equals(new OMSymbol("group4", "left_coset_representative"));
          assert OpenMathBase.parse("left_cosets").equals(new OMSymbol("group4", "left_cosets"));
          assert OpenMathBase.parse("left_transversal").equals(new OMSymbol("group4", "left_transversal"));
          assert OpenMathBase.parse("right_coset").equals(new OMSymbol("group4", "right_coset"));
          assert OpenMathBase.parse("right_coset_representative").equals(new OMSymbol("group4", "right_coset_representative"));
          assert OpenMathBase.parse("right_cosets").equals(new OMSymbol("group4", "right_cosets"));
          assert OpenMathBase.parse("left_quotient_map").equals(new OMSymbol("group5", "left_quotient_map"));
          assert OpenMathBase.parse("right_quotient_map").equals(new OMSymbol("group5", "right_quotient_map"));
          assert OpenMathBase.parse("generalized_quaternion_group").equals(new OMSymbol("groupname1", "generalized_quaternion_group"));
          assert OpenMathBase.parse("IndType").equals(new OMSymbol("icc", "IndType"));
          assert OpenMathBase.parse("indNat").equals(new OMSymbol("indnat", "indNat"));
          assert OpenMathBase.parse("succ").equals(new OMSymbol("indnat", "succ"));
          assert OpenMathBase.parse("euler").equals(new OMSymbol("integer1", "euler"));
          assert OpenMathBase.parse("factorial").equals(new OMSymbol("integer1", "factorial"));
          assert OpenMathBase.parse("factorof").equals(new OMSymbol("integer1", "factorof"));
          assert OpenMathBase.parse("leading_monomial").equals(new OMSymbol("integer1", "leading_monomial"));
          assert OpenMathBase.parse("leading_term").equals(new OMSymbol("integer1", "leading_term"));
          assert OpenMathBase.parse("ord").equals(new OMSymbol("integer1", "ord"));
          assert OpenMathBase.parse("integer_interval").equals(new OMSymbol("interval1", "integer_interval"));
          assert OpenMathBase.parse("interval").equals(new OMSymbol("interval1", "interval"));
          assert OpenMathBase.parse("interval_cc").equals(new OMSymbol("interval1", "interval_cc"));
          assert OpenMathBase.parse("interval_co").equals(new OMSymbol("interval1", "interval_co"));
          assert OpenMathBase.parse("interval_oc").equals(new OMSymbol("interval1", "interval_oc"));
          assert OpenMathBase.parse("interval_oo").equals(new OMSymbol("interval1", "interval_oo"));
          assert OpenMathBase.parse("Lambda").equals(new OMSymbol("lc", "Lambda"));
          assert OpenMathBase.parse("PiType").equals(new OMSymbol("lc", "PiType"));
          assert OpenMathBase.parse("above").equals(new OMSymbol("limit1", "above"));
          assert OpenMathBase.parse("below").equals(new OMSymbol("limit1", "below"));
          assert OpenMathBase.parse("both_sides").equals(new OMSymbol("limit1", "both_sides"));
          assert OpenMathBase.parse("limit").equals(new OMSymbol("limit1", "limit"));
          assert OpenMathBase.parse("null").equals(new OMSymbol("limit1", "null"));
          assert OpenMathBase.parse("determinant").equals(new OMSymbol("linalg1", "determinant"));
          assert OpenMathBase.parse("matrix_selector").equals(new OMSymbol("linalg1", "matrix_selector"));
          assert OpenMathBase.parse("outerproduct").equals(new OMSymbol("linalg1", "outerproduct"));
          assert OpenMathBase.parse("scalarproduct").equals(new OMSymbol("linalg1", "scalarproduct"));
          assert OpenMathBase.parse("transpose").equals(new OMSymbol("linalg1", "transpose"));
          assert OpenMathBase.parse("vector_selector").equals(new OMSymbol("linalg1", "vector_selector"));
          assert OpenMathBase.parse("vectorproduct").equals(new OMSymbol("linalg1", "vectorproduct"));
          assert OpenMathBase.parse("matrixrow").equals(new OMSymbol("linalg2", "matrixrow"));
          assert OpenMathBase.parse("matrixcolumn").equals(new OMSymbol("linalg3", "matrixcolumn"));
          assert OpenMathBase.parse("characteristic_eqn").equals(new OMSymbol("linalg4", "characteristic_eqn"));
          assert OpenMathBase.parse("columncount").equals(new OMSymbol("linalg4", "columncount"));
          assert OpenMathBase.parse("eigenvalue").equals(new OMSymbol("linalg4", "eigenvalue"));
          assert OpenMathBase.parse("eigenvector").equals(new OMSymbol("linalg4", "eigenvector"));
          assert OpenMathBase.parse("rowcount").equals(new OMSymbol("linalg4", "rowcount"));
          assert OpenMathBase.parse("Hermitian").equals(new OMSymbol("linalg5", "Hermitian"));
          assert OpenMathBase.parse("'anti-Hermitian'").equals(new OMSymbol("linalg5", "anti-Hermitian"));
          assert OpenMathBase.parse("banded").equals(new OMSymbol("linalg5", "banded"));
          assert OpenMathBase.parse("constant").equals(new OMSymbol("linalg5", "constant"));
          assert OpenMathBase.parse("diagonal_matrix").equals(new OMSymbol("linalg5", "diagonal_matrix"));
          assert OpenMathBase.parse("'lower-Hessenberg'").equals(new OMSymbol("linalg5", "lower-Hessenberg"));
          assert OpenMathBase.parse("'lower-triangular'").equals(new OMSymbol("linalg5", "lower-triangular"));
          assert OpenMathBase.parse("scalar").equals(new OMSymbol("linalg5", "scalar"));
          assert OpenMathBase.parse("'skew-symmetric'").equals(new OMSymbol("linalg5", "skew-symmetric"));
          assert OpenMathBase.parse("tridiagonal").equals(new OMSymbol("linalg5", "tridiagonal"));
          assert OpenMathBase.parse("'upper-Hessenberg'").equals(new OMSymbol("linalg5", "upper-Hessenberg"));
          assert OpenMathBase.parse("'upper-triangular'").equals(new OMSymbol("linalg5", "upper-triangular"));
          assert OpenMathBase.parse("list_to_matrix").equals(new OMSymbol("linalg7", "list_to_matrix"));
          assert OpenMathBase.parse("list_to_vector").equals(new OMSymbol("linalg7", "list_to_vector"));
          assert OpenMathBase.parse("difference").equals(new OMSymbol("list1", "difference"));
          assert OpenMathBase.parse("entry").equals(new OMSymbol("list1", "entry"));
          assert OpenMathBase.parse("list").equals(new OMSymbol("list1", "list"));
          assert OpenMathBase.parse("list_of_lengthn").equals(new OMSymbol("list1", "list_of_lengthn"));
          assert OpenMathBase.parse("select").equals(new OMSymbol("list1", "select"));
          assert OpenMathBase.parse("append").equals(new OMSymbol("list2", "append"));
          assert OpenMathBase.parse("cons").equals(new OMSymbol("list2", "cons"));
          assert OpenMathBase.parse("first").equals(new OMSymbol("list2", "first"));
          assert OpenMathBase.parse("list_selector").equals(new OMSymbol("list2", "list_selector"));
          assert OpenMathBase.parse("nil").equals(new OMSymbol("list2", "nil"));
          assert OpenMathBase.parse("rest").equals(new OMSymbol("list2", "rest"));
          assert OpenMathBase.parse("reverse").equals(new OMSymbol("list2", "reverse"));
          assert OpenMathBase.parse("equivalent").equals(new OMSymbol("logic1", "equivalent"));
          assert OpenMathBase.parse("false").equals(new OMSymbol("logic1", "false"));
          assert OpenMathBase.parse("implies").equals(new OMSymbol("logic1", "implies"));
          assert OpenMathBase.parse("true").equals(new OMSymbol("logic1", "true"));
          assert OpenMathBase.parse("xor").equals(new OMSymbol("logic1", "xor"));
          assert OpenMathBase.parse("is_associative").equals(new OMSymbol("magma1", "is_associative"));
          assert OpenMathBase.parse("is_identity").equals(new OMSymbol("magma1", "is_identity"));
          assert OpenMathBase.parse("is_submagma").equals(new OMSymbol("magma1", "is_submagma"));
          assert OpenMathBase.parse("left_divides").equals(new OMSymbol("magma1", "left_divides"));
          assert OpenMathBase.parse("left_expression").equals(new OMSymbol("magma1", "left_expression"));
          assert OpenMathBase.parse("right_divides").equals(new OMSymbol("magma1", "right_divides"));
          assert OpenMathBase.parse("right_expression").equals(new OMSymbol("magma1", "right_expression"));
          assert OpenMathBase.parse("submagma").equals(new OMSymbol("magma1", "submagma"));
          assert OpenMathBase.parse("free_magma").equals(new OMSymbol("magma3", "free_magma"));
          assert OpenMathBase.parse("complex_cartesian_type").equals(new OMSymbol("mathmltypes", "complex_cartesian_type"));
          assert OpenMathBase.parse("complex_polar_type").equals(new OMSymbol("mathmltypes", "complex_polar_type"));
          assert OpenMathBase.parse("constant_type").equals(new OMSymbol("mathmltypes", "constant_type"));
          assert OpenMathBase.parse("fn_type").equals(new OMSymbol("mathmltypes", "fn_type"));
          assert OpenMathBase.parse("integer_type").equals(new OMSymbol("mathmltypes", "integer_type"));
          assert OpenMathBase.parse("list_type").equals(new OMSymbol("mathmltypes", "list_type"));
          assert OpenMathBase.parse("matrix_type").equals(new OMSymbol("mathmltypes", "matrix_type"));
          assert OpenMathBase.parse("rational_type").equals(new OMSymbol("mathmltypes", "rational_type"));
          assert OpenMathBase.parse("real_type").equals(new OMSymbol("mathmltypes", "real_type"));
          assert OpenMathBase.parse("set_type").equals(new OMSymbol("mathmltypes", "set_type"));
          assert OpenMathBase.parse("vector_type").equals(new OMSymbol("mathmltypes", "vector_type"));
          assert OpenMathBase.parse("CD").equals(new OMSymbol("meta", "CD"));
          assert OpenMathBase.parse("CDBase").equals(new OMSymbol("meta", "CDBase"));
          assert OpenMathBase.parse("CDDate").equals(new OMSymbol("meta", "CDDate"));
          assert OpenMathBase.parse("CDDefinition").equals(new OMSymbol("meta", "CDDefinition"));
          assert OpenMathBase.parse("CDReviewDate").equals(new OMSymbol("meta", "CDReviewDate"));
          assert OpenMathBase.parse("CDRevision").equals(new OMSymbol("meta", "CDRevision"));
          assert OpenMathBase.parse("CDStatus").equals(new OMSymbol("meta", "CDStatus"));
          assert OpenMathBase.parse("CDUses").equals(new OMSymbol("meta", "CDUses"));
          assert OpenMathBase.parse("CMP").equals(new OMSymbol("meta", "CMP"));
          assert OpenMathBase.parse("Description").equals(new OMSymbol("meta", "Description"));
          assert OpenMathBase.parse("Example").equals(new OMSymbol("meta", "Example"));
          assert OpenMathBase.parse("FMP").equals(new OMSymbol("meta", "FMP"));
          assert OpenMathBase.parse("Name").equals(new OMSymbol("meta", "Name"));
          assert OpenMathBase.parse("Role").equals(new OMSymbol("meta", "Role"));
          assert OpenMathBase.parse("CDGroup").equals(new OMSymbol("metagrp", "CDGroup"));
          assert OpenMathBase.parse("CDGroupDescription").equals(new OMSymbol("metagrp", "CDGroupDescription"));
          assert OpenMathBase.parse("CDGroupMember").equals(new OMSymbol("metagrp", "CDGroupMember"));
          assert OpenMathBase.parse("CDGroupName").equals(new OMSymbol("metagrp", "CDGroupName"));
          assert OpenMathBase.parse("CDGroupURL").equals(new OMSymbol("metagrp", "CDGroupURL"));
          assert OpenMathBase.parse("CDGroupVersion").equals(new OMSymbol("metagrp", "CDGroupVersion"));
          assert OpenMathBase.parse("CDSComment").equals(new OMSymbol("metasig", "CDSComment"));
          assert OpenMathBase.parse("CDSReviewDate").equals(new OMSymbol("metasig", "CDSReviewDate"));
          assert OpenMathBase.parse("CDSStatus").equals(new OMSymbol("metasig", "CDSStatus"));
          assert OpenMathBase.parse("CDSignatures").equals(new OMSymbol("metasig", "CDSignatures"));
          assert OpenMathBase.parse("Signature").equals(new OMSymbol("metasig", "Signature"));
          assert OpenMathBase.parse("max").equals(new OMSymbol("minmax1", "max"));
          assert OpenMathBase.parse("min").equals(new OMSymbol("minmax1", "min"));
          assert OpenMathBase.parse("divisor_of").equals(new OMSymbol("monoid1", "divisor_of"));
          assert OpenMathBase.parse("is_invertible").equals(new OMSymbol("monoid1", "is_invertible"));
          assert OpenMathBase.parse("is_submonoid").equals(new OMSymbol("monoid1", "is_submonoid"));
          assert OpenMathBase.parse("submonoid").equals(new OMSymbol("monoid1", "submonoid"));
          assert OpenMathBase.parse("concatenation").equals(new OMSymbol("monoid3", "concatenation"));
          assert OpenMathBase.parse("cyclic_monoid").equals(new OMSymbol("monoid3", "cyclic_monoid"));
          assert OpenMathBase.parse("emptyword").equals(new OMSymbol("monoid3", "emptyword"));
          assert OpenMathBase.parse("free_monoid").equals(new OMSymbol("monoid3", "free_monoid"));
          assert OpenMathBase.parse("maps_monoid").equals(new OMSymbol("monoid3", "maps_monoid"));
          assert OpenMathBase.parse("strings").equals(new OMSymbol("monoid3", "strings"));
          assert OpenMathBase.parse("algorithm").equals(new OMSymbol("moreerrors", "algorithm"));
          assert OpenMathBase.parse("asynchronousError").equals(new OMSymbol("moreerrors", "asynchronousError"));
          assert OpenMathBase.parse("encodingError").equals(new OMSymbol("moreerrors", "encodingError"));
          assert OpenMathBase.parse("limitation").equals(new OMSymbol("moreerrors", "limitation"));
          assert OpenMathBase.parse("unexpected").equals(new OMSymbol("moreerrors", "unexpected"));
          assert OpenMathBase.parse("multiset").equals(new OMSymbol("multiset1", "multiset"));
          assert OpenMathBase.parse("NaN").equals(new OMSymbol("nums1", "NaN"));
          assert OpenMathBase.parse("based_integer").equals(new OMSymbol("nums1", "based_integer"));
          assert OpenMathBase.parse("e").equals(new OMSymbol("nums1", "e"));
          assert OpenMathBase.parse("gamma").equals(new OMSymbol("nums1", "gamma"));
          assert OpenMathBase.parse("i").equals(new OMSymbol("nums1", "i"));
          assert OpenMathBase.parse("infinity").equals(new OMSymbol("nums1", "infinity"));
          assert OpenMathBase.parse("pi").equals(new OMSymbol("nums1", "pi"));
          assert OpenMathBase.parse("rational").equals(new OMSymbol("nums1", "rational"));
          assert OpenMathBase.parse("bytearray").equals(new OMSymbol("omtypes", "bytearray"));
          assert OpenMathBase.parse("float").equals(new OMSymbol("omtypes", "float"));
          assert OpenMathBase.parse("integer").equals(new OMSymbol("omtypes", "integer"));
          assert OpenMathBase.parse("omtype").equals(new OMSymbol("omtypes", "omtype"));
          assert OpenMathBase.parse("string").equals(new OMSymbol("omtypes", "string"));
          assert OpenMathBase.parse("symtype").equals(new OMSymbol("omtypes", "symtype"));
          assert OpenMathBase.parse("base").equals(new OMSymbol("permgp1", "base"));
          assert OpenMathBase.parse("generators").equals(new OMSymbol("permgp1", "generators"));
          assert OpenMathBase.parse("is_in").equals(new OMSymbol("permgp1", "is_in"));
          assert OpenMathBase.parse("orbits").equals(new OMSymbol("permgp1", "orbits"));
          assert OpenMathBase.parse("schreier_tree").equals(new OMSymbol("permgp1", "schreier_tree"));
          assert OpenMathBase.parse("stabilizer_chain").equals(new OMSymbol("permgp1", "stabilizer_chain"));
          assert OpenMathBase.parse("vierer_group").equals(new OMSymbol("permgp2", "vierer_group"));
          assert OpenMathBase.parse("action").equals(new OMSymbol("permutation1", "action"));
          assert OpenMathBase.parse("are_distinct").equals(new OMSymbol("permutation1", "are_distinct"));
          assert OpenMathBase.parse("cycle_type").equals(new OMSymbol("permutation1", "cycle_type"));
          assert OpenMathBase.parse("cycles").equals(new OMSymbol("permutation1", "cycles"));
          assert OpenMathBase.parse("endomap").equals(new OMSymbol("permutation1", "endomap"));
          assert OpenMathBase.parse("endomap_left_compose").equals(new OMSymbol("permutation1", "endomap_left_compose"));
          assert OpenMathBase.parse("endomap_right_compose").equals(new OMSymbol("permutation1", "endomap_right_compose"));
          assert OpenMathBase.parse("fix").equals(new OMSymbol("permutation1", "fix"));
          assert OpenMathBase.parse("is_bijective").equals(new OMSymbol("permutation1", "is_bijective"));
          assert OpenMathBase.parse("is_endomap").equals(new OMSymbol("permutation1", "is_endomap"));
          assert OpenMathBase.parse("is_list_perm").equals(new OMSymbol("permutation1", "is_list_perm"));
          assert OpenMathBase.parse("is_permutation").equals(new OMSymbol("permutation1", "is_permutation"));
          assert OpenMathBase.parse("list_perm").equals(new OMSymbol("permutation1", "list_perm"));
          assert OpenMathBase.parse("listendomap").equals(new OMSymbol("permutation1", "listendomap"));
          assert OpenMathBase.parse("permutationsn").equals(new OMSymbol("permutation1", "permutationsn"));
          assert OpenMathBase.parse("sign").equals(new OMSymbol("permutation1", "sign"));
          assert OpenMathBase.parse("Avogadros_constant").equals(new OMSymbol("physical_consts1", "Avogadros_constant"));
          assert OpenMathBase.parse("Boltzmann_constant").equals(new OMSymbol("physical_consts1", "Boltzmann_constant"));
          assert OpenMathBase.parse("Faradays_constant").equals(new OMSymbol("physical_consts1", "Faradays_constant"));
          assert OpenMathBase.parse("Loschmidt_constant").equals(new OMSymbol("physical_consts1", "Loschmidt_constant"));
          assert OpenMathBase.parse("Planck_constant").equals(new OMSymbol("physical_consts1", "Planck_constant"));
          assert OpenMathBase.parse("absolute_zero").equals(new OMSymbol("physical_consts1", "absolute_zero"));
          assert OpenMathBase.parse("gas_constant").equals(new OMSymbol("physical_consts1", "gas_constant"));
          assert OpenMathBase.parse("gravitational_constant").equals(new OMSymbol("physical_consts1", "gravitational_constant"));
          assert OpenMathBase.parse("light_year").equals(new OMSymbol("physical_consts1", "light_year"));
          assert OpenMathBase.parse("magnetic_constant").equals(new OMSymbol("physical_consts1", "magnetic_constant"));
          assert OpenMathBase.parse("mole").equals(new OMSymbol("physical_consts1", "mole"));
          assert OpenMathBase.parse("speed_of_light").equals(new OMSymbol("physical_consts1", "speed_of_light"));
          assert OpenMathBase.parse("zero_Celsius").equals(new OMSymbol("physical_consts1", "zero_Celsius"));
          assert OpenMathBase.parse("zero_Fahrenheit").equals(new OMSymbol("physical_consts1", "zero_Fahrenheit"));
          assert OpenMathBase.parse("otherwise").equals(new OMSymbol("piece1", "otherwise"));
          assert OpenMathBase.parse("piece").equals(new OMSymbol("piece1", "piece"));
          assert OpenMathBase.parse("piecewise").equals(new OMSymbol("piece1", "piecewise"));
          assert OpenMathBase.parse("are_on_conic").equals(new OMSymbol("plangeo1", "are_on_conic"));
          assert OpenMathBase.parse("are_on_line").equals(new OMSymbol("plangeo1", "are_on_line"));
          assert OpenMathBase.parse("assertion").equals(new OMSymbol("plangeo1", "assertion"));
          assert OpenMathBase.parse("configuration").equals(new OMSymbol("plangeo1", "configuration"));
          assert OpenMathBase.parse("conic").equals(new OMSymbol("plangeo1", "conic"));
          assert OpenMathBase.parse("incident").equals(new OMSymbol("plangeo1", "incident"));
          assert OpenMathBase.parse("line").equals(new OMSymbol("plangeo1", "line"));
          assert OpenMathBase.parse("point").equals(new OMSymbol("plangeo1", "point"));
          assert OpenMathBase.parse("corner").equals(new OMSymbol("plangeo2", "corner"));
          assert OpenMathBase.parse("endpoint").equals(new OMSymbol("plangeo2", "endpoint"));
          assert OpenMathBase.parse("endpoints").equals(new OMSymbol("plangeo2", "endpoints"));
          assert OpenMathBase.parse("halfline").equals(new OMSymbol("plangeo2", "halfline"));
          assert OpenMathBase.parse("segment").equals(new OMSymbol("plangeo2", "segment"));
          assert OpenMathBase.parse("altitude").equals(new OMSymbol("plangeo3", "altitude"));
          assert OpenMathBase.parse("angle").equals(new OMSymbol("plangeo3", "angle"));
          assert OpenMathBase.parse("arc").equals(new OMSymbol("plangeo3", "arc"));
          assert OpenMathBase.parse("are_on_circle").equals(new OMSymbol("plangeo3", "are_on_circle"));
          assert OpenMathBase.parse("center_of").equals(new OMSymbol("plangeo3", "center_of"));
          assert OpenMathBase.parse("center_of_gravity").equals(new OMSymbol("plangeo3", "center_of_gravity"));
          assert OpenMathBase.parse("circle").equals(new OMSymbol("plangeo3", "circle"));
          assert OpenMathBase.parse("distance").equals(new OMSymbol("plangeo3", "distance"));
          assert OpenMathBase.parse("is_midpoint").equals(new OMSymbol("plangeo3", "is_midpoint"));
          assert OpenMathBase.parse("midpoint").equals(new OMSymbol("plangeo3", "midpoint"));
          assert OpenMathBase.parse("parallel").equals(new OMSymbol("plangeo3", "parallel"));
          assert OpenMathBase.parse("perpbisector").equals(new OMSymbol("plangeo3", "perpbisector"));
          assert OpenMathBase.parse("perpendicular").equals(new OMSymbol("plangeo3", "perpendicular"));
          assert OpenMathBase.parse("perpline").equals(new OMSymbol("plangeo3", "perpline"));
          assert OpenMathBase.parse("polarline").equals(new OMSymbol("plangeo3", "polarline"));
          assert OpenMathBase.parse("radius").equals(new OMSymbol("plangeo3", "radius"));
          assert OpenMathBase.parse("radius_of").equals(new OMSymbol("plangeo3", "radius_of"));
          assert OpenMathBase.parse("tangent").equals(new OMSymbol("plangeo3", "tangent"));
          assert OpenMathBase.parse("affine_coordinates").equals(new OMSymbol("plangeo4", "affine_coordinates"));
          assert OpenMathBase.parse("coordinates").equals(new OMSymbol("plangeo4", "coordinates"));
          assert OpenMathBase.parse("is_affine").equals(new OMSymbol("plangeo4", "is_affine"));
          assert OpenMathBase.parse("set_affine_coordinates").equals(new OMSymbol("plangeo4", "set_affine_coordinates"));
          assert OpenMathBase.parse("set_coordinates").equals(new OMSymbol("plangeo4", "set_coordinates"));
          assert OpenMathBase.parse("coordinatize").equals(new OMSymbol("plangeo5", "coordinatize"));
          assert OpenMathBase.parse("is_coordinatized").equals(new OMSymbol("plangeo5", "is_coordinatized"));
          assert OpenMathBase.parse("polynomial_assertion").equals(new OMSymbol("plangeo5", "polynomial_assertion"));
          assert OpenMathBase.parse("convert").equals(new OMSymbol("poly", "convert"));
          assert OpenMathBase.parse("degree_wrt").equals(new OMSymbol("poly", "degree_wrt"));
          assert OpenMathBase.parse("discriminant").equals(new OMSymbol("poly", "discriminant"));
          assert OpenMathBase.parse("factored").equals(new OMSymbol("poly", "factored"));
          assert OpenMathBase.parse("partially_factored").equals(new OMSymbol("poly", "partially_factored"));
          assert OpenMathBase.parse("resultant").equals(new OMSymbol("poly", "resultant"));
          assert OpenMathBase.parse("squarefree").equals(new OMSymbol("poly", "squarefree"));
          assert OpenMathBase.parse("squarefreed").equals(new OMSymbol("poly", "squarefreed"));
          assert OpenMathBase.parse("ambient_ring").equals(new OMSymbol("polyd1", "ambient_ring"));
          assert OpenMathBase.parse("variables").equals(new OMSymbol("polyd1", "variables"));
          assert OpenMathBase.parse("collect").equals(new OMSymbol("polyd3", "collect"));
          assert OpenMathBase.parse("list_to_poly_d").equals(new OMSymbol("polyd3", "list_to_poly_d"));
          assert OpenMathBase.parse("poly_d_named_to_arith").equals(new OMSymbol("polyd3", "poly_d_named_to_arith"));
          assert OpenMathBase.parse("poly_d_to_arith").equals(new OMSymbol("polyd3", "poly_d_to_arith"));
          assert OpenMathBase.parse("groebner_basis").equals(new OMSymbol("polygb", "groebner_basis"));
          assert OpenMathBase.parse("extended_in").equals(new OMSymbol("polygb2", "extended_in"));
          assert OpenMathBase.parse("in_radical").equals(new OMSymbol("polygb2", "in_radical"));
          assert OpenMathBase.parse("minimal_groebner_element").equals(new OMSymbol("polygb2", "minimal_groebner_element"));
          assert OpenMathBase.parse("poly_r_rep").equals(new OMSymbol("polyr", "poly_r_rep"));
          assert OpenMathBase.parse("polynomial_r").equals(new OMSymbol("polyr", "polynomial_r"));
          assert OpenMathBase.parse("polynomial_ring_r").equals(new OMSymbol("polyr", "polynomial_ring_r"));
          assert OpenMathBase.parse("const_node").equals(new OMSymbol("polyslp", "const_node"));
          assert OpenMathBase.parse("depth").equals(new OMSymbol("polyslp", "depth"));
          assert OpenMathBase.parse("inp_node").equals(new OMSymbol("polyslp", "inp_node"));
          assert OpenMathBase.parse("left_ref").equals(new OMSymbol("polyslp", "left_ref"));
          assert OpenMathBase.parse("monte_carlo_eq").equals(new OMSymbol("polyslp", "monte_carlo_eq"));
          assert OpenMathBase.parse("node_selector").equals(new OMSymbol("polyslp", "node_selector"));
          assert OpenMathBase.parse("op_node").equals(new OMSymbol("polyslp", "op_node"));
          assert OpenMathBase.parse("poly_ring_SLP").equals(new OMSymbol("polyslp", "poly_ring_SLP"));
          assert OpenMathBase.parse("polynomial_SLP").equals(new OMSymbol("polyslp", "polynomial_SLP"));
          assert OpenMathBase.parse("prog_body").equals(new OMSymbol("polyslp", "prog_body"));
          assert OpenMathBase.parse("return_node").equals(new OMSymbol("polyslp", "return_node"));
          assert OpenMathBase.parse("right_ref").equals(new OMSymbol("polyslp", "right_ref"));
          assert OpenMathBase.parse("slp_degree").equals(new OMSymbol("polyslp", "slp_degree"));
          assert OpenMathBase.parse("polynomial_ring").equals(new OMSymbol("polysts", "polynomial_ring"));
          assert OpenMathBase.parse("poly_u_rep").equals(new OMSymbol("polyu", "poly_u_rep"));
          assert OpenMathBase.parse("polynomial_ring_u").equals(new OMSymbol("polyu", "polynomial_ring_u"));
          assert OpenMathBase.parse("polynomial_u").equals(new OMSymbol("polyu", "polynomial_u"));
          assert OpenMathBase.parse("exists").equals(new OMSymbol("quant1", "exists"));
          assert OpenMathBase.parse("forall").equals(new OMSymbol("quant1", "forall"));
          assert OpenMathBase.parse("antisymmetric").equals(new OMSymbol("relation0", "antisymmetric"));
          assert OpenMathBase.parse("equivalence").equals(new OMSymbol("relation0", "equivalence"));
          assert OpenMathBase.parse("irreflexive").equals(new OMSymbol("relation0", "irreflexive"));
          assert OpenMathBase.parse("partial_equivalence").equals(new OMSymbol("relation0", "partial_equivalence"));
          assert OpenMathBase.parse("pre_order").equals(new OMSymbol("relation0", "pre_order"));
          assert OpenMathBase.parse("reflexive").equals(new OMSymbol("relation0", "reflexive"));
          assert OpenMathBase.parse("relation").equals(new OMSymbol("relation0", "relation"));
          assert OpenMathBase.parse("strict_order").equals(new OMSymbol("relation0", "strict_order"));
          assert OpenMathBase.parse("transitive").equals(new OMSymbol("relation0", "transitive"));
          assert OpenMathBase.parse("assignment").equals(new OMSymbol("relation1", "assignment"));
          assert OpenMathBase.parse("block").equals(new OMSymbol("relation1", "block"));
          assert OpenMathBase.parse("call_arguments").equals(new OMSymbol("relation1", "call_arguments"));
          assert OpenMathBase.parse("classes").equals(new OMSymbol("relation1", "classes"));
          assert OpenMathBase.parse("def_arguments").equals(new OMSymbol("relation1", "def_arguments"));
          assert OpenMathBase.parse("eq").equals(new OMSymbol("relation1", "eq"));
          assert OpenMathBase.parse("equivalence_closure").equals(new OMSymbol("relation1", "equivalence_closure"));
          assert OpenMathBase.parse("for").equals(new OMSymbol("relation1", "for"));
          assert OpenMathBase.parse("function_block").equals(new OMSymbol("relation1", "function_block"));
          assert OpenMathBase.parse("function_call").equals(new OMSymbol("relation1", "function_call"));
          assert OpenMathBase.parse("function_definition").equals(new OMSymbol("relation1", "function_definition"));
          assert OpenMathBase.parse("geq").equals(new OMSymbol("relation1", "geq"));
          assert OpenMathBase.parse("global_var").equals(new OMSymbol("relation1", "global_var"));
          assert OpenMathBase.parse("gt").equals(new OMSymbol("relation1", "gt"));
          assert OpenMathBase.parse("is_equivalence").equals(new OMSymbol("relation1", "is_equivalence"));
          assert OpenMathBase.parse("is_reflexive").equals(new OMSymbol("relation1", "is_reflexive"));
          assert OpenMathBase.parse("is_relation").equals(new OMSymbol("relation1", "is_relation"));
          assert OpenMathBase.parse("is_symmetric").equals(new OMSymbol("relation1", "is_symmetric"));
          assert OpenMathBase.parse("leq").equals(new OMSymbol("relation1", "leq"));
          assert OpenMathBase.parse("local_var").equals(new OMSymbol("relation1", "local_var"));
          assert OpenMathBase.parse("lt").equals(new OMSymbol("relation1", "lt"));
          assert OpenMathBase.parse("matrix_tensor").equals(new OMSymbol("relation1", "matrix_tensor"));
          assert OpenMathBase.parse("neq").equals(new OMSymbol("relation1", "neq"));
          assert OpenMathBase.parse("procedure_block").equals(new OMSymbol("relation1", "procedure_block"));
          assert OpenMathBase.parse("procedure_call").equals(new OMSymbol("relation1", "procedure_call"));
          assert OpenMathBase.parse("procedure_definition").equals(new OMSymbol("relation1", "procedure_definition"));
          assert OpenMathBase.parse("reflexive_closure").equals(new OMSymbol("relation1", "reflexive_closure"));
          assert OpenMathBase.parse("symmetric_closure").equals(new OMSymbol("relation1", "symmetric_closure"));
          assert OpenMathBase.parse("transitive_closure").equals(new OMSymbol("relation1", "transitive_closure"));
          assert OpenMathBase.parse("vector_tensor").equals(new OMSymbol("relation1", "vector_tensor"));
          assert OpenMathBase.parse("eqs").equals(new OMSymbol("relation4", "eqs"));
          assert OpenMathBase.parse("is_subring").equals(new OMSymbol("ring1", "is_subring"));
          assert OpenMathBase.parse("multiplicative_monoid").equals(new OMSymbol("ring1", "multiplicative_monoid"));
          assert OpenMathBase.parse("negation").equals(new OMSymbol("ring1", "negation"));
          assert OpenMathBase.parse("quaternions").equals(new OMSymbol("ring1", "quaternions"));
          assert OpenMathBase.parse("ring").equals(new OMSymbol("ring1", "ring"));
          assert OpenMathBase.parse("subring").equals(new OMSymbol("ring1", "subring"));
          assert OpenMathBase.parse("free_ring").equals(new OMSymbol("ring3", "free_ring"));
          assert OpenMathBase.parse("integers").equals(new OMSymbol("ring3", "integers"));
          assert OpenMathBase.parse("is_ideal").equals(new OMSymbol("ring3", "is_ideal"));
          assert OpenMathBase.parse("m_poly_ring").equals(new OMSymbol("ring3", "m_poly_ring"));
          assert OpenMathBase.parse("matrix_ring").equals(new OMSymbol("ring3", "matrix_ring"));
          assert OpenMathBase.parse("poly_ring").equals(new OMSymbol("ring3", "poly_ring"));
          assert OpenMathBase.parse("principal_ideal").equals(new OMSymbol("ring3", "principal_ideal"));
          assert OpenMathBase.parse("quotient_ring").equals(new OMSymbol("ring3", "quotient_ring"));
          assert OpenMathBase.parse("is_domain").equals(new OMSymbol("ring4", "is_domain"));
          assert OpenMathBase.parse("is_field").equals(new OMSymbol("ring4", "is_field"));
          assert OpenMathBase.parse("is_maximal_ideal").equals(new OMSymbol("ring4", "is_maximal_ideal"));
          assert OpenMathBase.parse("is_prime_ideal").equals(new OMSymbol("ring4", "is_prime_ideal"));
          assert OpenMathBase.parse("is_zero_divisor").equals(new OMSymbol("ring4", "is_zero_divisor"));
          assert OpenMathBase.parse("quotient_by_poly_map").equals(new OMSymbol("ring5", "quotient_by_poly_map"));
          assert OpenMathBase.parse("quotient_map").equals(new OMSymbol("ring5", "quotient_map"));
          assert OpenMathBase.parse("ceiling").equals(new OMSymbol("rounding1", "ceiling"));
          assert OpenMathBase.parse("floor").equals(new OMSymbol("rounding1", "floor"));
          assert OpenMathBase.parse("round").equals(new OMSymbol("rounding1", "round"));
          assert OpenMathBase.parse("trunc").equals(new OMSymbol("rounding1", "trunc"));
          assert OpenMathBase.parse("median").equals(new OMSymbol("s_data1", "median"));
          assert OpenMathBase.parse("mode").equals(new OMSymbol("s_data1", "mode"));
          assert OpenMathBase.parse("Semigroup").equals(new OMSymbol("semigroup", "Semigroup"));
          assert OpenMathBase.parse("associative").equals(new OMSymbol("semigroup", "associative"));
          assert OpenMathBase.parse("make_Semigroup").equals(new OMSymbol("semigroup", "make_Semigroup"));
          assert OpenMathBase.parse("factor_of").equals(new OMSymbol("semigroup1", "factor_of"));
          assert OpenMathBase.parse("is_subsemigroup").equals(new OMSymbol("semigroup1", "is_subsemigroup"));
          assert OpenMathBase.parse("subsemigroup").equals(new OMSymbol("semigroup1", "subsemigroup"));
          assert OpenMathBase.parse("cyclic_semigroup").equals(new OMSymbol("semigroup3", "cyclic_semigroup"));
          assert OpenMathBase.parse("free_semigroup").equals(new OMSymbol("semigroup3", "free_semigroup"));
          assert OpenMathBase.parse("maps_semigroup").equals(new OMSymbol("semigroup3", "maps_semigroup"));
          assert OpenMathBase.parse("set").equals(new OMSymbol("set1", "set"));
          assert OpenMathBase.parse("lift_binary").equals(new OMSymbol("set2", "lift_binary"));
          assert OpenMathBase.parse("N").equals(new OMSymbol("setname1", "N"));
          assert OpenMathBase.parse("P").equals(new OMSymbol("setname1", "P"));
          assert OpenMathBase.parse("big_intersect").equals(new OMSymbol("setname1", "big_intersect"));
          assert OpenMathBase.parse("big_union").equals(new OMSymbol("setname1", "big_union"));
          assert OpenMathBase.parse("cartesian_power").equals(new OMSymbol("setname1", "cartesian_power"));
          assert OpenMathBase.parse("inversion").equals(new OMSymbol("setname1", "inversion"));
          assert OpenMathBase.parse("k_subsets").equals(new OMSymbol("setname1", "k_subsets"));
          assert OpenMathBase.parse("map_with_condition").equals(new OMSymbol("setname1", "map_with_condition"));
          assert OpenMathBase.parse("map_with_target").equals(new OMSymbol("setname1", "map_with_target"));
          assert OpenMathBase.parse("map_with_target_and_condition").equals(new OMSymbol("setname1", "map_with_target_and_condition"));
          assert OpenMathBase.parse("powerset").equals(new OMSymbol("setname1", "powerset"));
          assert OpenMathBase.parse("subgroup").equals(new OMSymbol("setname1", "subgroup"));
          assert OpenMathBase.parse("A").equals(new OMSymbol("setname2", "A"));
          assert OpenMathBase.parse("Boolean").equals(new OMSymbol("setname2", "Boolean"));
          assert OpenMathBase.parse("GFp").equals(new OMSymbol("setname2", "GFp"));
          assert OpenMathBase.parse("GFpn").equals(new OMSymbol("setname2", "GFpn"));
          assert OpenMathBase.parse("H").equals(new OMSymbol("setname2", "H"));
          assert OpenMathBase.parse("QuotientField").equals(new OMSymbol("setname2", "QuotientField"));
          assert OpenMathBase.parse("Setoid").equals(new OMSymbol("setoid", "Setoid"));
          assert OpenMathBase.parse("make_Setoid").equals(new OMSymbol("setoid", "make_Setoid"));
          assert OpenMathBase.parse("NumericalValue").equals(new OMSymbol("sts", "NumericalValue"));
          assert OpenMathBase.parse("Object").equals(new OMSymbol("sts", "Object"));
          assert OpenMathBase.parse("SetNumericalValue").equals(new OMSymbol("sts", "SetNumericalValue"));
          assert OpenMathBase.parse("attribution").equals(new OMSymbol("sts", "attribution"));
          assert OpenMathBase.parse("binder").equals(new OMSymbol("sts", "binder"));
          assert OpenMathBase.parse("error").equals(new OMSymbol("sts", "error"));
          assert OpenMathBase.parse("nary").equals(new OMSymbol("sts", "nary"));
          assert OpenMathBase.parse("nassoc").equals(new OMSymbol("sts", "nassoc"));
          assert OpenMathBase.parse("structure").equals(new OMSymbol("sts", "structure"));
          assert OpenMathBase.parse("cos").equals(new OMSymbol("transc1", "cos"));
          assert OpenMathBase.parse("cosh").equals(new OMSymbol("transc1", "cosh"));
          assert OpenMathBase.parse("cot").equals(new OMSymbol("transc1", "cot"));
          assert OpenMathBase.parse("coth").equals(new OMSymbol("transc1", "coth"));
          assert OpenMathBase.parse("csc").equals(new OMSymbol("transc1", "csc"));
          assert OpenMathBase.parse("csch").equals(new OMSymbol("transc1", "csch"));
          assert OpenMathBase.parse("exp").equals(new OMSymbol("transc1", "exp"));
          assert OpenMathBase.parse("sec").equals(new OMSymbol("transc1", "sec"));
          assert OpenMathBase.parse("sech").equals(new OMSymbol("transc1", "sech"));
          assert OpenMathBase.parse("sin").equals(new OMSymbol("transc1", "sin"));
          assert OpenMathBase.parse("sinh").equals(new OMSymbol("transc1", "sinh"));
          assert OpenMathBase.parse("tan").equals(new OMSymbol("transc1", "tan"));
          assert OpenMathBase.parse("tanh").equals(new OMSymbol("transc1", "tanh"));
          assert OpenMathBase.parse("unwind").equals(new OMSymbol("transc2", "unwind"));
          assert OpenMathBase.parse("Prop").equals(new OMSymbol("typesorts", "Prop"));
          assert OpenMathBase.parse("Type").equals(new OMSymbol("typesorts", "Type"));
          assert OpenMathBase.parse("Type0").equals(new OMSymbol("typesorts", "Type0"));
          assert OpenMathBase.parse("acre").equals(new OMSymbol("units_imperial1", "acre"));
          assert OpenMathBase.parse("bar").equals(new OMSymbol("units_imperial1", "bar"));
          assert OpenMathBase.parse("degree_Fahrenheit").equals(new OMSymbol("units_imperial1", "degree_Fahrenheit"));
          assert OpenMathBase.parse("foot").equals(new OMSymbol("units_imperial1", "foot"));
          assert OpenMathBase.parse("mile").equals(new OMSymbol("units_imperial1", "mile"));
          assert OpenMathBase.parse("miles_per_hr").equals(new OMSymbol("units_imperial1", "miles_per_hr"));
          assert OpenMathBase.parse("miles_per_hr_sqrd").equals(new OMSymbol("units_imperial1", "miles_per_hr_sqrd"));
          assert OpenMathBase.parse("pint").equals(new OMSymbol("units_imperial1", "pint"));
          assert OpenMathBase.parse("pound_force").equals(new OMSymbol("units_imperial1", "pound_force"));
          assert OpenMathBase.parse("pound_mass").equals(new OMSymbol("units_imperial1", "pound_mass"));
          assert OpenMathBase.parse("yard").equals(new OMSymbol("units_imperial1", "yard"));
          assert OpenMathBase.parse("Coulomb").equals(new OMSymbol("units_metric1", "Coulomb"));
          assert OpenMathBase.parse("Joule").equals(new OMSymbol("units_metric1", "Joule"));
          assert OpenMathBase.parse("Newton").equals(new OMSymbol("units_metric1", "Newton"));
          assert OpenMathBase.parse("Newton_per_sqr_metre").equals(new OMSymbol("units_metric1", "Newton_per_sqr_metre"));
          assert OpenMathBase.parse("Pascal").equals(new OMSymbol("units_metric1", "Pascal"));
          assert OpenMathBase.parse("Watt").equals(new OMSymbol("units_metric1", "Watt"));
          assert OpenMathBase.parse("amp").equals(new OMSymbol("units_metric1", "amp"));
          assert OpenMathBase.parse("degree_Celsius").equals(new OMSymbol("units_metric1", "degree_Celsius"));
          assert OpenMathBase.parse("degree_Kelvin").equals(new OMSymbol("units_metric1", "degree_Kelvin"));
          assert OpenMathBase.parse("gramme").equals(new OMSymbol("units_metric1", "gramme"));
          assert OpenMathBase.parse("litre").equals(new OMSymbol("units_metric1", "litre"));
          assert OpenMathBase.parse("litre_pre1964").equals(new OMSymbol("units_metric1", "litre_pre1964"));
          assert OpenMathBase.parse("metre").equals(new OMSymbol("units_metric1", "metre"));
          assert OpenMathBase.parse("metre_sqrd").equals(new OMSymbol("units_metric1", "metre_sqrd"));
          assert OpenMathBase.parse("metres_per_second").equals(new OMSymbol("units_metric1", "metres_per_second"));
          assert OpenMathBase.parse("metres_per_second_sqrd").equals(new OMSymbol("units_metric1", "metres_per_second_sqrd"));
          assert OpenMathBase.parse("volt").equals(new OMSymbol("units_metric1", "volt"));
          assert OpenMathBase.parse("prefix").equals(new OMSymbol("units_ops1", "prefix"));
          assert OpenMathBase.parse("atto").equals(new OMSymbol("units_siprefix1", "atto"));
          assert OpenMathBase.parse("centi").equals(new OMSymbol("units_siprefix1", "centi"));
          assert OpenMathBase.parse("deci").equals(new OMSymbol("units_siprefix1", "deci"));
          assert OpenMathBase.parse("deka").equals(new OMSymbol("units_siprefix1", "deka"));
          assert OpenMathBase.parse("exa").equals(new OMSymbol("units_siprefix1", "exa"));
          assert OpenMathBase.parse("femto").equals(new OMSymbol("units_siprefix1", "femto"));
          assert OpenMathBase.parse("giga").equals(new OMSymbol("units_siprefix1", "giga"));
          assert OpenMathBase.parse("hecto").equals(new OMSymbol("units_siprefix1", "hecto"));
          assert OpenMathBase.parse("kilo").equals(new OMSymbol("units_siprefix1", "kilo"));
          assert OpenMathBase.parse("mega").equals(new OMSymbol("units_siprefix1", "mega"));
          assert OpenMathBase.parse("micro").equals(new OMSymbol("units_siprefix1", "micro"));
          assert OpenMathBase.parse("milli").equals(new OMSymbol("units_siprefix1", "milli"));
          assert OpenMathBase.parse("nano").equals(new OMSymbol("units_siprefix1", "nano"));
          assert OpenMathBase.parse("peta").equals(new OMSymbol("units_siprefix1", "peta"));
          assert OpenMathBase.parse("pico").equals(new OMSymbol("units_siprefix1", "pico"));
          assert OpenMathBase.parse("tera").equals(new OMSymbol("units_siprefix1", "tera"));
          assert OpenMathBase.parse("yocto").equals(new OMSymbol("units_siprefix1", "yocto"));
          assert OpenMathBase.parse("yotta").equals(new OMSymbol("units_siprefix1", "yotta"));
          assert OpenMathBase.parse("zepto").equals(new OMSymbol("units_siprefix1", "zepto"));
          assert OpenMathBase.parse("zetta").equals(new OMSymbol("units_siprefix1", "zetta"));
          assert OpenMathBase.parse("unit_prefix").equals(new OMSymbol("units_sts", "unit_prefix"));
          assert OpenMathBase.parse("calendar_month").equals(new OMSymbol("units_time1", "calendar_month"));
          assert OpenMathBase.parse("calendar_year").equals(new OMSymbol("units_time1", "calendar_year"));
          assert OpenMathBase.parse("day").equals(new OMSymbol("units_time1", "day"));
          assert OpenMathBase.parse("hour").equals(new OMSymbol("units_time1", "hour"));
          assert OpenMathBase.parse("minute").equals(new OMSymbol("units_time1", "minute"));
          assert OpenMathBase.parse("week").equals(new OMSymbol("units_time1", "week"));
          assert OpenMathBase.parse("acre_us_survey").equals(new OMSymbol("units_us1", "acre_us_survey"));
          assert OpenMathBase.parse("foot_us_survey").equals(new OMSymbol("units_us1", "foot_us_survey"));
          assert OpenMathBase.parse("mile_us_survey").equals(new OMSymbol("units_us1", "mile_us_survey"));
          assert OpenMathBase.parse("pint_us_dry").equals(new OMSymbol("units_us1", "pint_us_dry"));
          assert OpenMathBase.parse("pint_us_liquid").equals(new OMSymbol("units_us1", "pint_us_liquid"));
          assert OpenMathBase.parse("yard_us_survey").equals(new OMSymbol("units_us1", "yard_us_survey"));
          assert OpenMathBase.parse("Laplacian").equals(new OMSymbol("veccalc1", "Laplacian"));
          assert OpenMathBase.parse("curl").equals(new OMSymbol("veccalc1", "curl"));
          assert OpenMathBase.parse("divergence").equals(new OMSymbol("veccalc1", "divergence"));
          assert OpenMathBase.parse("grad").equals(new OMSymbol("veccalc1", "grad"));
        }

        public void testUtf8() throws Exception {
            String s = "<OMV name=\"ü\"/>";
            OpenMathBase om = OpenMathBase.parse(s);
            assert om.equals(new OMVariable("ü"));
            assert s.equals(om.toXml());
        }

        public void testTypedef() throws Exception {
            String s = "$muff::ping.pong";
            OpenMathBase om = null;
            try {
                om = OpenMathBase.parse(s);
                assert false;
            } catch(Exception ignored) {
                assert true;
            }
            PopcornHelper.typeSymbol = new OMSymbol("piff", "puff");
            try {
                om = OpenMathBase.parse(s);
                assert true;
            } catch(Exception ignored) {
                ignored.printStackTrace();
                assert false;
            }
            assert om.getAt(PopcornHelper.typeSymbol).equals(new OMSymbol("ping", "pong"));
            assert om.toPopcorn().equals(s);
        }
        
}