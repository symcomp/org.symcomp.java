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

package org.symcomp.openmath.popcorn;

import org.symcomp.openmath.*;

import java.io.*;
import java.math.BigInteger;
import java.util.Map;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Class that renders an OpenMathBase tree to a POPCORN expression.
 * Don't use, use openMathBase.toPopcorn() instead.
 */
public class PopcornRenderer extends AbstractRenderer {

	protected Pattern idDoesntNeedEscape;
	protected final String ID_DOESNT_NEED_ESCAPE = "^[A-Za-z][A-Za-z0-9_]*$";

    public PopcornRenderer(Writer out) throws Exception {
        super("org.symcomp.openmath.popcorn.symbols", out);
		idDoesntNeedEscape = Pattern.compile(ID_DOESNT_NEED_ESCAPE);
    }

	protected String escapeIfNeeded(String s) {
		return (idDoesntNeedEscape.matcher(s).matches() ? s : "'" + s + "'");
	}

    protected void render(OpenMathBase om, int prec) throws IOException {
		//Main contents
        super.render(om, prec);

		//Attributions
		Map<String,OpenMathBase[]> attrs = om.getAttributions();
		if (attrs.size() > 0) {
                    if (attrs.size() == 1
                            && PopcornHelper.typeSymbol != null
                            && om.getAt(PopcornHelper.typeSymbol) != null) {
                        out.write("::");
                        render(om.getAt(PopcornHelper.typeSymbol),0);
                    } else {
			out.write("{");
			Iterator it = attrs.values().iterator();
                        while (it.hasNext()) {
                            OpenMathBase[] kvp = (OpenMathBase[]) it.next();
                            render(kvp[0], 0);
                            out.write("->");
                            render(kvp[1], 0);
                            if (it.hasNext()) out.write(",");
                        }
    			out.write("}");
                    }
               }
		//id
        if (om.getId() != null) {
            out.write(":");
            out.write(escapeIfNeeded(om.getId()));
        }
    }

    /*
    ***** OMObject
     */
    protected void render(OMObject omobj, int prec) throws IOException {
        render(omobj.getElement(), 0);
    }

    /*
    ***** OMInteger
     */
    protected void render(OMInteger omi, int prec) throws IOException {
		String strv = omi.getStrValue();
		if (strv.startsWith("x")) {
			out.write("0"); out.write(strv);
		} else if (omi.getIntValue().compareTo(BigInteger.ZERO) >= 0) {
            out.write(strv);
        } else {
            out.write("(");
            out.write(strv);
            out.write(")");
        }
    }

    /*
    ***** OMVariable
     */
    protected void render(OMVariable omv, int level) throws IOException {
        out.write("$");
        out.write(escapeIfNeeded(omv.getName()));
    }

    /*
    ***** OMFloat
     */
    protected void render(OMFloat omf, int level) throws IOException {
		if (omf.getDec() == null) {
            out.write("0f"); out.write(omf.getHex());
		} else if (omf.getDec() >= 0.0) {
            out.write(omf.getDec().toString());
        } else {
            out.write("(");
            out.write(omf.getDec().toString());
            out.write(")");
        }
    }

    /*
    ***** OMSymbol
     */
    protected void render(OMSymbol oms, int level) throws IOException {
        if (renderSymbol(oms)) return;
        out.write(escapeIfNeeded(oms.getCd().trim()));
        out.write(".");
        out.write(escapeIfNeeded(oms.getName().trim()));
    }

    /*
    ***** OMReference
     */
    protected void render(OMReference omr, int level) throws IOException {
        String ref = omr.getHref();
        if(ref.substring(0,1).equals("#")) {
            out.write("#");
            out.write(escapeIfNeeded(ref.substring(1)));
        } else {
            out.write("##");
            out.write(omr.getHref());
            out.write("##");
        }
    }

    /*
    ***** OMString
     */
    protected void render(OMString omstr, int level) throws IOException {
		String s = omstr.getValue();
		s = s.replace("\\", "\\\\");
		s = s.replace("\"", "\\\"");
        out.write("\"");
        out.write(s);
        out.write("\"");
    }

    /*
    ***** OMBind
     */
    protected void render(OMBind ombind, int level) throws IOException {
        OMSymbol oms = ombind.getSymbol();
        OMVariable[] bvars = ombind.getBvars();
        OpenMathBase param = ombind.getParam();
        if (renderBind(oms, bvars, param, level)) return;
        render(oms, 0);
        out.write("[");
        renderNAry(bvars, ",", 1, 0);
        out.write(" -> ");
        render(param, 0);
        out.write("]");
    }

    /*
    ***** OMApply
     */
    protected void render(OMApply oma, int level) throws IOException {
        OpenMathBase head = oma.getHead();
        OpenMathBase[] params = oma.getParams();

		//This is somewhat unfortunate if there are attributes, because there may be
		//  superfluous brackets. However, the (possible) generality of custom renderers
		//  more or less necessitates this.
		if (oma.isAttributed()) out.write("(");
        if (head.getClass().equals(OMSymbol.class) && renderApply( (OMSymbol) head, params, level)) {
			//This has been handled by a custom symbolrenderer
			if (oma.isAttributed()) out.write(")");
			return;
		}
		
        render(head, 0);
        out.write("(");
        renderNAry(params, ", ", 1, 0);
        out.write(")");

		if (oma.isAttributed()) out.write(")");
    }

    /*
    ***** OMError
     */
    protected void render(OMError ome, int level) throws IOException {
        OpenMathBase head = ome.getHead();
        OpenMathBase[] params = ome.getParams();
        render(head, 0);
        out.write("!(");
        renderNAry(params, ", ", 1, 0);
        out.write(")");
    }

    /*
    ***** OMBinary
     */
    protected void render(OMBinary omb, int level) throws IOException {
        out.write("%");
		out.write(omb.getBase64Value());
        out.write("%");
    }

    /*
    ***** OMForeign
     */
    protected void render(OMForeign omf, int level) throws IOException {
        out.write("`");
        if (omf.getEncoding() != null)
            out.write(omf.getEncoding());
		out.write(omf.getContent());
        out.write("`");
    }
}