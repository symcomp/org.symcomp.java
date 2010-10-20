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

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.io.CharArrayWriter;
import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;

public class BinaryRenderer implements BinaryConstants {

    private Writer writer = null;
    private HashMap<String, Integer> locrefs = new HashMap<String, Integer>();
    private boolean compress = false;

    private BinaryRenderer(Writer writer) throws Exception {
        this.writer = writer;
    }

    public static char[] render(OpenMathBase om) throws Exception {
        CharArrayWriter out = new CharArrayWriter();
        render(out, om);
        return out.toCharArray();
    }

    public static char[] render(OpenMathBase om, boolean compress) throws Exception {
        CharArrayWriter out = new CharArrayWriter();
        render(out, om, compress);
        return out.toCharArray();
    }

    public static void render(Writer writer, OpenMathBase om) throws Exception {
        BinaryRenderer r = new BinaryRenderer(writer);
		//Binary should always be encapsulated in an OMOBJ
        r.srender(om.toOMObject());
    }

    public static void render(Writer writer, OpenMathBase om, boolean compress) throws Exception {
        BinaryRenderer r = new BinaryRenderer(writer);
		//Binary should always be encapsulated in an OMOBJ
        r.compress = compress;
        r.srender(om.toOMObject());
    }

    private void srender(OpenMathBase om) throws Exception {
        if (om.isAttributed()) {
        /* Attributions

            are encoded using the attribution tags (token identifiers 18 and 19). More precisely,
            attribution of the object E with (S1, E1), É (Sn, En) pairs (where Si are the attributes) is
            encoded as the attributed object tag (token identifier 18), followed by the encoding of the
            attribute pairs as the attribute pairs tags (token identifier 20), followed by the encoding of
            each symbol and value, followed by the end attribute pairs tag (token identifier 21), followed
            by the encoding of E, followed by the end attributed object tag (token identifier 19).

            Grammar:

            attribution	?	 [18] attrpairs object [19]
                |	 [18+64] [m] id:m  attrpairs object [19]
                |	 [18+64+128] {m} id:m  attrpairs object [19]
            attrpairs	?	 [20] pairs [21]
                |	 [20+64] [m] id:m  pairs [21]
                |	 [20+64+128] {m} id:m  pairs [21]
            pairs	?	 symbol object
                |	 symbol object pairs
        */
            Map<String, OpenMathBase[]> a = om.getAttributions();
            assert a.size() > 0;
            writeChar(TYPE_ATTRIBUTION);
            writeChar(TYPE_ATTRPAIRS);
            for (OpenMathBase[] kv : a.values()) {
                srender(kv[0]);
                srender(kv[1]);
            }
            writeChar(TYPE_ATTRPAIRS_END);
        }
        Class c = om.getClass();
        if (c.equals(OMObject.class)) {
            writeChar(TYPE_OBJECT);
            srender (((OMObject) om).getElement());
            writeChar(TYPE_OBJECT_END);
        } else if (c.equals(OMInteger.class)) {
                srender ((OMInteger) om);
        } else if (c.equals(OMVariable.class)) {
                srender ((OMVariable) om);
        } else if (c.equals(OMFloat.class)) {
                srender ((OMFloat) om);
        } else if (c.equals(OMSymbol.class)) {
                srender ((OMSymbol) om);
        } else if (c.equals(OMReference.class)) {
                srender ((OMReference) om);
        } else if (c.equals(OMString.class)) {
                srender ((OMString) om);
        } else if (c.equals(OMBind.class)) {
                srender ((OMBind) om);
        } else if (c.equals(OMApply.class)) {
                srender ((OMApply) om);
        } else if (c.equals(OMError.class)) {
                srender ((OMError) om);
	    } else if (c.equals(OMBinary.class)) {
	            srender ((OMBinary) om);
	    } else if (c.equals(OMForeign.class)) {
	            srender ((OMForeign) om);
	    } else if (OMContainer.class.isInstance(om)) {
                srender(((OMContainer) om).toOpenMath());
        } else {
                throw new Exception("Unknown class, Programming error");
        }

        if (om.isAttributed()) {
            writeChar(TYPE_ATTRIBUTION_END);
        }
    }

