package org.symcomp.openmath.xml;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.*;
import org.symcomp.openmath.*;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class OMSaxParser extends DefaultHandler {

    private OpenMathBase result;
    private StringWriter currentCharacterStream;
    private Stack stack = new Stack();
    private StringWriter foreignWriter;
    private TransformerHandler tr;
    private int inForeign = 0;

    enum TagType { OMA, OMB, OMBIND, OME, OMF, OMFOREIGN, OMI, OMOBJ, OMR, OMSTR, OMS, OMV, OMATTR, OMATP, OMBVAR }

    private static HashMap<String, TagType> nodename2Type = new HashMap<String, TagType>();
    static {
        nodename2Type.put("OMA",       TagType.OMA);
        nodename2Type.put("OMB",       TagType.OMB);
        nodename2Type.put("OMBIND",    TagType.OMBIND);
        nodename2Type.put("OME",       TagType.OME);
        nodename2Type.put("OMF",       TagType.OMF);
        nodename2Type.put("OMFOREIGN", TagType.OMFOREIGN);
        nodename2Type.put("OMI",       TagType.OMI);
        nodename2Type.put("OMOBJ",     TagType.OMOBJ);
        nodename2Type.put("OMR",       TagType.OMR);
        nodename2Type.put("OMS",       TagType.OMS);
        nodename2Type.put("OMSTR",     TagType.OMSTR);
        nodename2Type.put("OMV",       TagType.OMV);
        nodename2Type.put("OMATTR",    TagType.OMATTR);
        nodename2Type.put("OMATP",     TagType.OMATP);
        nodename2Type.put("OMBVAR",    TagType.OMBVAR);
    }


    public static OpenMathBase parse(InputSource input) throws Exception {
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setFeature("http://xml.org/sax/features/namespaces", false);
        xr.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        OMSaxParser handler = new OMSaxParser ();
        xr.setContentHandler(handler);
        xr.setErrorHandler(handler);
        xr.parse(input);
        return handler.result;
    }

    private TagType getTagType(String uri, String localName, String qName) {
        if(qName.contains(":"))
            qName = qName.substring(qName.indexOf(":")+1);
        return nodename2Type.get(qName);
    }

    @Override
    public void startDocument() throws SAXException {
        result = null;
        stack = new Stack();
    }

    @Override
    public void endDocument() throws SAXException {
        if (stack.size() != 1)
            throw new SAXException("Empty document");
        this.result = (OpenMathBase) stack.pop();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        TagType typ = getTagType(uri, localName, qName);
        if (inForeign > 0) {
            //System.out.println("startElenent: "+uri+" "+localName+" "+qName);
            tr.startElement(uri, localName, qName, attributes);
            if (typ != null && typ.equals(TagType.OMFOREIGN)) inForeign++;
            return;
        }
        if (typ.equals(TagType.OMFOREIGN) && inForeign==0) {
            initOMForeign();
        }
        HashMap<String, String> at = null;
        if (attributes.getLength() > 0) {
            at = new HashMap<String, String>();
            for (int i=0; i < attributes.getLength(); i++) {
                at.put(attributes.getQName(i), attributes.getValue(i));
            }
        }
        stack.push(new Object[] {typ, at} );
        if (typ == TagType.OMI || typ == TagType.OMSTR || typ == TagType.OMB || typ == TagType.OMV) {
            currentCharacterStream = new StringWriter();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        OpenMathBase result;
        HashMap<String, String> attributes;
        //System.out.println(String.format("uri='%s', localName='%s', qName=%s'", uri, localName, qName));
        TagType typ = getTagType(uri, localName, qName);
        if (typ != null && typ.equals(TagType.OMFOREIGN)) inForeign--;
        if (inForeign > 0) {
            //System.out.println("endElenent: "+uri+" "+localName+" "+qName);
            tr.endElement(uri, localName, qName);
            return;
        }
        if (null==typ)
            throw new SAXException(String.format("Don't know how to handle uri='%s', localName='%s', qName='%s'", uri, localName, qName));

        // variables used later
        LinkedList<OpenMathBase> params;
        OpenMathBase o;
        OpenMathBase head = null;
        Object[] oo = null;
        TagType etyp = null;

        switch (typ) {
        case OMA:       // --------------------------------------------------------
            params = new LinkedList<OpenMathBase>();
            while (stack.peek().getClass() != Object[].class) {
                o = (OpenMathBase) stack.pop();
                if (stack.peek().getClass() != Object[].class)
                    params.addFirst(o);
                else
                    head = o;
            }
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
			if (head == null) throw new SAXException("Found OMA without head symbol.");
            result = head.apply(params.toArray(new OpenMathBase[params.size()]));
            attributes = (HashMap<String, String>) oo[1];
            break;

        case OMATP:     // --------------------------------------------------------
            params = new LinkedList<OpenMathBase>();
            while (stack.peek().getClass() != Object[].class) {
                o = (OpenMathBase) stack.pop();
                params.addFirst(o);
            }
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            attributes = (HashMap<String, String>) oo[1];
            stack.push(params);
            return;

        case OMATTR:    // --------------------------------------------------------
            o = (OpenMathBase) stack.pop();
            params = (LinkedList) stack.pop();
            for (int i = 0; i<params.size()-1; i+=2) {
                o.putAt(params.get(i), params.get(i+1));
            }
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            attributes = (HashMap<String, String>) oo[1];
            result = o;
            break;

        case OMB:       // --------------------------------------------------------
	        result = new OMBinary(currentCharacterStream.getBuffer().toString());
	        oo = (Object[]) stack.pop();
	        etyp = (TagType) oo[0];
	        if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
	        attributes = (HashMap<String, String>) oo[1];
	        break;
	
        case OMBIND:    // --------------------------------------------------------
            OpenMathBase o2 = (OpenMathBase) stack.pop();
            params = (LinkedList<OpenMathBase>) stack.pop();
            o = (OpenMathBase) stack.pop();
            result = new OMBind((OMSymbol) o, params.toArray(new OMVariable[params.size()]), o2);
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
			if (o == null) throw new SAXException("Found OMBIND without head symbol.");
            attributes = (HashMap<String, String>) oo[1];
            break;

        case OMBVAR:    // --------------------------------------------------------
            params = new LinkedList<OpenMathBase>();
            while (stack.peek().getClass() != Object[].class) {
                o = (OpenMathBase) stack.pop();
                params.addFirst(o);
            }
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            attributes = (HashMap<String, String>) oo[1];
            stack.push(params);
            return;

        case OME:       // --------------------------------------------------------
            params = new LinkedList<OpenMathBase>();
            head = null;
            while (stack.peek().getClass() != Object[].class) {
                o = (OpenMathBase) stack.pop();
                if (stack.peek().getClass() != Object[].class)
                    params.addFirst(o);
                else
                    head = o;
            }
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
			if (head == null) throw new SAXException("Found OME without head symbol.");
            result = new OMError(head, params.toArray(new OpenMathBase[params.size()]));
            attributes = (HashMap<String, String>) oo[1];
            break;

        case OMF:       // --------------------------------------------------------
            oo = (Object[]) stack.pop();
            attributes = (HashMap<String, String>) oo[1];
            OMFloat omf;
            if (attributes != null && attributes.get("dec") != null) {
                result = new OMFloat(Double.parseDouble(attributes.get("dec")));
            } else if (attributes != null && attributes.get("hex") != null) {
                result = new OMFloat(attributes.get("hex"));
			} else {
				throw new SAXException("Found OMF without hex, without dec");
			}
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            break;

        case OMFOREIGN: // --------------------------------------------------------
            oo = (Object[]) stack.pop();
            if (inForeign > 0) return;
            tr.endDocument();
            String content = (foreignWriter.getBuffer().toString().substring(43));
            attributes = (HashMap<String, String>) oo[1];
            result = null;
            if (null != attributes) {
                String enc = attributes.get("encoding");
                if (enc != null)
                    result = new OMForeign(content, enc);
            } else {
                result = new OMForeign(content);
            }
            break;
        case OMI:       // --------------------------------------------------------
            result = new OMInteger(currentCharacterStream.getBuffer().toString().trim());
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            attributes = (HashMap<String, String>) oo[1];
            break;

        case OMOBJ:     // --------------------------------------------------------
            result = new OMObject((OpenMathBase) stack.pop());
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            attributes = (HashMap<String, String>) oo[1];
            break;

        case OMR:       // --------------------------------------------------------
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            attributes = (HashMap<String, String>) oo[1];
			if (attributes == null || attributes.get("href") == null) throw new SAXException("Found OMR without href.");
            result = new OMReference(attributes.get("href"));
            break;

        case OMS:       // --------------------------------------------------------
            oo = (Object[]) stack.pop();
            attributes = (HashMap<String, String>) oo[1];
			if (attributes == null || attributes.get("cd") == null || attributes.get("name") == null) throw new SAXException("Found OMS without cd or without name");
            result = new OMSymbol(attributes.get("cd"), attributes.get("name"));
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            break;

        case OMSTR:     // --------------------------------------------------------
            result = new OMString(currentCharacterStream.getBuffer().toString());
            oo = (Object[]) stack.pop();
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            attributes = (HashMap<String, String>) oo[1];
            break;

        case OMV:       // --------------------------------------------------------
            oo = (Object[]) stack.pop();
            attributes = (HashMap<String, String>) oo[1];
            if (attributes == null || attributes.get("name") == null) {
                String s = currentCharacterStream.getBuffer().toString();
                if (attributes == null)
                    attributes = new HashMap<String, String>();
                if (s != null && s.length()>0)
                    attributes.put("name", s);
                else {
                    throw new SAXException("Found OMV without variable name");
                }
            }
            result = new OMVariable(attributes.get("name"));
            etyp = (TagType) oo[0];
            if (etyp != typ) throw new SAXException(String.format("STRANGE THINGS. Implementation bug! found %s, expected %s", etyp, typ));
            break;

        default:        // --------------------------------------------------------
            throw new SAXException(String.format("Don't know how to handle uri='%s', localName='%s', qName='%s'", uri, localName, qName));
        }
        if (null != attributes) {
            result.setId(attributes.get("id"));
            result.setCdbase(attributes.get("cdbase"));
        }
        stack.push(result);
    }

    private void initOMForeign() {
        try {
            //System.out.println("inForeign starts");
            inForeign += 1;
            foreignWriter = new StringWriter();
            StreamResult sresult = new StreamResult(foreignWriter);
            SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
            tr = tf.newTransformerHandler();
            Transformer serializer = tr.getTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
            serializer.setOutputProperty(OutputKeys.INDENT,"no");
            tr.setResult(sresult);
            tr.startDocument();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inForeign > 0) {
            tr.characters(ch, start, length);
            return;
        }

        if (null != currentCharacterStream) {
            currentCharacterStream.write(ch, start, length);
        }
    }

    @Override
    public void processingInstruction(String s, String s1) throws SAXException {
        if (inForeign > 0) {
            tr.processingInstruction(s, s1);
            return;
        }
    }
    
    @Override
    public void warning(SAXParseException e) throws SAXException {
        // System.out.print("Warning: ");
        // e.printStackTrace();
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        // System.out.print("Error: ");
        // e.printStackTrace();
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        // System.out.print("Fatal Error: ");
        // e.printStackTrace();
    }


}
