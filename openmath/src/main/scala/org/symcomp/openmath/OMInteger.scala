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

import java.math.BigInteger
import java.security.MessageDigest;
import java.util.Map;
import java.io.PrintStream;

/**
 * Representing the OpenMath integer node <tt>&lt;OMI&gt;</tt>
 */
case class OMInteger(bigIntValue:BigInteger) extends OpenMathBase {
    var strValue:String = null;
    def this(value:Int) = this(new BigInteger(value.toString()));
    def this(value:Long) = this(new BigInteger(value.toString()));
    def this(value:String) = {
        this(OMInteger.parseEncodings(value));
        strValue = value;
    }
    
    /**
	 * Get value of OMInteger as BigInt
	 */
    def getIntValue():BigInteger = bigIntValue


	/**
	 * Get value of OMInteger as String. The return value depends on how the
	 *   OMInteger was constructed: If it was constructed by a String, that is
	 *   returned; if it was constructed by a BigInt, a decimal representation
	 *   of that integer is returned.
	 */
    def getStrValue():String = {
		if (strValue == null) {
			assert (bigIntValue != null);
			strValue = bigIntValue.toString();
		}
        strValue;
    }

	/**
	 * Get value of OMInteger as decimal String.
	 */
    def getStrValueDec():String = getIntValue().toString();

	/**
	 * Get value of OMInteger as hex String (returned value always starts with "x")
	 */
    def getStrValueHex():String = OMInteger.toBase16(getIntValue());

    override def subTreeHash():Tuple2[java.lang.Integer, String] = {
        (1, OpenMathBase.b64md5String(bigIntValue.toString() + ":Integer"))
    }

}

object OMInteger {
    val f16 = new BigInteger("16");
    val hexchars:Array[String] = Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
         "a", "b", "c", "d", "e", "f")
    val hexToInt = scala.collection.immutable.Map(
        '0' -> new BigInteger("0"),  '1' -> new BigInteger("1"),
        '2' -> new BigInteger("2"),  '3' -> new BigInteger("3"),
        '4' -> new BigInteger("4"),  '5' -> new BigInteger("5"),
        '6' -> new BigInteger("6"),  '7' -> new BigInteger("7"),
        '8' -> new BigInteger("8"),  '9' -> new BigInteger("9"),
        'a' -> new BigInteger("10"), 'b' -> new BigInteger("11"),
        'c' -> new BigInteger("12"), 'd' -> new BigInteger("13"),
        'e' -> new BigInteger("14"), 'f' -> new BigInteger("15"),
        'A' -> new BigInteger("10"), 'B' -> new BigInteger("11"),
        'C' -> new BigInteger("12"), 'D' -> new BigInteger("13"),
        'E' -> new BigInteger("14"), 'F' -> new BigInteger("15")
     )

    /**
	 * Get value of OMInteger as BigInt
	 */
    def parseEncodings(str:String):BigInteger = {
        assert (str != null)
        if (str.startsWith("x") || str.startsWith("-x")) {
            OMInteger.fromBase16(str);
        } else {
            return new BigInteger(str);
        }
    }


    def toBase16(i:BigInteger):String = {
        val qr = i.divideAndRemainder(f16)
        val q = qr(0)
        val r:Int = qr(1).intValue()
        if(q.equals(BigInteger.ZERO))
            "0x0"
        else
            toBase16(q) + hexchars.apply(r)
    }

    def fromBase16(str:String):BigInteger = {
        val bi = BigInteger.ZERO;
        if(!str.startsWith("0x"))
            throw new RuntimeException("Couldn't parse Hex-Integer, not starting with 0x.")
        for(i:Int <- (str.length-1) to 2)
            bi.add(hexToInt(str.charAt(i)).shiftLeft(i*16))
        bi
    }

}