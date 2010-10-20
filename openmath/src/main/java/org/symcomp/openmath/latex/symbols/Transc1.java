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

import org.symcomp.openmath.SymbolRenderer;
import org.symcomp.openmath.AbstractRenderer;

import java.io.IOException;
import java.io.Writer;

public class Transc1 extends SymbolRenderer {

    public Transc1(AbstractRenderer renderer) {
        super(renderer);
    }

    public void arccos() throws IOException { out.write("\\arccos"); }
    public void arccosh() throws IOException { out.write("\\arccosh"); }
    public void arccot() throws IOException { out.write("\\arccot"); }
    public void arccoth() throws IOException { out.write("\\arccoth"); }
    public void arccsc() throws IOException { out.write("\\arccsc"); }
    public void arccsch() throws IOException { out.write("\\arccsch"); }
    public void arcsec() throws IOException { out.write("\\arcsec"); }
    public void arcsech() throws IOException { out.write("\\arcsech"); }
    public void arcsin() throws IOException { out.write("\\arcsin"); }
    public void arcsinh() throws IOException { out.write("\\arcsinh"); }
    public void arctan() throws IOException { out.write("\\arctan"); }
    public void arctanh() throws IOException { out.write("\\arctanh"); }
    public void cos() throws IOException { out.write("\\cos"); }
    public void cosh() throws IOException { out.write("\\cosh"); }
    public void cot() throws IOException { out.write("\\cot"); }
    public void coth() throws IOException { out.write("\\coth"); }
    public void csc() throws IOException { out.write("\\csc"); }
    public void csch() throws IOException { out.write("\\csch"); }
    public void exp() throws IOException { out.write("\\exp"); }
    public void ln() throws IOException { out.write("\\ln"); }
    public void log() throws IOException { out.write("\\log"); }
    public void sec() throws IOException { out.write("\\sec"); }
    public void sech() throws IOException { out.write("\\sech"); }
    public void sin() throws IOException { out.write("\\sin"); }
    public void sinh() throws IOException { out.write("\\sinh"); }
    public void tan() throws IOException { out.write("\\tan"); }
    public void tanh() throws IOException { out.write("\\tanh"); }

}