    private void writeChar(int val) throws IOException {
        writer.write((char) (val & 0xff));
    }

    private void writeChars(char[] bts) throws IOException {
        for(int i = 0; i < bts.length; i++) {
            writer.append(bts[i]);
        }
    }

    private void writeBytes(byte[] bts) throws IOException {
        for(int i = 0; i < bts.length; i++) {
            writer.write((int) bts[i]);
        }
    }


    private void writeReveresed(byte[] bts) throws IOException {
        for(int i = bts.length-1; i >= 0; i--) {
            writer.append((char) bts[i]);
        }
    }

    private void writeNumber(int val) throws IOException {
        writer.append((char) ((val>>24) & 0xff));
        writer.append((char) ((val>>16) & 0xff));
        writer.append((char) ((val>> 8) & 0xff));
        writer.append((char) ((val    ) & 0xff));
    }

    private void writeString(String s) throws IOException {
        writer.append(s);
    }

    /*  INTEGERS

        are encoded depending on how large they are. There are four possible formats. Integers between
        -128 and 127 are encoded as the small integer tags (token identifier 1) followed by a single
        byte that is the value of the integer (interpreted as a signed character). For example 16 is
        encoded as 0x01 0x10. Integers between -2 31 (-2147483648) and 2 31 - 1 (2147483647) are
        encoded as the small integer tag with the long flag set followed by the integer encoded in
        little endian format in four bytes (network byte order: the most significant byte comes
        first). For example, 128 is encoded as 0x81 0x00000080. The most general encoding begins with
        the big integer tag (token identifier 2) with the long flag set if the number of bytes in the
        encoding of the digits is greater or equal than 256. It is followed by the length (in bytes)
        of the sequence of digits, encoded on one byte (0 to 255, if the long flag was not set) or
        four bytes (network byte order, if the long flag was set). It is then followed by a byte
        describing the sign and the base. This 'sign/base' byte is + (0x2B) or - (0x2D) for the sign
        or-ed with the base mask bits that can be 0 for base 10 or 0x40 for base 16 or 0x80 for "base
        256". It is followed by the sequence of digits (as characters for bases 10 and 16 as in the
        XML encoing, and as bytes for base 256) in their natural order. For example, the decimal
        number 8589934592 (233) is encoded as 0x02 0x0A 0x2B 0x38 0x35 0x38 0x39 0x39 0x33 0x34 0x35
        0x39 0x32 and the hexadecimal number xfffffff1 is encoded as 0x02 0x08 0x6b 0x66 0x66 0x66
        0x66 0x66 0x66 0x66 0x31 in the base 16 character encoding and as 0x02 0x04 0xFF 0xFF 0xFF
        0xFI in the byte encoding (base 256).

        Note that it is permitted to encode a "small" integer in any "bigger" format.

        To splice sequences of integer packets into integers, we have to consider three cases: In the
        case of token identifiers 1, 33, and 65 the sequence of packets is treated as a sequence of
        integer digits to the base of 27 (most significant first). The case of token identifiers 129,
        161, and 193 is analogous with digits of base 231. In the case of token identifiers 2, 34, 66,
        130, 162, and 194 the integer is assembled by concatenating the string of decimal digits in
        the packets in sequence order (which corresponds to most significant first). Note that in all
        cases only the sequence-initial packet may contain a signed integer. The sign of this packet
        determines the sign of the overall integer.

        Grammar:

        integer	?	 [1] [_]
                |	 [1+64] [n] id:n  [_]
                |	 [1+32] [_]
                |	 [1+128] {_}
                |	 [1+64+128] {n} id:n  {_}
                |	 [1+32+128] {_}
                |	 [2] [n] [_] digits:n
                |	 [2+64] [n] [m] [_] digits:n  id:m
                |	 [2+32] [n] [_] digits:n
                |	 [2+128] {n} [_] digits:n
                |	 [2+64+128] {n} {n} [_] digits:n  id:n
                |	 [2+32+128] {n} [_] digits:n
     */
    @SuppressWarnings({"ConstantConditions"})
    private void srender(OMInteger omi) throws IOException {
        BigInteger ii = omi.getIntValue();
        String id = omi.getId();
        int typ = 0;
        if (null != id) {
            if (id.length() >= 127) { typ |= FLAG_LONG; }
            typ |= FLAG_ID;
        }
        if (ii.bitLength() > 0 && ii.bitLength() < 8 && ii.abs().intValue()<0x80 && 0 == (typ & FLAG_LONG)) {
            //-- first case: just one byte
            typ |= TYPE_INT_SMALL;
            writeChar(typ);
            if ((typ & FLAG_ID) != 0) { writeChar((char) id.length()); }
            byte o;
            if (ii.compareTo(BigInteger.ZERO) < 0) {
                o = ii.negate().byteValue();
                o = (byte) ((o ^ 0xff)+1);
            } else {
                o = ii.byteValue();
            }
            writeChar((char) o);
        } else if (ii.bitLength() > 0 && ii.bitLength() < 32 && Math.abs(ii.longValue()) < 2147483646) {
            //-- second case: just one int
            typ |= TYPE_INT_SMALL | FLAG_LONG;
            writeChar(typ);
            if ((typ & FLAG_ID) != 0) { writeNumber((char) id.length()); }
            writeNumber(ii.intValue());
        } else {
            //-- third case: big int > 32 bit
            typ |= TYPE_INT_BIG;
            char base = MASK_BASE_256;
            if (ii.compareTo(BigInteger.ZERO) < 0) {
                base |= MASK_SIGN_NEG;
                ii = ii.negate();
            } else {
                base |= MASK_SIGN_POS;
            }
            if (null != id) {
                if (id.length() >= 127) { typ |= FLAG_LONG; }
                typ |= FLAG_ID;
            }
            byte[] bts = ii.toByteArray();
            if (bts.length >= 127) { typ |= FLAG_LONG; }
            writeChar(typ);
            if ((typ & FLAG_LONG) == 0) {
                writeChar((char) bts.length);
                if ((typ & FLAG_ID) != 0) { writeChar((char) id.length()); }
            } else {
                writeNumber(bts.length);
                if ((typ & FLAG_ID) != 0) { writeNumber((char) id.length()); }
            }
            writeChar(base);
            writeReveresed(bts);
        }
        if ((typ & FLAG_ID) != 0) { writeString(id); }
    }

