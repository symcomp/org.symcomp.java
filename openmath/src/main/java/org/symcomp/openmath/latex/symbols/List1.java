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

import java.io.Writer;
import java.io.IOException;

public class List1 extends SymbolRenderer {

    public List1(AbstractRenderer renderer) {
        super(renderer);
    }

    /*
    ***** list1.list
     */
    public int prec_list() { return 65; }
    public void list(OpenMathBase[] params, int prec) throws IOException {
        out.write("\\left[");
        renderNAry(params, ", ", 1, 0);
        out.write("\\right]");
    }


}
