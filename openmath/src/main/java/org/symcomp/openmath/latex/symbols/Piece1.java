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

import java.io.Writer;
import java.io.IOException;

public class Piece1 extends SymbolRenderer{
    public Piece1(AbstractRenderer renderer) {
        super(renderer);
    }

    public void piecewise(OpenMathBase[] params, int prec) throws IOException {
        out.write("\\begin{cases}");
        for (int i = 0; i<params.length; i++ ) {
            if (params[i].isApplication("piece1", "piece")) {
                OMApply param = (OMApply) params[i];
                render(param.getParam(0), 0);
                out.write(" & ");
                render(param.getParam(1), 0);
            } else {
                render(params[i], 0);
                out.write(" & \\mathrm{otherwise} ");
            }
            if (i < params.length-1)
                out.write(" \\\\ ");
        }
        out.write("\\end{cases}");
    }

}
