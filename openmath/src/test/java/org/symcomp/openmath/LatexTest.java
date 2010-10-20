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

import org.symcomp.openmath.latex.LatexRenderer;

import java.io.*;
import java.nio.charset.Charset;

public class LatexTest {

    static String[][] tests_xml_latex = {
        { "<OMI>1</OMI>", "1" },
        { "<OMI>-1</OMI>", "-1" },
        { "<OMI>123456789012345678901234567890</OMI>", "123456789012345678901234567890" },

        { "<OMF dec='1.23' />", "1.23" },
        { "<OMF dec='-11.23' />", "-11.23"},
        { "<OMF dec='1.2e3' />", "1200.0"},
        { "<OMF dec='1.2e-3' />", "0.0012"},
        { "<OMF dec='1.2e30' />", "1.2E30"},
        { "<OMF dec='1.2e-30' />", "1.2E-30"},

        { "<OMV name=\"a\" />", "{a}"},
        { "<OMV name=\"aa1\" />", "{aa_{1}}"},
        { "<OMV name=\"a1\" />", "{a_{1}}"},
        { "<OMV name=\"aa\" />", "{aa}"},
        { "<OMV name=\"aa11\" />", "{aa_{11}}"},
        { "<OMV name=\"aa-1\" />", "{aa_{-1}}"},
        { "<OMV name=\"alpha\" />", "{\\alpha}"},
        { "<OMV name=\"beta22\" />", "{\\beta_{22}}"},
        { "<OMV name=\"gamma-222\" />", "{\\gamma_{-222}}"},
        { "<OMV name=\"aa22aa\" />", "{aa22aa}"},
        { "<OMV name=\"aa_22_aa\" />", "{aa\\_22\\_aa}"},
        { "<OMV name=\"aa_22\" />", "{aa\\__{22}}"},
        { "<OMV name=\"a+2-a*a\" />", "{a+2-a*a}"},

        { "<OMS cd=\"generic\" name=\"symbol\" />", "\\mathrm{generic.symbol}"},
        { "<OMS cd=\"gen_cd1\" name=\"sym_bol\" />", "\\mathrm{gen\\_cd1.sym\\_bol}"},
        { "<OMS cd=\"transc1\" name=\"sin\" />", "\\sin"},
        { "<OMS cd=\"setname1\" name=\"N\" />", "\\mathbf{N}"},

	    { "<OMS cd=\"nums1\" name=\"i\" />", "\\mathbf{i}" },
	    { "<OMS cd=\"nums1\" name=\"e\" />", "\\mathbf{e}" },
	    { "<OMS cd=\"nums1\" name=\"infinity\" />", "\\infty" },
	    { "<OMS cd=\"nums1\" name=\"pi\" />", "\\pi" },

	    { "<OMS cd=\"setname1\" name=\"N\" />", "\\mathbf{N}" },
	    { "<OMS cd=\"setname1\" name=\"Z\" />", "\\mathbf{Z}" },
	    { "<OMS cd=\"setname1\" name=\"Q\" />", "\\mathbf{Q}" },
	    { "<OMS cd=\"setname1\" name=\"R\" />", "\\mathbf{R}" },
	    { "<OMS cd=\"setname1\" name=\"C\" />", "\\mathbf{C}" },
	
    	{"<OMA><OMS cd=\"piece1\" name=\"piecewise\" /><OMA><OMS cd=\"piece1\" name=\"piece\" /><OMI>1</OMI><OMV name=\"c1\" /></OMA><OMA><OMS cd=\"piece1\" name=\"piece\" /><OMI>2</OMI><OMV name=\"c2\" /></OMA><OMI>3</OMI></OMA>",
            "\\begin{cases}1 & {c_{1}} \\\\ 2 & {c_{2}} \\\\ 3 & \\mathrm{otherwise} \\end{cases}" },
  		{ "<OMA><OMS cd=\"piece1\" name=\"piecewise\" /><OMA><OMS cd=\"piece1\" name=\"piece\" /><OMI>1</OMI><OMV name=\"c1\" /></OMA><OMA><OMS cd=\"piece1\" name=\"piece\" /><OMI>2</OMI><OMV name=\"c2\" /></OMA></OMA>",
            "\\begin{cases}1 & {c_{1}} \\\\ 2 & {c_{2}}\\end{cases}" }
    };
	static String[][] tests_pop_latex = { 
		{ "sum(1 .. infinity, lambda[$x -> 1/$x^2])", "\\sum_{{x}=1}^{\\infty}\\frac{1}{{{x}}^{2}}" },
        { "1//2", "\\frac{1}{2}"},
        { "$a/(-2)", "\\frac{{a}}{-2}"},
        { "$a/$b2", "\\frac{{a}}{{b_{2}}}"},
        { "muff.puff(2,3)", "\\mathrm{muff.puff}\\left(2, 3\\right)"},
        { "transc1.sin(2)", "\\sin\\left(2\\right)"},
        { "set1.set(2,3,4,-5)", "\\left\\{2, 3, 4, -5\\right\\}"},
        { "set1.set()", "\\emptyset"},
	  	{ "1+2+3", "1 + 2 + 3" },
	    { "1+2*3", "1 + 2 \\cdot 3" },
	    { "1*2+3", "1 \\cdot 2 + 3" },
	    { "1*2*3", "1 \\cdot 2 \\cdot 3" },
	    { "1*arith2.inverse(2)*3", "\\frac{1 \\cdot 3}{2}" },
	    { "1*arith2.inverse(2)*3*arith2.inverse(4)", "\\frac{1 \\cdot 3}{2 \\cdot 4}" },
	    { "lambda[$a -> $a*arith2.inverse(2)*3]", "{a}\\mapsto \\frac{{a} \\cdot 3}{2}" },
	    { "1|2", "1 + 2\\,\\mathbf{i}" },
	    { "0|2", "2\\,\\mathbf{i}" },
	    { "1|0", "1" },
	    { "7|1", "7 + \\mathbf{i}" },
	    { "0|1", "\\mathbf{i}" },
	    { "$a = 1", "{a} = 1" },
	    { "$a <> 1", "{a} \\neq 1" },
	    { "$a != 1", "{a} \\neq 1" },
	    { "$a > 1", "{a} > 1" },
	    { "$a < 1", "{a} < 1" },
	    { "$a >= 1", "{a} \\geq 1" },
	    { "$a <= 1", "{a} \\leq 1" },
	    { "$a ==> true", "{a} \\Rightarrow \\mathrm{true}" },
	    { "$a <=> false", "{a} \\iff \\mathrm{false}" },
	    { "$a and false and 7", "{a} \\land \\mathrm{false} \\land 7" },
	    { "$a or false or 7", "{a} \\lor \\mathrm{false} \\lor 7" },
		{ "root(9,2)", "\\sqrt{9}" },
		{ "root(9,3)", "\\sqrt[3]{9}" }
    };

