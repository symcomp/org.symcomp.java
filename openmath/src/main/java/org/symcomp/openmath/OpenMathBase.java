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

import java.io.*;
import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.HashMap;
import java.math.BigInteger;
import java.nio.CharBuffer;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.symcomp.openmath.xml.OMSaxParser;
import org.symcomp.openmath.xml.OMXmlRenderer;
import org.symcomp.openmath.popcorn.PopcornRenderer;
import org.symcomp.openmath.popcorn.PopcornASTLexer;
import org.symcomp.openmath.popcorn.PopcornASTParser;
import org.symcomp.openmath.popcorn.PopcornWalker;
import org.symcomp.openmath.popcorn.ReaderInputStream;
import org.symcomp.openmath.latex.LatexRenderer;
import org.symcomp.openmath.binary.BinaryConstants;
import org.symcomp.openmath.binary.BinaryParser;
import org.symcomp.openmath.binary.BinaryRenderer;
import org.xml.sax.InputSource;

/**
 * Base class of all OpenMath Objects
 *
 * OpenMathBase and its subclasses are used to store OpenMath expressions. They offer
 * a 1:1-correspondence to the XML-Tree.
 *
 * A tree of OpenMath-Objects can either be constructed programmatically by calling the
 * constructors and stacking the resulting objects together, or by parsing an xml-string.
 *
 * Note that there are no classes for representing attributions and bound variables, the
 * functionality is integrated into all classes and OMBind respectively.
 */
public abstract class OpenMathBase {
    /** the id of the node, if set */
    protected String id = null;
    /** the cdbase of the node, if set*/
    protected String cdbase = null;
    /** an assoziative array that contains the OMATTR attributes of the node */
    protected Map<String ,OpenMathBase[]> attributions = new HashMap<String, OpenMathBase[]>();

    // Getters and Setters

    /**
     * Get the value of the xml-attribute id
     *
     * @return value of the xml-attribute id
     */
    public String getId() { return this.id; }

    /**
     * Set the value for the xml-attribute id
     *
     * @param id The new id.
     */
    public void setId(String id) { this.id = id; }

    /**
     * Get the value of the xml-attribute cdbase
     *
     * @return value of the xml-attribute cdbase
     */
    public String getCdbase() { return this.cdbase; }

    /**
     * Set the value for the xml-attribute cdbase
     *
     * @param cdbase The new cdbase.
     */
    public void setCdbase(String cdbase) { this.cdbase = cdbase; }

    /**
     * Get the attributions. Note that the Format of the map is:
     * Key - String representation of the OMATTR-key,
     * Value - an OpenMathBase[], where [0] is the OMATTR key and [1] is the OMATTR value.
     *
     * @return the attributions
     */
    public Map<String,OpenMathBase[]> getAttributions() { return this.attributions; }

    /**
     * Set the attributions. Note that the Format of the map is:
     * Key - String representation of the OMATTR-key,
     * Value - an OpenMathBase[], where [0] is the OMATTR key and [1] is the OMATTR value.
     *
     * @param attributions The new attributions.
     */
    public void setAttributions(Map<String,OpenMathBase[]> attributions) { this.attributions = attributions; }

     /**
      * Set an OMATTR attribute. In groovy, this can syntactically be done using the
      * []-operator
      *
      * @param key the OMATTR key
      * @param val the OMATTR value
      */
     public void putAt(OpenMathBase key, OpenMathBase val) {
         this.attributions.put(key.toXml(), new OpenMathBase[] { key, val });
     }

    /**
     * Get an OMATTR attribute. In groovy, this can syntactically be done using the
     * []-operator
     *
     * @param key the key for which to fetch a value
     * @return the value if available or null if none is set
     */
    public OpenMathBase getAt(OpenMathBase key) {
        OpenMathBase[] a = new OpenMathBase[0];
        a = this.attributions.get(key.toXml());
        if (a != null)
        return a[1];
        return null;
    }