    /* SYMBOLS

        are encoded as the symbol tags (token identifier 8) with the long flag set if the maximum of
        the length in bytes in the UTF-8 encoding of the Content Dictionary name or the symbol name is
        greater than or equal to 256 . The symbol tag is followed by the length in bytes in the UTF-8
        encoding of the Content Dictionary name, the symbol name, and the id (if the shared bit was
        set) as a byte (if the long flag was not set) or a four byte integer (in network byte order).
        These are followed by the bytes of the UTF-8 encoding of the Content Dictionary name, the
        symbol name, and the id.

        Grammar:

        symbol	?	 [8] [n] [m] cdname:n  symbname:m
               |	 [8+64] [n] [m] [k] cdname:n  symbname:m  id:k
               |	 [8+128] {n} {m} cdname:n  symbname:m
               |	 [8+64+128] {n} {m} {k} cdname:n  symbname:m  id:k
     */
    private void srender(OMSymbol oms) throws IOException {
        String id = oms.getId();
        String cd = oms.getCd();
        String name = oms.getName();
        String qname = cd+"."+name;
        if(compress && locrefs.get(qname) == null && id == null) {
            // internal_reference	? [30] [_] | [30+128] {_}
            int n = locrefs.size();
            id = String.format("##%d", n);
            locrefs.put(qname, n);
        }
        if(compress && locrefs.get(qname) != null && id == null) {
            // internal_reference	? [30] [_] | [30+128] {_}
            int n = locrefs.get(qname);
            char typ = TYPE_REFERENCE_INT;
            if (Math.abs(n)>125) typ |= FLAG_LONG;
            writeChar(typ);
            if (Math.abs(n)>125)
                writeNumber(n);
            else
                writeChar(n);
            return;
        }
        char typ = TYPE_SYMBOL;
        if ((id != null && id.length()>125) || cd.length()>125 || name.length()>125)
            typ |= FLAG_LONG;
        if (id != null)
            typ |= FLAG_ID;
        writeChar(typ);
        if (0 == (typ & FLAG_LONG)) {
            writeChar(cd.length());
            writeChar(name.length());
            if (id != null)
                writeChar(id.length());
        } else {
            writeNumber(cd.length());
            writeNumber(name.length());
            if (id != null)
                writeNumber(id.length());
        }
        writeString(cd);
        writeString(name);
        if (id != null)
            writeString(id);
    }

