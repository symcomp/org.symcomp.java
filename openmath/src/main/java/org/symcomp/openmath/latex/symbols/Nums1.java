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

import org.symcomp.openmath.OpenMathBase;
import org.symcomp.openmath.SymbolRenderer;
import org.symcomp.openmath.AbstractRenderer;
import org.symcomp.openmath.popcorn.PopcornRenderer;

import java.io.Writer;
import java.io.IOException;

public class Nums1 extends SymbolRenderer {

    public Nums1(AbstractRenderer renderer) {
        super(renderer);
    }

    /*
    ***** nums1.rational
     */
    public int prec_rational() { return 100; }
    public void rational(OpenMathBase[] params, int prec) throws IOException {
        out.write("\\frac{");
        render(params[0], 0);
        out.write("}{");
        render(params[1], 0);
        out.write("}");
    }

    /*
    ***** nums1 constants
     */
    public void e() throws IOException { out.write("\\mathbf{e}"); }
    public void pi() throws IOException { out.write("\\pi"); }
    public void i() throws IOException { out.write("\\mathbf{i}"); }
    public void infinity() throws IOException { out.write("\\infty"); }

}