    /**
     * Find out whether the Object is attributed
     * @return true if the object is attributed
     */
     public boolean isAttributed() {
         return !(this.attributions.size() == 0);
     }

     /**
      * Decide whether two nodes are identical. To return true, the nodes need to have the
      * same class, id, cdbase, attributes and childs (recursively).
      * This is -- of course -- only a syntactical check.
      */
    public abstract boolean equals(Object that);


    /**
     * Creates an application where this is head and params are the params
     *
     * @param params the parameters
     * @return the application of this
     */
    public OMApply apply(OpenMathBase... params) {
        return new OMApply(this, params);
    }

    public OMApply apply2any(Object[] params) {
        OpenMathBase[] pp = new OpenMathBase[params.length];
        for (int i = 0; i < params.length; i++) {
          pp[i] = (OpenMathBase) params[i];
        }
        return new OMApply(this, pp);
    }

    /**
     * Creates an error where this is head and params are the params
     *
     * @param params the parameters
     * @return the application of this
     */
    public OMError error(OpenMathBase[] params) {
        return new OMError(this, params);
    }

    /**
     * Creates a binding where this is head and params are the params
     *
     * @param bvars the variables to bind
     * @param param the expression to bind
     * @return the binding of this
     */
    public OMBind bind(OMVariable[] bvars, OpenMathBase param) {
        return new OMBind((OMSymbol) this, bvars, param);
    }

    /**
     * Check whether this is an OMA
     * @return true if this is an OMA
     */
    public boolean isApplication() { return (this.getClass().equals(OMApply.class)); }

    /**
     * Check whether this is an application of oms
     * @param oms the possible head
     * @return true if this is an application of oms
     */
    public boolean isApplication(OMSymbol oms) { return this.isApplication(oms.getCd(), oms.getName()); }

    /**
     * Check whether this is an application of <OMS> with the given cd and name
     * @param cd the cd of the OMS
     * @param name the name of the OMS
     * @return true if this is an application of OMS with the given name and cd
     */
    public boolean isApplication(String cd, String name) {
        if (!this.isApplication()) return false;
        if (!((OMApply)this).getHead().isSymbol()) return false;
        OMSymbol oms = (OMSymbol)((OMApply)this).getHead();
        return (oms.getCd().equals(cd) && oms.getName().equals(name));
    }

    /**
     * Check whether this is an OMB
     * @return true if this is an OMB
     */
    public boolean isBinary() { return (this.getClass().equals(OMBinary.class)); }

    /**
     * Check whether this is an OMBIND
     * @return true if this is an OMBIND
     */
    public boolean isBinding() { return (this.getClass().equals(OMBind.class)); }

    /**
     * Check whether this is a binding of oms
     * @param oms the possible head
     * @return true if this is a binding of oms
     */
    public boolean isBinding(OMSymbol oms) { return this.isBinding(oms.getCd(), oms.getName()); }

    /**
     * Check whether this is a binding of <OMS> with the given cd and name
     * @param cd the cd of the OMS
     * @param name the name of the OMS
     * @return true if this is a binding of OMS with the given name and cd
     */
    public boolean isBinding(String cd, String name) {
        if (!this.isBinding()) return false;
        OMSymbol oms = ((OMBind)this).getSymbol();
        return (oms.getCd().equals(cd) && oms.getName().equals(name));
    }

    /**
     * Check whether this is an OME
     * @return true if this is an OME
     */
    public boolean isError() { return (this.getClass().equals(OMError.class)); }

    /**
     * Check whether this is an application of oms
     * @param oms the possible head
     * @return true if this is an application of oms
     */
    public boolean isError(OMSymbol oms) { return this.isError(oms.getCd(), oms.getName()); }

