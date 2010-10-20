package org.symcomp.openmath;


public class OMForeignTest {
	
	private void cmp(String a, String b) throws Exception {
		if (a.equals(b)) return;
		
		//Print the problem
		System.out.println("  Found:     "  + a);
		System.out.println("  Should be: "  + b);

		//Create the exception and print the stack trace
		RuntimeException e = new RuntimeException("Test failed");
		e.fillInStackTrace();
		System.out.println("   " + e.getStackTrace()[1]);
		System.out.println("   " + e.getStackTrace()[2]);
		throw e;
	}

    public void testOMForeign1() throws Exception {
        OpenMathBase om = OpenMathBase.parse("<OMOBJ><OMFOREIGN><a><muff/>Hund</a></OMFOREIGN></OMOBJ>");
        OMForeign omf = (OMForeign) om.deOMObject();
		cmp(omf.getContent(), "<a><muff/>Hund</a>");
        cmp(om.toXml(), "<OMOBJ><OMFOREIGN><a><muff/>Hund</a></OMFOREIGN></OMOBJ>");
    }

    public void testOMForeign2() throws Exception {
        OpenMathBase om = OpenMathBase.parse("<OMOBJ><OMFOREIGN><a><muff/>Hund<OMFOREIGN>lalala</OMFOREIGN></a></OMFOREIGN></OMOBJ>");
        OMForeign omf = (OMForeign) om.deOMObject();
        cmp(omf.getContent(), "<a><muff/>Hund<OMFOREIGN>lalala</OMFOREIGN></a>");
        cmp(om.toXml(), "<OMOBJ><OMFOREIGN><a><muff/>Hund<OMFOREIGN>lalala</OMFOREIGN></a></OMFOREIGN></OMOBJ>");
    }

    public void testOMForeign3() throws Exception {
        OpenMathBase om = OpenMathBase.parse("<OMOBJ><OMFOREIGN><a><muff/>Hund<?php $a = 1; ?><OMI>3</OMI></a></OMFOREIGN></OMOBJ>");
        OMForeign omf = (OMForeign) om.deOMObject();
        cmp(omf.getContent(), "<a><muff/>Hund<?php $a = 1; ?><OMI>3</OMI></a>");
        cmp(om.toXml(), "<OMOBJ><OMFOREIGN><a><muff/>Hund<?php $a = 1; ?><OMI>3</OMI></a></OMFOREIGN></OMOBJ>");
    }

    public void testOMForeign4() throws Exception {
        OpenMathBase om = OpenMathBase.parse("1+2+`<a>muff</a>`");
        OMForeign omf = (OMForeign) ((OMApply) om.deOMObject()).getParam(2);
        cmp(omf.getContent(), "<a>muff</a>");
    }

    public void testOMForeign5() throws Exception {
        OpenMathBase om = OpenMathBase.parse("1 + 2 + `<a>muff</a>`");
        cmp(om.toPopcorn(), "1 + 2 + `<a>muff</a>`");
    }

    public void testOMForeign6() throws Exception {
        OpenMathBase om = OpenMathBase.parse("1 + 2 + `<a>muff</a>`");
        cmp(om.toLatex(), "1 + 2 + \\%FOREIGN\\%");
    }

    public void testOMForeign7() throws Exception {
        OpenMathBase om = OpenMathBase.parse("1 + 2 + `xhtml strict 1.0<a>muff</a>`");
        cmp(om.toPopcorn(), "1 + 2 + `xhtml strict 1.0<a>muff</a>`");
    }

    public void testOMForeign8() throws Exception {
        OpenMathBase om = OpenMathBase.parse("1 + 2 + `xhtml strict 1.0<a>muff</a>`");
        cmp(om.toXml(), "<OMA><OMS cd=\"arith1\" name=\"plus\"/><OMI>1</OMI><OMI>2</OMI><OMFOREIGN encoding=\"xhtml strict 1.0\"><a>muff</a></OMFOREIGN></OMA>");
    }

    public void testOMForeign9() throws Exception {
        OpenMathBase om = OpenMathBase.parse("<OMOBJ><OMFOREIGN encoding=\"xnonsense 2.0\"><a><muff/>Hund<?php $a = 1; ?><OMI>3</OMI></a></OMFOREIGN></OMOBJ>");
        OMForeign omf = (OMForeign) om.deOMObject();
        cmp(omf.getContent(), "<a><muff/>Hund<?php $a = 1; ?><OMI>3</OMI></a>");
        cmp(omf.getEncoding(), "xnonsense 2.0");
        cmp(om.toXml(), "<OMOBJ><OMFOREIGN encoding=\"xnonsense 2.0\"><a><muff/>Hund<?php $a = 1; ?><OMI>3</OMI></a></OMFOREIGN></OMOBJ>");
        cmp(om.toPopcorn(), "`xnonsense 2.0<a><muff/>Hund<?php $a = 1; ?><OMI>3</OMI></a>`");
    }

	public void testOMForeignBinaryEnc() throws Exception {
		OpenMathBase b1 = OpenMathBase.parse("1 + 2 + `xhtml strict 1.0<a><b>muff</b></a><c/>`:xx + 3:yy");
		char[] c = b1.toBinary();
		OpenMathBase b2 = OpenMathBase.parseBinary(new String(c)).deOMObject();
		
		assert b1.equals(b2);
	}
}
