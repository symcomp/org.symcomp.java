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

package org.symcomp.openmath.binary;

import org.symcomp.openmath.*;

import java.io.Reader;
import java.io.IOException;
import java.io.CharArrayReader;
import java.math.BigInteger;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class BinaryParser implements BinaryConstants {

    private HashMap<Integer, OpenMathBase> refs = new HashMap<Integer, OpenMathBase>();

    private Reader reader;

    public BinaryParser(Reader reader) {
        this.reader = reader;
    }

    public static OpenMathBase parse(char[] in) throws OpenMathException, IOException {
        return (new BinaryParser(new CharArrayReader(in)).process());
    }

    public static OpenMathBase parse(Reader reader) throws OpenMathException, IOException {
        return (new BinaryParser(reader).process());
    }

    public OpenMathBase process() throws OpenMathException, IOException {
        return slurpNext();
    }

    private OpenMathBase slurpNext() throws IOException, OpenMathException {
        String id = null;
        int idlen = -1;
        int type = (char) (0xff & reader.read());
        // System.out.println("type: " + (int) type);
        OpenMathBase result = null;
        boolean has_id = 0 != (type & FLAG_ID);
        boolean is_long = 0 != (type & FLAG_LONG);
        if ((type & FLAG_STATUS) != 0)
            throw new OpenMathException("Can't handle streamed objects at ");
        switch(type & TYPE_MASK) {
        case TYPE_OBJECT:
            result = slurpNext();
            if (reader.read() != TYPE_OBJECT_END)
                throw new OpenMathException("Binary Format Error: Expecting object-end-byte");
            return new OMObject(result);
        case TYPE_INT_SMALL:
            if (has_id) {
                idlen = slurpNumber(is_long);
            }
            int r = slurpNumber(is_long);
            if (r > 127 & !is_long) {
                r = -((-r) & 0xff);
            }
            result = new OMInteger(r);
            break;
        case TYPE_INT_BIG:
            int len = slurpNumber(is_long);
            if (has_id) {
                idlen = slurpNumber(is_long);
            }
            int base = reader.read();
            int sign = (MASK_SIGN_POS == (base & MASK_SIGN_POS))?MASK_SIGN_POS:MASK_SIGN_NEG;
            base |= sign;
            BigInteger bi = null;
            switch(base & (MASK_BASE_16 | MASK_BASE_256)) {
            case MASK_BASE_256:
                char[] rbs = slurpChars(len);
                int n = rbs.length;
                byte[] bts = new byte[n];
                for (int i = 0; i<n; i++) bts[i] = (byte) (rbs[n-i-1]);
                bi = new BigInteger(bts);
                break;
            case MASK_BASE_10:
                String t = slurpString(len);
                bi = new BigInteger(t);
                break;
            case MASK_BASE_16:
                t = slurpString(len);
                bi = new BigInteger("0x"+t);
                break;
            }
            //if (bi.signum() == -1) bi = bi.negate();
            if (sign == MASK_SIGN_NEG) bi = bi.negate();
            result = new OMInteger(bi);
            break;
        case TYPE_FLOAT:
            if (has_id) {
                idlen = slurpNumber(is_long);
                id = slurpString(idlen);
            }
            result = new OMFloat(slurpChars(8));
            break;
        case TYPE_VARIABLE:
            int namelength = slurpNumber(is_long);
            if (has_id) { idlen = slurpNumber(is_long); }
            result = new OMVariable(slurpString(namelength));
            break;
        case TYPE_SYMBOL:
            int cdnamelength = slurpNumber(is_long);
            namelength = slurpNumber(is_long);
            if (has_id) { idlen = slurpNumber(is_long); }
            result = new OMSymbol(slurpString(cdnamelength), slurpString(namelength));
            break;
        case TYPE_STRING_ISO:
        case TYPE_STRING_UTF: // TODO: correctly deal with UTF strings
            int stringlength = slurpNumber(is_long);
            if((type & TYPE_MASK) == TYPE_STRING_UTF) { stringlength *= 2; }
            if (has_id) { idlen = slurpNumber(is_long); }
            result = new OMString(slurpString(stringlength));
            break;
        case TYPE_BYTES:
			int bytelength = slurpNumber(is_long);
			if (has_id) { idlen = slurpNumber(is_long); }
			result = new OMBinary(slurpBytes(bytelength));
			break;
        case TYPE_FOREIGN:
			int enc_bytelength = slurpNumber(is_long);
			int cont_bytelength = slurpNumber(is_long);
			if (has_id) { idlen = slurpNumber(is_long); }
			String enc = new String(slurpChars(enc_bytelength));
			String cont = new String(slurpChars(cont_bytelength));
			result = new OMForeign(cont, enc);
			break;
        case TYPE_APPLICATION:
            if(has_id) {
                idlen = slurpNumber(is_long);
                id = slurpString(idlen);
            }
            OpenMathBase head = slurpNext();
            List<OpenMathBase> params = new LinkedList<OpenMathBase>();
            while (true) {
                OpenMathBase om = slurpNext();
                if (null==om) break;
                params.add(om);
            }
            result = head.apply(params.toArray(new OpenMathBase[params.size()]));
            break;
        case TYPE_ATTRIBUTION:
            if (TYPE_ATTRPAIRS != (reader.read() & TYPE_MASK))
                throw new OpenMathException("Binary Format Error: Expecting attribution-begin-byte");
            Map<String ,OpenMathBase[]> attributions = new HashMap<String, OpenMathBase[]>();
            while (true) {
                OpenMathBase k = slurpNext();
                if (null==k) break;
                attributions.put(k.toXml(), new OpenMathBase[] { k, slurpNext()} );
            }
            result = slurpNext();
            result.setAttributions(attributions);
            if (TYPE_ATTRIBUTION_END != (reader.read() & TYPE_MASK))
                throw new OpenMathException("Binary Format Error: Expecting attribution-end-byte");
            break;
        case TYPE_ERROR:
            if(has_id) {
                idlen = slurpNumber(is_long);
                id = slurpString(idlen);
            }
            head = slurpNext();
            params = new LinkedList<OpenMathBase>();
            while (true) {
                OpenMathBase om = slurpNext();
                if (null==om) break;
                params.add(om);
            }
            result = new OMError(head, params.toArray(new OpenMathBase[params.size()]));
            break;
        case TYPE_BINDING:
            if(has_id) {
                idlen = slurpNumber(is_long);
                id = slurpString(idlen);
            }
            OMSymbol oms = (OMSymbol) slurpNext();
            if (TYPE_BVARS != (reader.read() & TYPE_MASK))
                throw new OpenMathException("Binary Format Error: Expecting bvar-begin-byte");
            List<OMVariable> bvars = new LinkedList<OMVariable>();
            while (true) {
                OMVariable omv = (OMVariable) slurpNext();
                if (null==omv) break;
                bvars.add(omv);
            }
            OpenMathBase param = slurpNext();
            result = new OMBind(oms, bvars.toArray(new OMVariable[bvars.size()]), param);
			if (reader.read() != TYPE_BINDING_END)
                throw new OpenMathException("Binary Format Error: Expecting binding-end-byte");
            break;
        case TYPE_REFERENCE_INT:
            int idref = slurpNumber(is_long);
            result = refs.get(idref);
            break;
        case TYPE_REFERENCE_EXT:
            int reflength = slurpNumber(is_long);
            result = new OMReference(slurpString(reflength));
            break;
        // these cases are dealt with inline,
        case TYPE_BVARS:
            throw new OpenMathException("Binary Format Error: Orphaned BVARS.");
        case TYPE_ATTRPAIRS:
            throw new OpenMathException("Binary Format Error: Orphaned ATTP.");
        // all end-tokens simply return null
        case TYPE_APPLICATION_END:
        case TYPE_BINDING_END:
        case TYPE_ATTRIBUTION_END:
        case TYPE_ERROR_END:
        case TYPE_ATTRPAIRS_END:
        case TYPE_BVARS_END:
            return null;
        // these need to be implemented when their time has come
        case TYPE_CDBASE:
            throw new OpenMathException("Implement this!");
        }
        if (has_id && id == null) {
            id = slurpString(idlen);
        }
        if (null != id) {
            if(id.startsWith("##")) {
                int n = Integer.parseInt(id.substring(2));
                refs.put(n, result);
            } else
                result.setId(id);
        }
        return result;
    }

    private int slurpNumber(boolean is_long) throws IOException {
        if (is_long)
            return (reader.read()<<24) | (reader.read()<<16) | (reader.read()<<8) | reader.read();
        else
            return reader.read();
    }

    private char[] slurpChars(int n) throws IOException {
        char[] buf = new char[n];
        reader.read(buf);
        return buf;
    }

    private String slurpString(int n) throws IOException {
        char[] buf = new char[n];
        reader.read(buf);
        return String.valueOf(buf);
    }

    @SuppressWarnings({"SameParameterValue"})
    private byte[] slurpBytes(int len) throws IOException {
        char[] buf = new char[len];
		byte[] res = new byte[len];
        reader.read(buf);

		for (int i = 0; i < len; ++i) res[i] = (byte) buf[i];
        return res;
    }

}
