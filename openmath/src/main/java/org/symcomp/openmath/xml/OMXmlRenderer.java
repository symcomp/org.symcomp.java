package org.symcomp.openmath.xml;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.*;
import org.symcomp.openmath.*;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import java.io.Writer;
import java.io.StringReader;

public class OMXmlRenderer {

    TransformerHandler tr;
    StreamResult result;

    public OMXmlRenderer(Writer out) throws Exception {
        result = new StreamResult(out);
        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        tr = tf.newTransformerHandler();

        Transformer serializer = tr.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
	serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.setOutputProperty(OutputKeys.INDENT,"no");

        tr.setResult(result);
        tr.startDocument();
    }

    public void render(OpenMathBase om) throws Exception {
        tr.startDocument();
        srender(om);
        tr.endDocument();
    }

    public void srender(OpenMathBase om) throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        Class c = om.getClass();
        if (om.getAttributions().size() > 0) {
            tr.startElement("", "", "OMATTR", attrs);
            tr.startElement("", "", "OMATP", attrs);
            for (String k : om.getAttributions().keySet()) {
                OpenMathBase[] at = om.getAttributions().get(k);
                srender(at[0]);
                srender(at[1]);
            }
            tr.endElement("", "", "OMATP");
        }
        String id = om.getId();
        if (null != id) { attrs.addAttribute("", "", "id", "CDATA", id); }
        String cdbase = om.getCdbase();
        if (null != cdbase) { attrs.addAttribute("", "", "cdbase", "CDATA", cdbase); }
        OpenMathBase[] params;
        if (c == OMApply.class) {
            OMApply oma = (OMApply) om;
            tr.startElement("", "", "OMA", attrs);
            if (oma.getHead() == null) throw new OpenMathException("Whoa! Trying to XML render OMA without head.");
            srender(oma.getHead());
            params = oma.getParams();
            for (OpenMathBase param : params) { srender(param); }
            tr.endElement("", "", "OMA");
        } else if (c == OMBind.class) {
            OMBind omb = (OMBind) om;
            tr.startElement("", "", "OMBIND", attrs);
            if (omb.getSymbol() == null) throw new OpenMathException("Whoa! Trying to XML render OMBind without head symbol.");
            srender(omb.getSymbol());
            attrs.clear();
            tr.startElement("", "", "OMBVAR", attrs);
            if (omb.getBvars() == null) throw new OpenMathException("Whoa! Trying to XML render OMBind without variables.");
            params = omb.getBvars();
            for (OpenMathBase param : params) { srender(param); }
            tr.endElement("", "", "OMBVAR");
            srender(omb.getParam());
            tr.endElement("", "", "OMBIND");
        } else if (c == OMError.class) {
            OMError ome = (OMError) om;
            tr.startElement("", "", "OME", attrs);
            if (ome.getHead() == null) throw new OpenMathException("Whoa! Trying to XML render OMError without head symbol.");
            srender(ome.getHead());
            params = ome.getParams();
            for (OpenMathBase param : params) { srender(param); }
            tr.endElement("", "", "OME");
        } else if (c == OMFloat.class) {
            OMFloat omf = (OMFloat) om;
            if (omf.getDec() == null && omf.getHex() == null) throw new OpenMathException("Whoa! Trying to XML render OMFloat without dec, without hex.");
			if (omf.getDec() != null) attrs.addAttribute("", "", "dec", "CDATA", omf.getDec().toString());
			if (omf.getHex() != null) attrs.addAttribute("", "", "hex", "CDATA", omf.getHex());
            tr.startElement("", "", "OMF", attrs);
            tr.endElement("", "", "OMF");
        } else if (c == OMForeign.class) {
            //Lets do it the ugly way!
			//The ugly way fails with Java 1.6 :(, so we do it the inefficient way!
            OMForeign omfo = (OMForeign) om;
            if (omfo.getEncoding() != null) attrs.addAttribute("", "", "encoding", "CDATA", omfo.getEncoding());
            tr.startElement("", "", "OMFOREIGN", attrs);

			//Do the content of the omfo
			String cont = omfo.getContent();
			{
		        XMLReader xr = XMLReaderFactory.createXMLReader();
		        OMSaxParser handler = new OMSaxParser ();
		        xr.setContentHandler(tr);
		        xr.parse(new InputSource(new StringReader(cont)));
			}

			//The rest
            tr.endElement("", "", "OMFOREIGN");
        } else if (c == OMInteger.class) {
            OMInteger omi = (OMInteger) om;
            char[] dta = omi.getStrValue().toCharArray();
            tr.startElement("", "", "OMI", attrs);
            tr.characters(dta, 0, dta.length);
            tr.endElement("", "", "OMI");    
        } else if (c == OMObject.class) {
            OMObject omo = (OMObject) om;
            if (omo.getElement() == null) throw new OpenMathException("Whoa! Trying to XML render OMObject without element.");
            tr.startElement("", "", "OMOBJ", attrs);
            srender(omo.getElement());
            tr.endElement("", "", "OMOBJ");    
        } else if (c == OMReference.class) {
            OMReference omr = (OMReference) om;
            if (omr.getHref() == null) throw new OpenMathException("Whoa! Trying to XML render OMReference without href.");
            attrs.addAttribute("", "", "href", "CDATA", omr.getHref());
            tr.startElement("", "", "OMR", attrs);
            tr.endElement("", "", "OMR");
        } else if (c == OMString.class) {
            OMString omst = (OMString) om;
            char[] dta = omst.getValue().toCharArray();
            tr.startElement("", "", "OMSTR", attrs);
            tr.characters(dta, 0, dta.length);
            tr.endElement("", "", "OMSTR");    
        } else if (c == OMBinary.class) {
            OMBinary ombn = (OMBinary) om;
            char[] dta = ombn.getBase64Value().toCharArray();
            tr.startElement("", "", "OMB", attrs);
            tr.characters(dta, 0, dta.length);
            tr.endElement("", "", "OMB");
        } else if (c == OMSymbol.class) {
            OMSymbol oms = (OMSymbol) om;
            if (oms.getCd() == null) throw new OpenMathException("Whoa! Trying to XML render OMSymbol without cd.");
            if (oms.getName() == null) throw new OpenMathException("Whoa! Trying to XML render OMSymbol without name.");
            attrs.addAttribute("", "", "cd", "CDATA", oms.getCd());
            attrs.addAttribute("", "", "name", "CDATA", oms.getName());
            tr.startElement("", "", "OMS", attrs);
            tr.endElement("", "", "OMS");
        } else if (c == OMVariable.class) {
            OMVariable omv = (OMVariable) om;
            if (omv.getName() == null) throw new OpenMathException("Whoa! Trying to XML render OMVariable without name.");
            attrs.addAttribute("", "", "name", "CDATA", omv.getName());
            tr.startElement("", "", "OMV", attrs);
            tr.endElement("", "", "OMV");
        } else if (OMContainer.class.isInstance(om)) {
            srender(((OMContainer)om).toOpenMath());
        } else {
            throw new Exception("Unknown class, Programming error");
        }
        if (om.getAttributions().size() > 0) {
            tr.endElement("", "", "OMATTR");
        }
    }

}