    /**
     * Check whether this is an application of <OMS> with the given cd and name
     * @param cd the cd of the OMS
     * @param name the name of the OMS
     * @return true if this is an application of OMS with the given name and cd
     */
    public boolean isError(String cd, String name) {
        if (!this.isError()) return false;
        if (!((OMError)this).getHead().isSymbol()) return false;
        OMSymbol oms = (OMSymbol)((OMError)this).getHead();
        return (oms.getCd().equals(cd) && oms.getName().equals(name));
    }


    /**
     * Check whether this is an OMF
     * @return true if this is an OMF
     */
    public boolean isFloat() { return (this.getClass().equals(OMFloat.class)); }

    /**
     * Check whether this is an OMF and has value f
     * @param f the value to compare
     * @return true if this is an OMF of given value
     */
    public boolean isFloat(double f) {
        return this.isFloat() && (((OMFloat) this).getDec() == f);
    }


    /**
     * Check whether this is an OMFOREIGN
     * @return true if this is an OMFOREIGN
     */
    public boolean isForeign() { return (this.getClass().equals(OMForeign.class)); }

    /**
     * Check whether this is an OMI
     * @return true if this is an OMI
     */
    public boolean isInteger() { return (this.getClass().equals(OMInteger.class)); }

    /**
     * Check whether this is an OMI with value i
     * @param i the value to compare
     * @return true if this is an OMI of given value
     */
    public boolean isInteger(BigInteger i) {
        return this.isInteger() && ((OMInteger) this).getIntValue().equals(i);
    }

    /**
     * Check whether this is an OMI with value i
     * @param i the value to compare
     * @return true if this is an OMI of given value
     */
    public boolean isInteger(Integer i) {
        return this.isInteger(BigInteger.valueOf(i));
    }

    /**
     * Check whether this is an OMOBJ
     * @return true if this is an OMOBJ
     */
    public boolean isObject() { return (this.getClass().equals(OMObject.class)); }

    /**
     * Wraps the OpenMathBase object in an OMOBject is it is not already an OMOBject
     * @return the wrapped Object
     */
    public OMObject toOMObject() {
        if (this.isObject()) return (OMObject) this;
        return new OMObject(this);
    }

    /**
     * Removes the OMObject wrapping if present, otherwise return the unmodified object.
     * @return the OMObject element or the OpenMathBase object itself if it wasn't an OMObject
     */
    public OpenMathBase deOMObject() {
        if (!this.isObject()) return this;
        return ((OMObject) this).getElement();
    }

    /**
     * Check whether this is an OMR
     * @return true if this is an OMR
     */
    public boolean isReference() { return (this.getClass().equals(OMReference.class)); }

    /**
     * Check whether this is an OMR whith given href
     * @param href the value to compare
     * @return true if this is an OMR with given href
     */
    public boolean isReference(String href) {
        return this.isReference() && (((OMReference) this).getHref().equals(href));
    }

    /**
     * Check whether this is an OMSTR
     * @return true if this is an OMSTR
     */
    public boolean isString() { return (this.getClass().equals(OMString.class)); }

    /**
     * Check whether this is an OMSTR with given value
     * @param s the String to compare to
     * @return true if this is an OMSTR with given value
     */
    public boolean isString(String s) {
        return this.isString() && (((OMString) this).getValue().equals(s));
    }

    /**
     * Check whether this is an OMS
     * @return true if this is an OMS
     */
    public boolean isSymbol() { return (this.getClass().equals(OMSymbol.class)); }

    /**
     * Check whether this is an OMS with given cd and name
     * @param cd the cd to check
     * @param name the name to check
     * @return true if this is an OMS of given cd and name
     */
    public boolean isSymbol(String cd, String name) {
        return this.isSymbol() && (((OMSymbol) this).getCd().equals(cd) &&
                ((OMSymbol) this).getName().equals(name));
    }

    /**
     * Check whether this is an OMV
     * @return true if this is an OMV
     */
    public boolean isVariable() { return (this.getClass().equals(OMVariable.class)); }

    /**
     * Check whether this is an OMV of the given name
     * @param name the name to check
     * @return true if this is an OMV of given name
     */
    public boolean isVariable(String name) {
        return this.isVariable() && ((OMVariable) this).getName().equals(name);
    }