    /* VARIABLES

        are encoded using the variable tags (token identifiers 5) with the long flag set if the number
        of bytes in the UTF-8 encoding of the variable name is greater than or equal to 256. Then,
        there is the number of characters as a byte (if the long flag was not set) or a four byte
        integer (in network byte order), followed by the characters of the name of the variable. For
        example, the variable x is encoded as 0x05 0x01 0x78.

        Grammar:

        variable	?	 [5] [n] varname:n
                    |	 [5+64] [n] [m] varname:n  id:m
                    |	 [5+128] {n} varname:n
                    |	 [5+64+128] {n} {m} varname:n  id:m
    */
    private void srender(OMVariable omv) throws Exception {
        String id = omv.getId();
        String name = omv.getName();
        char typ = TYPE_VARIABLE;
        if ((id != null && id.length()>125) || name.length()>125)
            typ |= FLAG_LONG;
        if (id != null)
            typ |= FLAG_ID;
        writeChar(typ);
        if (0 == (typ & FLAG_LONG)) {
            writeChar(name.length());
            if (id != null)
                writeChar(id.length());
        } else {
            writeNumber(name.length());
            if (id != null)
                writeNumber(id.length());
        }
        writeString(name);
        if (id != null)
            writeString(id);
    }

    /* Floating-point number

        are encoded using the floating-point number tags (token identifier 3) followed by eight bytes that are
        the IEEE 754 representation [6], most significant bytes first. For example, 0.1 is encoded
        as 0x03 0x000000000000f03f.

        Grammar:

        float	?	 [3] {_}{_}
                |	 [3+64] [n] id:n  {_}{_}
                |	 [3+64+128] {n} id:n  {_}{_}
    */
    private void srender(OMFloat omf) throws Exception {
        String id = omf.getId();
        char[] bts = omf.getBytes();
        char typ = TYPE_FLOAT;
        if (id != null && id.length()>125)
            typ |= FLAG_LONG;
        if (id != null)
            typ |= FLAG_ID;
        writeChar(typ);
        if (id != null) {
            if (0 == (typ & FLAG_LONG)) { writeChar(id.length()); }
            else { writeNumber(id.length()); }
            writeString(id);
        }
        writeChars(bts);
    }

