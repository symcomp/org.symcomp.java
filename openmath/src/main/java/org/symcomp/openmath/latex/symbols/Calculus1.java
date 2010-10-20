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

public class Calculus1 extends SymbolRenderer {

    public Calculus1(AbstractRenderer renderer) { super(renderer); }

    /*
    ***** calculus1.diff
     */
    public int prec_diff() { return 76; }
    public void diff() throws IOException { out.write("\\partial"); }
    public void diff(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_diff();
        OMVariable var;
        OpenMathBase expr;
        OMBind func;
        try{
            func = (OMBind) params[0];
            var = func.getBvars()[0];
            expr = func.getParam();
        } catch (Exception oops) {
            oops.printStackTrace();
            out.write("[illegal diff syntax]");
            return;
        }
        if (iprec < prec) out.write("\\left(");
        out.write("\\frac{\\partial}{\\partial\\,");
        render(var, 0);
        out.write("}");
        render(expr, iprec);
        if (iprec < prec) out.write("\\right)");
    }



    /*
    ***** calculus1.int
     */
    public int prec_int_() { return 76; }
    public void int_() throws IOException { out.write("\\int"); }
    public void int_(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_int_();
        OMVariable var;
        OpenMathBase expr;
        OMBind func;
        try{
            func = (OMBind) params[0];
            var = func.getBvars()[0];
            expr = func.getParam();
        } catch (Exception oops) {
            oops.printStackTrace();
            out.write("[illegal int syntax]");
            return;
        }
        if (iprec < prec) out.write("\\left(");
        out.write("\\int");
        render(expr, iprec);
        out.write("\\,\\mathrm{d}");
        render(var, 0);
        if (iprec < prec) out.write("\\right)");
    }

    /*
    ***** calculus1.defint
     */
    public int prec_defint() { return 76; }
    public void defint() throws IOException { out.write("\\int"); }
    public void defint(OpenMathBase[] params, int prec) throws Exception {
        int iprec = prec_defint();
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
            out.write("[illegal int syntax]");
            return;
        }
        if (iprec < prec) out.write("\\left(");
        out.write("\\int_{");
        render(l0, 0);
        out.write("}^{");
        render(l1, 0);
        out.write("}");
        render(expr, iprec);
        out.write("\\,\\mathrm{d}");
        render(var, 0);
        if (iprec < prec) out.write("\\right)");
    }

}
