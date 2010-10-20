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

package org.symcomp.openmath;

import java.io.Writer;
import java.io.IOException;

public abstract class SymbolRenderer {

    protected AbstractRenderer renderer;
	protected Writer out;

    public SymbolRenderer(AbstractRenderer renderer) {
        this.renderer = renderer;
		this.out = renderer.getOut();
    }

    protected void render(OpenMathBase om, int prec) throws IOException {
        renderer.render(om, prec);
    }

    protected void renderBinary(OpenMathBase[] params, String operator, int iprec, int prec) throws IOException {
        renderer.renderBinary(params, operator, iprec, prec);
    }

    protected void renderNonAssocBinary(OpenMathBase[] params, String operator, int iprec, int prec) throws IOException {
        renderer.renderNonAssocBinary(params, operator, iprec, prec);
    }

    protected void renderNAry(OpenMathBase[] params, String operator, int iprec, int prec) throws IOException {
        renderer.renderNAry(params, operator, iprec, prec);
    }

    protected void renderNAry(OpenMathBase[] params, String operator, int iprec, int prec, boolean assoc) throws IOException {
        renderer.renderNAry(params, operator, iprec, prec, assoc);
    }

}