    /* STRINGS

        are encoded in two ways depending on whether , the string is encoded in UTF-16 or ISO-8859-1
        (LATIN-1). In the case of LATIN-1 it is encoded as the one byte character string tags (token
        identifier 6) with the long flag set if the number of bytes (characters) in the string is
        greater than or equal to 256. Then, there is the number of characters as a byte (if the length
        flag was not set) or a four byte integer (in network byte order), followed by the characters
        in the string. If the string is encoded in UTF-16, it is encoded as the UTF-16 character
        string tags (token identifier 7) with the long flag set if the number of characters in the
        string is greater or equal to 256. Then, there is the number of UTF-16 units, which will be
        the number of characters unless characters in the higher planes of Unicode are used, as a byte
        (if the long flag was not set) or a four byte integer (in network byte order), followed by the
        characters (UTF-16 encoded Unicode).

        Sequences of string packets are assumed to have the same encoding for every packet. They are
        assembled into strings by concatenating the strings in the packets in sequence order.

        Grammar:

        string	?	 [6] [n] bytes:n
                |	 [6+64] [n] bytes:n
                |	 [6+32] [n] bytes:n
                |	 [6+128] {n} bytes:n
                |	 [6+64+128] {n} {m} bytes:n  id:m
                |	 [6+32+128] {n} bytes:n
                |	 [7] [n] bytes:2n
                |	 [7+64] [n] [m] bytes:n  id:m
                |	 [7+32] [n] bytes:2n
                |	 [7+128] {n} bytes:2n
                |	 [7+64+128] {n} {m} bytes:2n  id:m
                |	 [7+32+128] {n} bytes:2n
     */
    private void srender(OMString omstr) throws IOException {
        String id = omstr.getId();
        String str = omstr.getValue();
        char typ = TYPE_STRING_ISO;
        if ((id != null && id.length()>125) || str.length() > 125)
            typ |= FLAG_LONG;
        if (id != null)
            typ |= FLAG_ID;
        writeChar(typ);
        if (0 == (typ & FLAG_LONG)) {
            writeChar(str.length());
            if (id != null)
                writeChar(id.length());
        } else {
            writeNumber(str.length());
            if (id != null)
                writeNumber(id.length());
        }
        writeString(str);
        if (id != null)
            writeString(id);
    }

    /* BYTEARRAYS

        are encoded using the bytearray tags (token identifier 4) with the long flag set if
        the number elements is greater than or equal to 256. Then, there is the number of elements, as
        a byte (if the long flag was not set) or a four byte integer (in network byte order), followed
        by the elements of the arrays in their normal order.

        Sequences of bytearray packets are assembled into byte arrays by concatenating the bytearrays
        in the packets in sequence order.

        Grammar:

        bytearray	?	 [4] [n] bytes:n
                    |	 [4+64] [n] [m] bytes:n  id:m
                    |	 [4+32] [n] bytes:n
                    |	 [4+128] {n} bytes:n
                    |	 [4+64+128] {n} {m} bytes:n  id:m
                    |	 [4+32+128] {n} bytes:n
    */
    private void srender(OMBinary ombin) throws Exception {
        String id = ombin.getId();
        byte[] bts = ombin.getByteValue();

		//Write the type
        char typ = TYPE_BYTES;
        if ((id != null && id.length()>125) || bts.length > 255)
            typ |= FLAG_LONG;
        if (id != null)
            typ |= FLAG_ID;
        writeChar(typ);

		//Write the lengths
        if (0 == (typ & FLAG_LONG)) {
            writeChar(bts.length);
            if (id != null)
                writeChar(id.length());
        } else {
            writeNumber(bts.length);
            if (id != null)
                writeNumber(id.length());
        }

		//Write the data
        writeBytes(bts);
        if (id != null)
            writeString(id);
    }

