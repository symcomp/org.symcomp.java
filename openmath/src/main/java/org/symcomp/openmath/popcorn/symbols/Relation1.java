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

import org.symcomp.openmath.popcorn.PopcornRenderer;
import org.symcomp.openmath.OpenMathBase;
import org.symcomp.openmath.SymbolRenderer;
import org.symcomp.openmath.AbstractRenderer;

import java.io.Writer;
import java.io.IOException;

public class Relation1 extends SymbolRenderer {

    public Relation1(AbstractRenderer renderer) {
        super(renderer);
    }

    /*
    ***** relation1.eq
     */
    public int prec_eq() { return 60; }
    public void eq(OpenMathBase[] params, int prec) throws IOException {
        renderBinary(params, " = ", prec_eq(), prec);
    }

    /*
    ***** relation1.neq
     */
    public int prec_neq() { return 60; }
    public void neq(OpenMathBase[] params, int prec) throws IOException {
        renderBinary(params, " != ", prec_neq(), prec);
    }

    /*
    ***** relation1.gt
     */
    public int prec_gt() { return 60; }
    public void gt(OpenMathBase[] params, int prec) throws IOException {
        renderBinary(params, " > ", prec_gt(), prec);
    }

    /*
    ***** relation1.geq
     */
    public int prec_geq() { return 60; }
    public void geq(OpenMathBase[] params, int prec) throws IOException {
        renderBinary(params, " >= ", prec_geq(), prec);
    }

    /*
    ***** relation1.lt
     */
    public int prec_lt() { return 60; }
    public void lt(OpenMathBase[] params, int prec) throws IOException {
        renderBinary(params, " < ", prec_lt(), prec);
    }

    /*
    ***** relation1.leq
     */
    public int prec_leq() { return 60; }
    public void leq(OpenMathBase[] params, int prec) throws IOException {
        renderBinary(params, " <= ", prec_leq(), prec);
    }

}