    /**
     * Decide whether two nodes have the same OMATTR attributes, necessary for equality testing
     *
     * @param that the OpenMathBase Object to test against
     * @return true if the attributes are the same, false otherwise
     */
    protected boolean sameAttributes(OpenMathBase that) {
        if (this.getClass() != that.getClass()) { return false; }
        if ((this.id != null && that.id == null) || (that.id != null && this.id == null)) { return false;}
        if ((this.id != null && that.id != null) && !this.id.equals(that.id)) { return false; }
        if ((this.cdbase != null && that.cdbase == null) || (that.cdbase != null && this.cdbase == null)) { return false;}
        if ((this.cdbase != null && that.cdbase != null) && !this.cdbase.equals(that.cdbase)) { return false; }
        if (this.attributions.size() == 0 && that.attributions.size() == 0) { return true; }
        if (this.attributions.size() != that.attributions.size()) { return false; }
        Map<String, OpenMathBase[]> t = that.attributions;
        for (String k : this.attributions.keySet()) {
            if (null == t.get(k)) return false;
            if (!this.attributions.get(k)[1].equals(t.get(k)[1]))
                return false;
        }
        return true;
    }

    public abstract scala.Tuple2<Integer, String> subTreeHash();

    protected static String b64md5String(String in) { return b64md5String(in.getBytes()); }
    protected static String b64md5String(byte[] in) {
        String out;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            out = Base64.encodeBytes(md.digest(in));
            return out;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    //
    // ========== Visiting trees ==========
    //

    /**
     * Traverse the Object-tree with the visitor.
     * @param visitor
     * @return
     */
    public OpenMathBase traverse(OpenMathVisitor visitor) {
        return visitor.visit(this);
    }

    //
    // ========== Methods for parsing OpenMath Representatios ==========
    //

    //=================== Supported Encodings ===================

  /** constants **/
  public enum ENCODINGS {
    BINARY("binary", "parseBinary", "parseBinary", "toBinaryAsString", "toBinary"),
    XML("xml", "parseXml", "toXml"),
    POPCORN("popcorn", "parsePopcorn", "toPopcorn");

    private final String str;
    public String toString() { return str; }

    public static ENCODINGS get(String str) {
      for (OpenMathBase.ENCODINGS e : OpenMathBase.ENCODINGS.values()) {
        if (e.toString().equals(str)) return e;
      }
      return null;
    }

    private Method parseReader, parseString;
    public OpenMathBase parse(Reader r) throws OpenMathException {
      try {
        return (OpenMathBase) parseReader.invoke(null,r);
      } catch (IllegalAccessException iae) { throw new OpenMathException(iae.getMessage()); }
      catch (InvocationTargetException ite) { throw (OpenMathException) ite.getCause(); }
    }
    public OpenMathBase parse(String s) throws OpenMathException {
      try {
        return (OpenMathBase) parseString.invoke(null,s);
      } catch (IllegalAccessException iae) { throw new OpenMathException(iae.getMessage()); }
      catch (InvocationTargetException ite) { throw (OpenMathException) ite.getCause(); }
    }

    private Method renderString, renderWriter;
    public String render(OpenMathBase obj) throws OpenMathException {
      try {
        return (String) renderString.invoke(obj);
      } catch (IllegalAccessException iae) { throw new OpenMathException(iae.getMessage()); }
      catch (InvocationTargetException ite) { throw (OpenMathException) ite.getCause(); }
    }
    public void render(OpenMathBase obj, Writer out) throws OpenMathException {
      try {
        renderWriter.invoke(obj,out);
      } catch (IllegalAccessException iae) { throw new OpenMathException(iae.getMessage()); }
      catch (InvocationTargetException ite) { throw (OpenMathException) ite.getCause(); }
    }

      ENCODINGS(String s, String psr, String pss, String rss, String rsw) {
      this.str = s;
      try {
        this.parseReader = OpenMathBase.class.getMethod(psr, Reader.class);
        this.parseString = OpenMathBase.class.getMethod(pss, String.class);
        this.renderString = OpenMathBase.class.getMethod(rss);
        this.renderWriter = OpenMathBase.class.getMethod(rsw, Writer.class);
      } catch (NoSuchMethodException e) {
        System.out.println("WHOA! Problems constructing OpenMathBase.ENCODINGS!!");
        e.printStackTrace();
      }
    };
    ENCODINGS(String s, String ps, String rs) {
      this(s, ps, ps, rs, rs);
    }
  };


    //=================== Generic Parsing Switch ===================

    /**
     * Guesses the encoding of the OpenMath object in the given Reader by the first byte.
     * @param r a reader that delivers an OpenMath stream
     * @return an Object[] o, o[0] is one of the OpenMathBase.ENCODINGS,
   *         o[1] is the reader you should subsequently read from.
     * @throws OpenMathException if something went wrong
     */
   public static Object[] sniffEncoding(Reader r) throws OpenMathException {
    Reader in;
    char c = ' ';
    try {
         if (r.markSupported())
             in = r;
         else
             in = new BufferedReader(r);
         in.mark(1024);

         while (Character.isSpaceChar(c)) c = (char) in.read();
         in.reset();
    } catch (IOException e) {
      throw new OpenMathException("Something failed while sniffEncoding " + e.getMessage());
    }

       if ((byte) c == -1) {
         throw new OpenMathException("OpenMathBase.parse(Reader r) Could not (sufficiently) read from r");
       }

       switch(c) {
         case BinaryConstants.TYPE_OBJECT:   return new Object[]{ ENCODINGS.BINARY, in};
         case '<':              return new Object[]{ ENCODINGS.XML, in};
         default:              return new Object[]{ ENCODINGS.POPCORN, in};
       }
   } // sniffEncoding Reader

    /**
     * Parse the content from the given Reader to an OpenMathBase tree
     * The encoding is guessed by the first byte (using sniffEncoding)
     * @param r a reader that delivers an OpenMath stream
     * @return the representing OpenMathBase tree
     * @throws OpenMathException if something went wrong
     */
    public static OpenMathBase parse(Reader r) throws OpenMathException {
    Object[] sn = sniffEncoding(r);
    ENCODINGS enc = (ENCODINGS) sn[0];
    Reader in = (Reader) sn[1];

    return enc.parse(in);
  } // parse Reader

    /**
     * Parse the given OpenMath string to an OpenMathBase tree.
     * The encoding is guessed by the first byte.
     * @param text an OpenMath XML encoded string
     * @return the representing OpenMathBase tree
     * @throws OpenMathException if something went wrong
     */
    public static OpenMathBase parse(String text) throws OpenMathException {
        return parse(new StringReader(text));
    } // parse

    //=================== PARSE XML ===================

    /**
     * Parse the given OpenMath XML encoded string to an OpenMathBase tree
     * @param r a Reader that delivers an OpenMath XML encoded string
     * @return the representing OpenMathBase tree
     * @throws OpenMathException if something went wrong
     */
    public static OpenMathBase parseXml(Reader r) throws OpenMathException {
        try {
            return OMSaxParser.parse(new InputSource(r));
        } catch (Exception e) {
            //e.printStackTrace();
            throw new OpenMathException("Parsing XML went wrong: "+e.getMessage());
        }
    } // parseXml Reader

    /**
     * Parse the given OpenMath XML encoded string to an OpenMathBase tree
     * @param s an OpenMath XML encoded string
     * @return the representing OpenMathBase tree
     * @throws OpenMathException if something went wrong
     */
    public static OpenMathBase parseXml(String s) throws OpenMathException {
        try {
            return OMSaxParser.parse(new InputSource(s));
        } catch (Exception e) {
            throw new OpenMathException("Parsing XML went wrong: "+e.getMessage());
        }
    } // parseXml String

    //=================== PARSE BINARY ===================

    /**
     * Parse the given OpenMath binary encoded string to an OpenMathBase tree
     * @param r a Reader that delivers an OpenMath binary encoded string
     * @return the representing OpenMathBase tree
     * @throws OpenMathException if something went wrong
     */
    public static OpenMathBase parseBinary(Reader r) throws OpenMathException {
    try {
          return BinaryParser.parse(r);
        } catch (Exception e) {
            throw new OpenMathException("Parsing binary went wrong: "+e.getMessage());
    }
    } // parseBinary Reader

    /**
     * Parse the given OpenMath binary encoded string to an OpenMathBase tree
     * @param s an OpenMath binary encoded string
     * @return the representing OpenMathBase tree
     * @throws OpenMathException if something went wrong
     */
    public static OpenMathBase parseBinary(String s) throws OpenMathException {
    try {
          return BinaryParser.parse(new StringReader(s));
        } catch (Exception e) {
            throw new OpenMathException("Parsing binary went wrong: "+e.getMessage());
    }
    } // parseBinary String

    //=================== PARSE POPCORN ===================

    /**
     * Parse a string containing POPCORN code to an OpenMath tree.
     * @param r a Reader delivering a POPCORN string
     * @return the represented OpenMathBase tree
     * @throws OpenMathException if sth went wrong ;)
     */
    public synchronized static OpenMathBase parsePopcorn(Reader r) throws OpenMathException {
    try {
          ReaderInputStream ris = new ReaderInputStream(r);
          return parsePopcorn(ris);
    } catch (Exception e) {
      throw new OpenMathException("Problem in parsePopcorn(Reader r) : " + e.getMessage());
    }
    } // parsePopcorn Reader

    /**
     * Parse a string containing POPCORN code to an OpenMath tree.
     * @param popcode a POPCORN string
     * @return the represented OpenMathBase tree
     * @throws OpenMathException if sth went wrong ;)
     */
    public synchronized static OpenMathBase parsePopcorn(String popcode) throws OpenMathException {
        byte[] stringBytes = popcode.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(stringBytes);
        return parsePopcorn(bais);
    } // parsePopcorn String

    /**
     * Parse a string containing POPCORN code to an OpenMath tree.
     * @param istream a stream delivering a POPCORN string
     * @return the represented OpenMathBase tree
     * @throws OpenMathException if sth went wrong ;)
     */
    public synchronized static OpenMathBase parsePopcorn(InputStream istream) throws OpenMathException {
        OpenMathBase om;
        try {
          // Create an input character stream from standard in
          ANTLRInputStream input = new ANTLRInputStream(istream);
          // Create an ExprLexer that feeds from that stream
          PopcornASTLexer lexer = new PopcornASTLexer(input);
          // Create a stream of tokens fed by the lexer
          CommonTokenStream tokens = new CommonTokenStream(lexer);

          // Create a parser that feeds off the token stream
          PopcornASTParser parser = new PopcornASTParser(tokens);
          // Begin parsing at rule prog, get return value structure
      PopcornASTParser.start_return r = null;
        r = parser.start();

          // WALK RESULTING TREE
          CommonTree t = (CommonTree)r.getTree(); // get tree from parser
          // Create a tree node stream from resulting tree
          CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);

          PopcornWalker walker = new PopcornWalker(nodes); // create a tree parser
          om = walker.start(); // launch at start rule prog
        } catch(Exception e) {
            throw new OpenMathException("Parsing Popcorn Code went wrong:" + e.getMessage());
        }
        return om;
    } // parsePopcorn InputStream

