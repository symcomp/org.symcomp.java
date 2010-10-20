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
import org.symcomp.openmath.OpenMathBase;
import org.symcomp.openmath.SymbolRenderer;
import org.symcomp.openmath.AbstractRenderer;
import org.symcomp.openmath.OMInteger;

import java.io.Writer;
import java.io.IOException;

public class Complex1 extends SymbolRenderer {

    public Complex1(AbstractRenderer renderer) {
        super(renderer);
    }

    public int complex_cartesian() { return 70; }
    public void complex_cartesian(OpenMathBase[] params, int prec) throws IOException {
        int iprec = complex_cartesian();
        OpenMathBase real = params[0];
        OpenMathBase imag = params[1];
        if (real.isInteger(0) && imag.isInteger(0) ) {
            out.write("0");
            return;
        }
        if (imag.isInteger(0)) {
            render(real, prec);
            return;
        }
        if (real.isInteger(0)) {
            if (imag.isInteger(1)) {
                out.write("\\mathbf{i}");
            } else {
                render(imag, 80);
                out.write("\\,\\mathbf{i}");
            }
            return;
        }
        if (iprec < prec) out.write("\\left(");
        render(real, 70);
        out.write(" + ");
        if (imag.isInteger(1)) {
            out.write("\\mathbf{i}");
        } else {
            render(imag, 80);
            out.write("\\,\\mathbf{i}");
        }
        if (iprec < prec) out.write("\\right)");
    }

}
