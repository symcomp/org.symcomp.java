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
import org.symcomp.openmath.*;

import java.io.Writer;
import java.io.IOException;

public class Prog1 extends SymbolRenderer {

    public Prog1(AbstractRenderer renderer) {
        super(renderer);
    }

    /*
    ***** prog1.block
     */
    public int prec_block() { return 10; }
    public void block() throws IOException { out.write("prog1.block"); }
    public void block(OpenMathBase[] params, int prec) throws Exception {
        renderNAry(params, "; ", prec_block(), prec);
    }

    /*
    ***** prog1.assign
     */
    public int prec_assign() { return 20; }
    public void assign(OpenMathBase[] params, int prec) throws IOException {
        renderBinary(params, " := ", prec_assign(), prec);
    }


}