    //
    // ========== Methods for rendering OpenMath into different formats ==========
    //

    //=================== RENDER XML ===================

    /**
     * Render whole tree to an XML-string w/o namespace w/o dereferencing
     * @return a XML string representation of the tree
     */
    public String toXml() {
        StringWriter s = new StringWriter();
        try {
            toXml(s);
        } catch (Exception e) {
      System.err.println("OpenMathBase.toXml() failed: " + e.getMessage());
      e.printStackTrace();
            return null;
        }
        return s.getBuffer().toString();
    }

    /**
     * Render whole tree to an XML-string w/o namespace w/o dereferencing
     * @param out the writer to which to write
     * @throws OpenMathException if something goes wrong
     */
    public void toXml(Writer out) throws OpenMathException {
    try {
          new OMXmlRenderer(out).render(this);
    } catch (Exception e) {
      throw new OpenMathException("toXml(Writer) failed: " + e.getMessage());
    }
    }

    //=================== RENDER BINARY ===================

    /**
     * Render whole tree to the binary representation
     * @return a char[]
     */
    public char[] toBinary() {
    char[] ret;
    try {
      ret = BinaryRenderer.render(this);
    } catch (Exception e) {
      System.err.println("OpenMathBase.toBinary() failed: " + e.getMessage());
      e.printStackTrace();
        return null;
    }
        return ret;
    }