    /* FOREIGN OBJECTS

        are encoded using the foreign object tags (token identifier 12) with the long
        flag set if the number of bytes is greater than or equal to 256 and the streaming bit set for
        dividing it up into packets. Then, there is the number n of bytes used to encode the encoding,
        and the number m of bytes used to encode the foreign object. n and m are represented as a byte
        (if the long flag was not set) or a four byte integer (in network byte order). These numbers
        are followed by an n-byte representation of the encoding attribute and an m byte sequence of
        bytes encoding the foreign object in their normal order (we call these the payload bytes). The
        encoding attribute is encoded in UTF-8.

        Sequences of foreign object packets are assembled into foreign objects by concatenating the
        payload bytes in the packets in sequence order.

        Note that the foreign object is encoded as a stream of bytes, not a stream of characters.
        Character based formats (including XML based formats) should be encoded in UTF-8 to produce a
        stream of bytes to use as the payload of the foreign object.

        Grammar:

        foreign	?	 [12] [n] [m] bytes:n  bytes:m
                |	 [12+64] [n] [m] [k] bytes:n  bytes:m  id:k
                |	 [12+32] [n] [m] bytes:n  bytes:m
                |	 [12+128] {n} {m} bytes:n  bytes:m
                |	 [12+64+128] {n} {m} {k} bytes:n  bytes:m  id:k
                |	 [12+32+128] {n} {m} bytes:n  bytes:m
    */
    private void srender(OMForeign omforeign) throws Exception {
        String id = omforeign.getId();
		String encoding = omforeign.getEncoding();
		if (encoding == null) encoding = "";
		byte[] bts_enc = encoding.getBytes("UTF-8");
		byte[] bts_cont = omforeign.getContent().getBytes("UTF-8");

		//Write the type
        char typ = TYPE_FOREIGN;
        if ((id != null && id.length()>125) || bts_enc.length > 255 || bts_cont.length > 255)
            typ |= FLAG_LONG;
        if (id != null)
            typ |= FLAG_ID;
        writeChar(typ);

		//Write the lengths
        if (0 == (typ & FLAG_LONG)) {
            writeChar(bts_enc.length);
            writeChar(bts_cont.length);
            if (id != null)
                writeChar(id.length());
        } else {
            writeNumber(bts_enc.length);
            writeNumber(bts_cont.length);
            if (id != null)
                writeNumber(id.length());
        }

		//Write the data
		writeBytes(bts_enc);
        writeBytes(bts_cont);
        if (id != null)
            writeString(id);
    }


    /* CDBASE SCOPES

        are encoded using the token identifier 9. The purpose of these scoping devices
        is to associate a cdbase with an object. The start token [9] (or [137] if the long flag is
        set) is followed by a single-byte (or 4-byte- if the long flag is set) number n and then by a
        seqence of n bytes that represent the value of the cdbase attribute (a URI) in UTF-8 encoding.
        This is then followed by the binary encoding of a single object: the object over which this
        cdbase attribute has scope.

        Grammar:

        cdbase	?	 [9] [n] uri:n 	 object
                |	 [9+128] {n} uri:n 	 object

        MUST BE IMPLEMENTED AS A SIDE-EFFECT!
    */


    /* Applications

        are encoded using the application tags (token identifiers 16 and 17). More precisely, the
        application of E0 to E1É En is encoded using the application tags (token identifier 16), the
        sequence of the encodings of E0 to En and the end application tags (token identifier 17).

        Grammar:

        application	?	 [16] object objects [17]
                    |	 [16+64] [m] id:m  object objects [17]
                    |	 [16+64+128] {m} id:m  object objects [17]
    */
    private void srender(OMApply oma) throws Exception {
        OpenMathBase head = oma.getHead();
        OpenMathBase[] params = oma.getParams();
        String id = oma.getId();
        int typ = TYPE_APPLICATION;
        if (null != id) {
            typ |= FLAG_ID;
            if(id.length() > 125)
                typ |= FLAG_LONG;
        }
        writeChar(typ);
        if (null != id) {
            if(id.length() > 125)
                writeNumber(id.length());
            else
                writeChar(id.length());
            writeString(id);
        }
        srender(head);
        for(OpenMathBase p : params) {
            srender(p);
        }
        writeChar(TYPE_APPLICATION_END);
    }