	public void testPop2Latex() throws Exception {
		int cnterr = 0;
		
		for (int i = 0; i < tests_pop_latex.length; ++i) {
			String x = OpenMathBase.parse(tests_pop_latex[i][0]).toLatex();
			if (!(x.equals(tests_pop_latex[i][1]))) {
				++cnterr;
				System.out.println("testPop2Latex() error: ");
				System.out.println("Popcorn: " + tests_pop_latex[i][0]);
				System.out.println("Latex: " + x);
				System.out.println("Should be: " + tests_pop_latex[i][1]);
			}
		}
		
		if (cnterr != 0) throw new RuntimeException("FAILURE in testPop2Latex!");
	}
	
	public void testXml2Latex() throws Exception {
		int cnterr = 0;
		
		for (int i = 0; i < tests_xml_latex.length; ++i) {
			String x = OpenMathBase.parse(tests_xml_latex[i][0]).toLatex();
			if (!(x.equals(tests_xml_latex[i][1]))) {
				++cnterr;
				System.out.println("testXml2Latex() error: ");
				System.out.println("XML: " + tests_xml_latex[i][0]);
				System.out.println("Latex: " + x);
				System.out.println("Should be: " + tests_xml_latex[i][1]);
			}
		}
		
		if (cnterr != 0) throw new RuntimeException("FAILURE in testXml2Latex!");
		
	}	

    public void testListAndSet() throws Exception {
        OpenMathBase omb = OpenMathBase.parse("[1,2,3]");
//        System.out.println(omb.toLatex());
        assert omb.toLatex().equals("\\left[1, 2, 3\\right]");
        omb = OpenMathBase.parse("{1,2,3}");
        assert omb.toLatex().equals("\\left\\{1, 2, 3\\right\\}");
    }

    public void testInt() throws Exception {
        OpenMathBase omb = OpenMathBase.parse("int(lambda[$x -> $x^2])");
        assert omb.toLatex().equals("\\int{{x}}^{2}\\,\\mathrm{d}{x}");
        omb = OpenMathBase.parse("defint(1 .. 3, lambda[$x -> $x^2])");
        assert omb.toLatex().equals("\\int_{1}^{3}{{x}}^{2}\\,\\mathrm{d}{x}");
    }

    public void testDiff() throws Exception {
        OpenMathBase omb = OpenMathBase.parse("diff(lambda[$x -> $x^2+2])");
        assert omb.toLatex().equals("\\frac{\\partial}{\\partial\\,{x}}\\left({{x}}^{2} + 2\\right)");
    }

    public void testProduct() throws Exception {
        OpenMathBase omb = OpenMathBase.parse("product(1 .. $n, lambda[$x -> $x^2+2])");
        //System.out.println(omb.toLatex());
        assert omb.toLatex().equals("\\prod_{{x}=1}^{{n}}\\left({{x}}^{2} + 2\\right)");
    }

    /* Testing printing to and reading from files
	 * And invariance while we're at it.
	 */
	public boolean testFileOut(OpenMathBase om, String latex) {

		File tfile;
		String s;

		try {
	        tfile = File.createTempFile("test", "om_latex");
	        DataOutputStream dos = new DataOutputStream(new FileOutputStream(tfile));
	        Writer out = new OutputStreamWriter(dos, Charset.forName("ISO-8859-1"));
	        om.toLatex(out);
	        out.close();
		} catch (Exception e) {
			throw new RuntimeException("Writing failed!");
		}

		try {
	        DataInputStream dis = new DataInputStream(new FileInputStream(tfile));
	        BufferedReader in = new BufferedReader(new InputStreamReader(dis,Charset.forName("ISO-8859-1")));
	        s = in.readLine();
		} catch (Exception e) {
			throw new RuntimeException("Reading failed!");
		}

        if (!(s.equals(latex))) {
			System.out.println("om = '" + om.toPopcorn() + "'");
			System.out.println("s = '" + s + "'");
			System.out.println("s should be = '" + latex + "'");
			return false;
		}

		tfile.delete();
		return true;
	}

	public void testFileOut() throws Exception {
		for (int i = 0; i < tests_pop_latex.length; ++i) {
			assert testFileOut(OpenMathBase.parse(tests_pop_latex[i][0]), tests_pop_latex[i][1]);
		}
		for (int i = 0; i < tests_xml_latex.length; ++i) {
			assert testFileOut(OpenMathBase.parse(tests_xml_latex[i][0]), tests_xml_latex[i][1]);
		}
	}
}