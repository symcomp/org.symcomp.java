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

import java.util.*;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public abstract class AbstractRenderer {
	//These are all cached things. However, as they are defined inside the AbstractRenderer, 
	//  we need to take measures to ensure that a separate HashMap is kept for every
	//  subclass... Fortunately, we have the "pack" variable to sort that out.
    protected static HashMap<String, HashMap<String, Constructor> > symbolRendererConstructors;
    protected static HashMap<String, HashMap<String, Method> > precMethods;
    protected static HashMap<String, HashMap<String, Method> > applicationMethods;
    protected static HashMap<String, HashMap<String, Method> > bindMethods;
    protected static HashMap<String, HashMap<String, Method> > symbolMethods;

	//This hashmap, however, are non-static, as these symbolRenderers are 
	//   constructed using an AbstractRenderer's instance's "out".
	protected HashMap<String, SymbolRenderer> symbolRenderers;
	protected String pack;
    protected Writer out;

    /**
     * Registers all methods from the classes within the package with the
     * symbolic name pack to render symbols.
     * @param pack Symbolic name of the package containing the classes
     * @param out The writer to which to write
     * @throws Exception
     */
    protected AbstractRenderer(String pack, Writer out) throws Exception {
        this.out = out;
		this.pack = pack;
        initialize(pack);
		symbolRenderers = new HashMap<String, SymbolRenderer>();
    }

    private static synchronized void initialize(String pack) throws Exception {
		if (symbolRendererConstructors == null) {
			//This only happens once
		    symbolRendererConstructors = new HashMap<String, HashMap<String, Constructor> >();
		    precMethods = new HashMap<String, HashMap<String, Method> >();
		    applicationMethods = new HashMap<String, HashMap<String, Method> >();
		    bindMethods = new HashMap<String, HashMap<String, Method> >();
		    symbolMethods = new HashMap<String, HashMap<String, Method> >();	
		}
		
		if (symbolRendererConstructors.get(pack) != null) return; 
		
		//This happens once for each pack
	    symbolRendererConstructors.put(pack, new HashMap<String, Constructor>());
	    precMethods.put(pack, new HashMap<String, Method>());
	    applicationMethods.put(pack, new HashMap<String, Method>());
	    bindMethods.put(pack, new HashMap<String, Method>());
	    symbolMethods.put(pack, new HashMap<String, Method>());
	
        try {
            for (Class c : Tools.getClassesForPackage(pack)) {
                // System.out.println("Registering " + c.getCanonicalName());

				//Store the constructor
                String nam = c.getSimpleName().toLowerCase();
                Constructor cstr = c.getConstructor(AbstractRenderer.class);
                symbolRendererConstructors.get(pack).put(c.getSimpleName().toLowerCase(), cstr);

				//Traverse methods, and store the intersting ones
                Method[] ms = c.getMethods();
                for (int i=0; i < ms.length; i++) {
                    Method m = ms[i];
                    Class[] params = m.getParameterTypes();
                    String s = nam + "." + m.getName();
                    if (params.length == 0 && m.getReturnType().equals(int.class) && m.getName().startsWith("prec_")) {
						String s0 = nam + "." + m.getName().substring(5);
                        // System.out.println("Registering Precedence-Method for "+s0);
                        precMethods.get(pack).put(s0, m);
                    } else if (params.length == 0 && m.getReturnType().equals(void.class)) {
                        // System.out.println("Registering Symbol-Method for '"+s+"'");
                        symbolMethods.get(pack).put(s, m);
                    } else if (params.length == 2 && m.getReturnType().equals(void.class) && params[0] == OpenMathBase[].class && params[1] == int.class) {
                        // System.out.println("Registering Application-Method for "+s);
                        applicationMethods.get(pack).put(s, m);
                    } else if (params.length == 3 && m.getReturnType().equals(void.class) && params[0] == OMVariable[].class && params[1] == OpenMathBase.class && params[2] == int.class) {
                        // System.out.println("Registering Bind-Method for "+s);
                        bindMethods.get(pack).put(s, m);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new Exception("Initializing failed!");
        }
    } // initialize

	/**
	 *@return this AbstractRenderer's Writer
	 */
	public Writer getOut() { return out; }

	/**
	 *@return A SymbolRenderer (with this AbstractRenderer as parent) for the specified cd
	 */
	private SymbolRenderer getSymbolRenderer(String cd) {
		//If we have one for this cd: That's easy.
		SymbolRenderer r = symbolRenderers.get(cd); 
		if (r != null) return r;
		
		//Otherwise, we'll try and construct one...
		Constructor c = symbolRendererConstructors.get(pack).get(cd);
		if (c == null) return null;
		
		//... and cache that.
		try {
        	r = (SymbolRenderer) c.newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't construct new instance for " + c.toString() + "(" + e.getMessage() + ")");
        }
			
		symbolRenderers.put(cd, r);
		return r;
	}


    /*
    ***** OpenMathBase
     */
    protected void render(OpenMathBase om, int prec) throws IOException {
        Class c = om.getClass();
        if (c.equals(OMObject.class)) {
                render((OMObject) om, prec);
        } else if (c.equals(OMInteger.class)) {
                render((OMInteger) om, prec);
        } else if (c.equals(OMVariable.class)) {
                render((OMVariable) om, prec);
        } else if (c.equals(OMFloat.class)) {
                render((OMFloat) om, prec);
        } else if (c.equals(OMSymbol.class)) {
                render((OMSymbol) om, prec);
        } else if (c.equals(OMReference.class)) {
                render((OMReference) om, prec);
        } else if (c.equals(OMString.class)) {
                render((OMString) om, prec);
        } else if (c.equals(OMBind.class)) {
                render((OMBind) om, prec);
        } else if (c.equals(OMApply.class)) {
                render((OMApply) om, prec);
        } else if (c.equals(OMError.class)) {
                render((OMError) om, prec);
        } else if (c.equals(OMBinary.class)) {
                render((OMBinary) om, prec);
        } else if (c.equals(OMForeign.class)) {
                render((OMForeign) om, prec);
        } else if (OMContainer.class.isInstance(om)) {
                render(((OMContainer) om).toOpenMath(), prec);
        } else {
				throw new IOException("Cannot render class " + c.toString());
		}
    }

    /*
    ***** OpenMathBase
     */
    protected void render(OpenMathBase om) throws IOException {
		render(om, 0);
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
        out.write("OMInteger(");
        out.write(omi.getStrValue());
        out.write(")");
    }

    /*
    ***** OMVariable
     */
    protected void render(OMVariable omv, int level) throws IOException {
        out.write("OMVariable");
        out.write(omv.getName());
        out.write(")");
    }

    /*
    ***** OMFloat
     */
    protected void render(OMFloat omf, int level) throws IOException {
        out.write("OMFloat(");
        out.write(omf.getDec().toString());
        out.write(")");
    }

    /*
    ***** OMSymbol
     */
    protected void render(OMSymbol oms, int level) throws IOException {
        out.write("OMSymbol(");
        out.write(oms.getCd().trim());
        out.write(", ");
        out.write(oms.getName().trim());
        out.write(")");
    }

    /*
    ***** OMReference
     */
    protected void render(OMReference omr, int level) throws IOException {
        out.write("OMReference(");
        out.write(omr.getHref());
        out.write(")");
    }

    /*
    ***** OMString
     */
    protected void render(OMString omstr, int level) throws IOException {
        out.write("OMString(");
        out.write(omstr.getValue());
        out.write(")");
    }

    /*
    ***** OMBinary
     */
    protected void render(OMBinary omstr, int level) throws IOException {
        out.write("OMBinary(");
        out.write(omstr.getBase64Value());
        out.write(")");
    }

    /*
    ***** OMForeign
     */
    protected void render(OMForeign omf, int level) throws IOException {
        out.write("OMForeign(");
        out.write(omf.getContent());
        out.write(")");
    }

    /*
    ***** OMBind
     */
    protected void render(OMBind ombind, int level) throws IOException {
        OMSymbol oms = ombind.getSymbol();
        OMVariable[] bvars = ombind.getBvars();
        OpenMathBase param = ombind.getParam();
        out.write("OMBind(");
        render(oms, level);
        out.write(", OMBvars(");
        for (int i = 0; i < bvars.length; i++ ) {
            render(bvars[i], 0);
            if (i != bvars.length - 1)
                out.write(", ");
        }
        out.write("), ");
        render(param, 0);
        out.write(")");
    }

    /*
    ***** OMApply
     */
    protected void render(OMApply oma, int level) throws IOException {
        OpenMathBase head = oma.getHead();
        OpenMathBase[] params = oma.getParams();
        out.write("OMApply(");
        render(head, 0);
        for (int i = 0; i < params.length; i++) {
            render(params[i], 0);
            if (i != params.length - 1)
                out.write(", ");
        }
        out.write(")");
    }

    /*
    ***** OMError
     */
    protected void render(OMError ome, int level) throws IOException {
        OpenMathBase head = ome.getHead();
        OpenMathBase[] params = ome.getParams();
        out.write("OMError(");
        render(head, 0);
        for (int i = 0; i < params.length; i++) {
            render(params[i], 0);
            if (i != params.length - 1)
                out.write(", ");
        }
        out.write(")");
    }

    /*
    ***** binary
     */
    protected void renderBinary(OpenMathBase[] params, String operator, int iprec, int prec) throws IOException {
       if (params.length != 2)
           throw new RuntimeException("binary function/relation expected");
       renderNAry(params, operator, iprec, prec, true);
    }

    /*
    ***** binary
     */
    protected void renderNonAssocBinary(OpenMathBase[] params, String operator, int iprec, int prec) throws IOException {
       if (params.length != 2)
           throw new RuntimeException("binary function/relation expected");
       renderNAry(params, operator, iprec, prec, false);
    }

    /*
    ***** n-ary
     */
    protected void renderNAry(OpenMathBase[] params, String operator, int iprec, int prec) throws IOException {
		renderNAry(params, operator, iprec, prec, true);
    }

    /*
    ***** n-ary
     */
    protected void renderNAry(OpenMathBase[] params, String operator, int iprec, int prec, boolean assoc) throws IOException {
        //if (params.length == 0)
        //    throw new RuntimeException("Don't know how to render n-ary function/relation with zero arguments");
        if (assoc && (iprec < prec)) out.write("(");
        if (!assoc && (iprec <= prec)) out.write("(");
        for (int i = 0; i < params.length; i++) {
            OpenMathBase param = params[i];
            if (i > 0)
                out.write(operator);
            render(param, iprec);
        }
        if (assoc && (iprec < prec)) out.write(")");
        if (!assoc && (iprec <= prec)) out.write(")");
    }

    protected boolean renderApply(OMSymbol oms, OpenMathBase[] para, int prec) {
        String cd = oms.getCd().trim();
        String name = oms.getName().trim();
        if (Tools.keywords.contains(name))
            name = name + "_";
        Method m = applicationMethods.get(pack).get(cd+"."+name);
        if(null == m)
            return false;
        SymbolRenderer sr = getSymbolRenderer(cd);
        if (null == sr)
            return false;
        try {
            m.invoke(sr, para, prec);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected Integer getPrec(OMSymbol oms) {
        String cd = oms.getCd().trim();
        String name = oms.getName().trim();
        if (Tools.keywords.contains(name))
            name = name + "_";
        Method m = applicationMethods.get(pack).get(cd+"."+name);
        if(null == m)
            return null;
        SymbolRenderer sr = getSymbolRenderer(cd);
        if (null == sr)
            return null;
        try {
            return (Integer) m.invoke(sr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected boolean renderBind(OpenMathBase om, OMVariable[] bvars, OpenMathBase param, int prec) {
        OMSymbol oms;
        if (om.getClass() != OMSymbol.class)
            return false;
        oms = (OMSymbol) om;
        String cd = oms.getCd().trim();
        String name = oms.getName().trim();
        //if (keywords.contains(name))
        //    name = name + "_";
        Method m = bindMethods.get(pack).get(cd+"."+oms.getName());
        if(null == m)
            return false;
        SymbolRenderer sr = getSymbolRenderer(cd);
        if (null == sr)
            return false;
        try {
            m.invoke(sr, bvars, param, prec);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected boolean renderSymbol(OMSymbol oms) {
            String cd = oms.getCd().trim();
            String name = oms.getName().trim();
            if (Tools.keywords.contains(name))
                name = name + "_";
            String fqname = cd+"."+name;
            Method m = symbolMethods.get(pack).get(fqname);
            if(null == m)
                return false;
            SymbolRenderer sr = getSymbolRenderer(cd);
            if (null == sr)
                return false;
            try {
                m.invoke(sr);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

}