    /* Errors

        are encoded using the error tags (token identifiers 22 and 23). More precisely, S0 applied to
        E1É En is encoded as the error tag (token identifier 22), the encoding of S0, the sequence of
        the encodings of E0 to En and the end error tag (token identifier 23).

        Grammar:

        error	?	 [22] symbol objects [23]
            |	 [22+64] [m] id:m  symbol objects [23]
            |	 [22+64+128] {m} id:m  symbol objects [23]
    */
    private void srender(OMError oma) throws Exception {
        OpenMathBase head = oma.getHead();
        OpenMathBase[] params = oma.getParams();
        String id = oma.getId();
        int typ = TYPE_ERROR;
        if (null != id) {
            typ |= FLAG_ID;
            if(id.length() > 125)
                typ |= FLAG_LONG;
        }
        writeChar(typ);
        if (null != id) {
            if(id.length() > 125)
                writeNumber(id.length());
            else
                writeChar(id.length());
            writeString(id);
        }
        srender(head);
        for(OpenMathBase p : params) {
            srender(p);
        }
        writeChar(TYPE_ERROR_END);
    }

    /* Bindings

        are encoded using the binding tags (token identifiers 26 and 27). More precisely, the binding
        by B of variables V1É Vn in C is encoded as the binding tag (token identifier 26), followed by
        the encoding of B, followed by the binding variables tags (token identifier 28), followed by
        the encodings of the variables V1 É Vn, followed by the end binding variables tags (token
        identifier 29), followed by the encoding of C, followed by the end binding tags (token
        identifier 27).

        Grammar:

        binding	?	 [26] object bvars object [27]
            |	 [26+64] [m] id:m  object bvars object [27]
            |	 [26+64+128] {m} id:m  object bvars object [27]
        bvars	?	 [28] vars [29]
            |	 [28+64] [m] id:m  vars [29]
            |	 [28+64+128] {m} id:m  vars [29]
    */
    private void srender(OMBind ombind) throws Exception {
        OpenMathBase symbol = ombind.getSymbol();
        OpenMathBase[] bvars = ombind.getBvars();
        OpenMathBase param = ombind.getParam();
        String id = ombind.getId();
        int typ = TYPE_BINDING;
        if (null != id) {
            typ |= FLAG_ID;
            if(id.length() > 125)
                typ |= FLAG_LONG;
        }
        writeChar(typ);
        if (null != id) {
            if(id.length() > 125)
                writeNumber(id.length());
            else
                writeChar(id.length());
            writeString(id);
        }
        srender(symbol);
        writeChar(TYPE_BVARS);
        for(OpenMathBase bv : bvars) {
            srender(bv);
        }
        writeChar(TYPE_BVARS_END);
        srender(param);
        writeChar(TYPE_BINDING_END);
    }

    /* Internal References

    are encoded using the internal reference tags [30] and [30+128] (the sharing flag cannot be
    set on this tag, since chains of references are not allowed in the OpenMath binary encoding)
    with long flag set if the number of OpenMath sub-objects in the encoded OpenMath is greater
    than or equal to 256. Then, there is the ordinal number of the referenced OpenMath object as a
    byte (if the long flag was not set) or a four byte integer (in network byte order).

    internal_reference	?	 [30] [_]
        |	 [30+128] {_}

        We-re creating internal references on the fly.

    */

    /* External References

        are encoded using the external reference tags [31] and [31+128] (the sharing flag cannot be
        set on this tag, since chains of references are not allowed in the OpenMath binary encoding)
        with the long flag set if the number of bytes in the reference URI is greater than or equal to
        256. Then, there is the number of bytes in the URI used for the external reference as a byte
        (if the long flag was not set) or a four byte integer (in network byte order), followed by the
        URI.

        Grammar:

        external_reference	?	 [31] [n] uri:n
        |	 [31+128] {n} uri:n

    */
    private void srender(OMReference omr) throws Exception {
        String ref = omr.getHref();
        char typ = TYPE_REFERENCE_EXT;
        if (ref.length()>125)
            typ |= FLAG_LONG;
        writeChar(typ);
        if (0 == (typ & FLAG_LONG)) {
            writeChar(ref.length());
        } else {
            writeNumber(ref.length());
        }
        writeString(ref);
    }
}


