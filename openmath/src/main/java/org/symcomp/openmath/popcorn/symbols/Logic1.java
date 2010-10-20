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

public class Logic1 extends SymbolRenderer {

    public Logic1(AbstractRenderer renderer) {
        super(renderer);
    }

    /*
    ***** logic1.implies
     */
    public int prec_implies() { return 30; }
    public void implies(OpenMathBase[] params, int prec) throws IOException {
        renderBinary(params, " ==> ", prec_implies(), prec);
    }

    /*
    ***** logic1.equivalent
     */
    public int prec_equivalent() { return 30; }
    public void equivalent(OpenMathBase[] params, int prec) throws IOException {
        renderBinary(params, " <=> ", prec_equivalent(), prec);
    }

    /*
    ***** logic1.or
     */
    public int prec_or() { return 40; }
    public void or(OpenMathBase[] params, int prec) throws IOException {
        renderNAry(params, " or ", prec_or(), prec);
    }

    /*
    ***** logic1.and
     */
    public int prec_and() { return 50; }
    public void and(OpenMathBase[] params, int prec) throws IOException {
        renderNAry(params, " and ", prec_and(), prec);
    }

    /*
    ***** logic1.true
     */
    public int prec_true_() { return 40; }
    public void true_() throws IOException { out.write("true"); }

    /*
    ***** logic1.false
     */
    public int prec_false_() { return 40; }
    public void false_() throws IOException { out.write("false"); }

    /*
    ***** logic1.not
     */
    public void not() throws IOException { out.write("not"); }

}
