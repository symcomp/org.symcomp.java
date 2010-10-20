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

package org.symcomp.openmath.popcorn.symbols;

import org.symcomp.openmath.*;
import org.symcomp.openmath.popcorn.PopcornRenderer;

import java.io.Writer;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class Arith1 extends SymbolRenderer {


    public Arith1(AbstractRenderer renderer) { super(renderer); }

    /*
    ***** arith1.plus
     */
    public int prec_plus() { return 70; }
    public void plus() throws IOException { out.write("arith1.plus"); }
    public void plus(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_plus();
        if (iprec < prec) out.write("(");
        for (int i = 0; i < params.length; i++ ) {
            OpenMathBase param = params[i];
            if (i>0 && params.getClass().equals(OMApply.class) && ((OMApply) param).getHead().equals(new OMSymbol("arith1", "unary_minus"))) {
                out.write(" - ");
                render(((OMApply)param).getParams()[0], iprec);
            } else {
                if (i > 0)
                    out.write(" + ");
                render(param, iprec);
            }
        }
        if (iprec < prec) out.write(")");
    }

    /*
    ***** arith1.unary_minus
     */
    public int prec_unary_minus() { return 65; }
    public void unary_minus() throws Exception { out.write("arith1.unary_minus"); }
    public void unary_minus(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_unary_minus();
        if (params.length != 1) throw new Exception ("arith1.unary_minus Must be unary");
        OpenMathBase param = params[0];
        if (iprec < prec) out.write("(");
        out.write("-");
        Class c = param.getClass();
        if (c == OMApply.class) { out.write("("); }
        render(param, iprec);
        if (c == OMApply.class) { out.write(")"); }
        if (iprec < prec) out.write(")");
    }

    /*
    ***** arith1.times
     */
    public int prec_times() { return 80; }
    public void times() throws IOException { out.write("arith1.times"); }
    public void times(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_times();
        if (iprec < prec) out.write("(");
        for (int i = 0; i < params.length; i++ ) {
            OpenMathBase param = params[i];
            if (i>0 && param.isApplication("arith3", "inverse")) {
                out.write("/");
                render(((OMApply)param).getParams()[0], iprec);
            } else {
                if (i > 0)
                    out.write("*");
                render(param, iprec);
            }
        }
        if (iprec < prec) out.write(")");
    }

    /*
    ***** arith1.power
     */
    public int prec_power() { return 90; }
    public void power(OpenMathBase[] params, int prec) throws IOException {
        renderNonAssocBinary(params, "^", prec_power(), prec);
    }

    /*
    ***** arith1.minus
     */
    public int prec_minus() { return 75; }
    public void minus(OpenMathBase[] params, int prec) throws IOException {
        renderNonAssocBinary(params, "-", prec_minus(), prec);
    }

    /*
    ***** arith1.divide
     */
    public int prec_divide() { return 85; }
    public void divide(OpenMathBase[] params, int prec) throws IOException {
        renderNonAssocBinary(params, "/", prec_divide(), prec);
    }


    /*
    ***** arith1 -- simple translations
     */
    public void lcm() throws IOException { out.write("lcm"); }
    public void gcd() throws IOException { out.write("gcd"); }
    public void abs() throws IOException { out.write("abs"); }
    public void root() throws IOException { out.write("root"); }
    public void sum() throws IOException { out.write("sum"); }
    public void product() throws IOException { out.write("product"); }


}