    /**
     * Render whole tree to the binary representation.
     * @return a String
     */
    public String toBinaryAsString() {
    char[] r = toBinary();
    return (r == null ? null : new String(r));
    }

    /**
     * Render whole tree to the binary representation
     * @param out the writer to which to write
     * @throws OpenMathException if something goes wrong
     */
    public void toBinary(Writer out) throws OpenMathException {
    try {
          BinaryRenderer.render(out, this);
    } catch (Exception e) {
      throw new OpenMathException("toBinary(Writer) failed: " + e.getMessage());
    }
    }

    //=================== RENDER POPCORN ===================

    /**
     * Convert the given OpenMath tree to a string containing a POPCORN representation.
     * This method uses org.symcomp.openmath.popcorn.PopcornRenderer
     * @return a string containing the Popcorn representation
     */
    public String toPopcorn() {
    StringWriter sw = new StringWriter();
    try {
        toPopcorn(sw);
    } catch (Exception e) {
      System.err.println("OpenMathBase.toPopcorn() failed: " + e.getMessage());
      e.printStackTrace();
        return null;
    }
    return sw.getBuffer().toString();
  }

    /**
     * Convert the given OpenMath tree to a string containing a POPCORN representation.
     * This method uses org.symcomp.openmath.popcorn.PopcornRenderer
     * @param out a writer to deliver the POPCODE
     * @throws OpenMathException if something goes wrong
     */
    public void toPopcorn(Writer out) throws OpenMathException {
    try {
          new PopcornRenderer(out).render(this);
    } catch (Exception e) {
      throw new OpenMathException("toPopcorn(Writer) failed: " + e.getMessage());
    }
    }

    //=================== RENDER LATEX ===================

    /**
     * Convert the given OpenMath tree to a string containing a LaTeX representation.
     * This method uses org.symcomp.openmath.latex.LatexRenderer
     * @return a string containing the LaTeX representation
     */
    public String toLatex() {
    StringWriter sw = new StringWriter();
    try {
        toLatex(sw);
    } catch (Exception e) {
        return null;
    }
    return sw.getBuffer().toString();
    }

    /**
     * Convert the given OpenMath tree to a string containing a LaTeX representation.
     * This method uses org.symcomp.openmath.latex.LatexRenderer
     * @param out a Writer for the LaTeX representation
     * @throws Exception if something goes wrong
     */
    public void toLatex(Writer out) throws OpenMathException {
    try {
          new LatexRenderer(out).render(this);
    } catch (Exception e) {
      throw new OpenMathException("toBinary(Writer) failed: " + e.getMessage());
    }
    }

    /**
     * used to override operators for use from Scala
     * @param that
     * @return
     */
    public OpenMathBase $plus(OpenMathBase that) {
        return new OMApply(new OMSymbol("arith1", "plus"), new OpenMathBase[] { this, that });
    }

}