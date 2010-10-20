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

package org.symcomp.openmath.latex.symbols;

import org.symcomp.openmath.*;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Arith1 extends SymbolRenderer {

    public Arith1(AbstractRenderer renderer) { super(renderer); }

    /*
    ***** arith1.plus
     */
    public int prec_plus() { return 70; }
    public void plus() throws IOException { out.write("\\sum"); }
    public void plus(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_plus();
        if (iprec < prec) out.write("\\left(");
        for (int i = 0; i < params.length; i++ ) {
            OpenMathBase param = params[i];
            if (i>0 && param.isApplication("arith1", "unary_minus")) {
                out.write(" - ");
                render(((OMApply)param).getParams()[0], iprec);
            } else {
                if (i > 0)
                    out.write(" + ");
                render(param, iprec);
            }
        }
        if (iprec < prec) out.write("\\right)");
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
        if (iprec < prec) out.write("\\left(");
        out.write("-");
        Class c = param.getClass();
        if (c == OMApply.class) { out.write("\\left("); }
        render(param, iprec);
        if (c == OMApply.class) { out.write("\\right)"); }
        if (iprec < prec) out.write("\\right)");
    }

    /*
    ***** arith1.times
     */
    public int prec_times() { return 80; }
    public void times() throws IOException { out.write("\\prod"); }
    public void times(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_times();
        if (iprec < prec) out.write("\\left(");
        List<OpenMathBase> numers = new ArrayList<OpenMathBase>();
        List<OpenMathBase> denoms = new ArrayList<OpenMathBase>();
        for (int i = 0; i < params.length; i++ ) {
            OpenMathBase param = params[i];
            if (i>0 && param.isApplication("arith2", "inverse")) {
                denoms.add(((OMApply)param).getParam(0));
            } else {
                numers.add(param);
            }
        }
        if (denoms.size() > 0)
            out.write("\\frac{");
        if (numers.size() == 0) {
            out.write("1");
        } else {
            renderNAry(numers.toArray(new OpenMathBase[numers.size()]), " \\cdot ", iprec, 0);
        }
        if (denoms.size() > 0) {
            out.write("}{");
            renderNAry(denoms.toArray(new OpenMathBase[denoms.size()]), " \\cdot ", iprec, 0);
            out.write("}");
        }
        if (iprec < prec) out.write("\\right)");
    }

    /*
    ***** arith1.power
     */
    public int prec_power() { return 90; }
    public void power(OpenMathBase[] params, int prec) throws IOException {
        int iprec = prec_power();
        out.write("{");
        render(params[0], iprec);
        out.write("}^{");
        render(params[1], 0);
        out.write("}");
    }

    /*
    ***** arith1.minus
     */
    public int prec_minus() { return 75; }
    public void minus(OpenMathBase[] params, int prec) throws IOException {
        int iprec = prec_minus();
        if (iprec < prec) out.write("\\left(");
        render(params[0], iprec);
        out.write("-");
        render(params[1], 0);
        if (iprec < prec) out.write("\\right)");
    }

    /*
    ***** arith1.abs
     */
    public void abs(OpenMathBase[] params, int prec) throws IOException {
        if (params.length != 1) throw new RuntimeException("arith1.abs is a unary function.");
        out.write("\\left|");
        render(params[0], 0);
        out.write("\\right|");

    }

    /*
    ***** arith1.root
     */
    public void root(OpenMathBase[] params, int prec) throws IOException {
        if (params[1].isInteger(2)) {
            out.write("\\sqrt{");
        } else {
            out.write("\\sqrt[");
            render(params[1], 0);
            out.write("]{");
        }
        render(params[0], 0);
        out.write("}");
    }

    /*
    ***** arith1.divide
     */
    public int prec_divide() { return 85; }
    public void divide(OpenMathBase[] params, int prec) throws IOException {
        out.write("\\frac{");
        render(params[0], 0);
        out.write("}{");
        render(params[1], 0);
        out.write("}");
    }

    /*
    ***** arith1.sum
     */
    public int prec_sum() { return 75; }
    public void sum() throws IOException { out.write("\\sum"); }
    public void sum(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_sum();
        OMVariable var;
        OpenMathBase l0, l1;
        OpenMathBase expr;
        OMBind func;
        OMApply interval;
        try{
            interval = (OMApply) params[0];
            func = (OMBind) params[1];
            l0 = interval.getParams()[0];
            l1 = interval.getParams()[1];
            var = func.getBvars()[0];
            expr = func.getParam();
        } catch (Exception oops) {
            oops.printStackTrace();
            out.write("[illegal sum syntax]");
            return;
        }
        if (iprec < prec) out.write("\\left(");
        out.write("\\sum_{");
        render(var, 0);
        out.write("=");
        render(l0, 0);
        out.write("}^{");
        render(l1, 0);
        out.write("}");
        render(expr, iprec);
        if (iprec < prec) out.write("\\right)");
    }


    /*
    ***** arith1.prod
     */
    public int prec_product() { return 80; }
    public void product() throws IOException { out.write("\\prod"); }
    public void product(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_product();
        OMVariable var;
        OpenMathBase l0, l1;
        OpenMathBase expr;
        OMBind func;
        OMApply interval;
        try{
            interval = (OMApply) params[0];
            func = (OMBind) params[1];
            l0 = interval.getParams()[0];
            l1 = interval.getParams()[1];
            var = func.getBvars()[0];
            expr = func.getParam();
        } catch (Exception oops) {
            oops.printStackTrace();
            out.write("[illegal product syntax]");
            return;
        }
        if (iprec < prec) out.write("\\left(");
        out.write("\\prod_{");
        render(var, 0);
        out.write("=");
        render(l0, 0);
        out.write("}^{");
        render(l1, 0);
        out.write("}");
        render(expr, iprec);
        if (iprec < prec) out.write("\\right)");
    }


    /*
    ***** arith1 -- simple translations
     */
    public void lcm() throws IOException { out.write("\\lcm"); }
    public void gcd() throws IOException { out.write("\\gcd"); }
}
