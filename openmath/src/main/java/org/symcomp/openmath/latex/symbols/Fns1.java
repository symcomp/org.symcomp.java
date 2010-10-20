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

import org.symcomp.openmath.popcorn.PopcornRenderer;
import org.symcomp.openmath.SymbolRenderer;
import org.symcomp.openmath.AbstractRenderer;
import org.symcomp.openmath.OMVariable;
import org.symcomp.openmath.OpenMathBase;

import java.io.Writer;
import java.io.IOException;

public class Fns1 extends SymbolRenderer {

    public Fns1(AbstractRenderer renderer) {
        super(renderer);
    }

    public int prec_lambda() { return 15; }
    public void lambda() throws IOException { out.write("\\lambda"); }
    public void lambda(OMVariable[] bvars, OpenMathBase param, int prec) throws IOException {
        int iprec = prec_lambda();
        if (iprec < prec) out.write("\\left(");
        if (bvars.length > 1) out.write("\\left(");
        renderNAry(bvars, ", ", 1, 0);
        if (bvars.length > 1) out.write("\\right)");
        out.write("\\mapsto ");
        render(param, iprec);
        if (iprec < prec) out.write("\\right)");

    }
}
