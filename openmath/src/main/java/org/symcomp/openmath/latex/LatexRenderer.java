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

package org.symcomp.openmath.latex;

import org.symcomp.openmath.*;

import java.util.*;
import java.io.*;
import java.math.BigInteger;

/**
 * Class that renders an OpenMathBase tree to a POPCORN expression.
 * Don't use, use openMathBase.toPopcorn() instead.
 */
public class LatexRenderer extends AbstractRenderer {

    public LatexRenderer(Writer out) throws Exception {
        super("org.symcomp.openmath.latex.symbols", out);
    }

    /*
    ***** OMObject
     */
    protected void render(OMObject omobj, int prec) throws IOException {
        render(omobj.getElement(), 0);
    }

    /*
    ***** OMInteger
     */
    protected void render(OMInteger omi, int prec) throws IOException {
		String strv = omi.getStrValue();
		if (strv.startsWith("x")) {
			out.write("0"); out.write(strv);
		} else {
            out.write(strv);
        }
    }

    /*
    ***** OMVariable
     */
    private static HashSet<String> greekVars = new HashSet<String>(Arrays.asList("Alpha", "Beta", "Gamma", "Delta",
            "Epsilon", "Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "My", "Ny", "Xi", "Omikron", "Pi", "Rho",
            "Sigma", "Tau", "Ypsilon", "Phi", "Chi", "Psi", "Omega", "alpha", "beta", "gamma", "delta", "epsilon",
            "zeta", "eta", "theta", "iota", "kappa", "lambda", "my", "ny", "xi", "omikron", "pi", "rho", "sigma",
            "tau", "ypsilon", "phi", "chi", "psi", "omega"));
    protected void render(OMVariable omv, int level) throws IOException {
        String nam = omv.getName().replace("_", "\\_");
        String ind = null;
        int n = nam.length()-1;
        while (nam.charAt(n) > '0' && nam.charAt(n) < '9') n--;
        if (n < nam.length()-1) {
            if (nam.charAt(n) == '-') n--;
            ind = nam.substring(n+1, nam.length());
            nam = nam.substring(0, n+1);
        }
        if (greekVars.contains(nam)) nam = "\\"+nam;
        out.write("{");
        out.write(nam);
        if (null != ind) {
            out.write("_{");
            out.write(ind);
            out.write("}");
        }
        out.write("}");

    }

    /*
    ***** OMFloat
     */
	protected void render(OMFloat omf, int level) throws IOException {
		if (omf.getDec() == null) {
	        out.write("0f"); out.write(omf.getHex());
		} else {
	        out.write(omf.getDec().toString());
	    }
	}
	
    /*
    ***** OMSymbol
     */
    protected void render(OMSymbol oms, int level) throws IOException {
        if (renderSymbol(oms)) return;
        out.write("\\mathrm{");
        out.write(oms.getCd().trim().replace("_", "\\_"));
        out.write(".");
        out.write(oms.getName().trim().replace("_", "\\_"));
        out.write("}");
    }

    /*
    ***** OMReference
     */
    protected void render(OMReference omr, int level) throws IOException {
        //out.write("");
        String ref = omr.getHref();
        if(ref.substring(0,1).equals("#")) {
            out.write("{");
            out.write(ref.substring(1));
            out.write("}");
        } else {
            out.write("\\mbox{");
            out.write(omr.getHref());
            out.write("}");
        }
    }

    /*
    ***** OMString
     */
    protected void render(OMString omstr, int level) throws IOException {
        out.write("'");
        out.write(omstr.getValue().replace("_", "\\_").replace(" ", "\\ "));
        out.write("'");
    }

    /*
    ***** OMBind
     */
    protected void render(OMBind ombind, int level) throws IOException {
        OMSymbol oms = ombind.getSymbol();
        OMVariable[] bvars = ombind.getBvars();
        OpenMathBase param = ombind.getParam();
        if (renderBind(oms, bvars, param, level)) return;
        render(oms, 0);
        out.write("_{");
        renderNAry(bvars, ",", 1, 0);
        out.write("}\\left(");
        render(param, 0);
        out.write("\\right)");
    }

    /*
    ***** OMApply
     */
    protected void render(OMApply oma, int level) throws IOException {
        OpenMathBase head = oma.getHead();
        OpenMathBase[] params = oma.getParams();
        if (head.getClass().equals(OMSymbol.class) && renderApply((OMSymbol) head, params, level)) return;
        render(head, 0);
        out.write("\\left(");
        renderNAry(params, ", ", 1, 0);
        out.write("\\right)");
    }

    /*
    ***** OMError
     */
    protected void render(OMError ome, int level) throws IOException {
        OpenMathBase head = ome.getHead();
        OpenMathBase[] params = ome.getParams();
        render(head, 0);
        out.write("!\\left(");
        renderNAry(params, ", ", 1, 0);
        out.write("\\right)");
    }

    /*
    ***** OMBinary
     */
    protected void render(OMBinary omb, int level) throws IOException {
        out.write("\\%BINARY\\%");
    }

    /*
    ***** OMForeign
     */
    protected void render(OMForeign omf, int level) throws IOException {
        out.write("\\%FOREIGN\\%");
    }


}