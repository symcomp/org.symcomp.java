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

/**
 * Representing the OpenMath float node <tt>&lt;OMF ... /&gt;</tt>
 */
case class OMFloat(dec:Double) extends OpenMathBase {

    def this(bts:Array[Char]) = this(OMFloat.bytes2double(bts))
    def this(hex:String) = this(OMFloat.hexStringToCharArray(hex))

    def getHex():String = OMFloat.charArrayToHexString(OMFloat.double2bytes(dec))
    def getDec():java.lang.Double = new java.lang.Double(dec)
    def getBytes():Array[Char] = OMFloat.double2bytes(dec)
    
 	//=== Methods ===
 	override def equals(that:Any):Boolean = {
        if (!that.isInstanceOf[OMFloat]) return false;
        val f = that.asInstanceOf[OMFloat];
 		if(!this.sameAttributes(f)) return false;
        this.dec == f.dec
 	}

  def subTreeHash():Tuple2[java.lang.Integer, String] = {
    (1,  OpenMathBase.b64md5String(getHex + ":Float") )
  }

}


object OMFloat {

	val hexchars:Array[String] = Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
	     "a", "b", "c", "d", "e", "f")
	val hexToInt = scala.collection.immutable.Map(
	    '0' -> 0,  '1' -> 1,
	    '2' -> 2,  '3' -> 3,
	    '4' -> 4,  '5' -> 5,
	    '6' -> 6,  '7' -> 7,
	    '8' -> 8,  '9' -> 9,
	    'a' -> 10, 'b' -> 11,
	    'c' -> 12, 'd' -> 13,
	    'e' -> 14, 'f' -> 15,
	    'A' -> 10, 'B' -> 11,
	    'C' -> 12, 'D' -> 13,
	    'E' -> 14, 'F' -> 15
	)

    /**
     * Converts an IEEE bytes-representation where the first byte
     * is most significant into a double. This is needed to deal with
     * the binary encoding.
     * We in fact use char instead of byte, since that's what you get out of readers.
     * @param b the bytes in question
     * @return the double
     */
    def bytes2double(b:Array[Char]):Double = {
        var accum:Long = 0;
        for (i <- 0 to 7) {
            accum |= (( b(7-i) & 0xff ).asInstanceOf[Long] << 8*i);
        }
        java.lang.Double.longBitsToDouble(accum);
    }

    /**
     * Converts a double into the IEEE bytes-representation where the first byte
     * is most significant. This is needed to deal with the binary encoding.
     * We in fact use char instead of byte, since that's what you get out of readers.
     * @param d the double in question
     * @return the bytes-array
     */
    def double2bytes(d:Double):Array[Char] = {
        val l:Long = java.lang.Double.doubleToRawLongBits(d);
        Array(
            ((l>>56) & 0xff).asInstanceOf[Char],
            ((l>>48) & 0xff).asInstanceOf[Char],
            ((l>>40) & 0xff).asInstanceOf[Char],
            ((l>>32) & 0xff).asInstanceOf[Char],
            ((l>>24) & 0xff).asInstanceOf[Char],
            ((l>>16) & 0xff).asInstanceOf[Char],
            ((l>>8) & 0xff).asInstanceOf[Char],
            (l & 0xff).asInstanceOf[Char]
        )
    }

	def charArrayToHexString(str:Array[Char]):String = {
		val s = new StringBuffer();
	    for(i:Int <- 0 to str.length-1) {
			s.append(hexchars.apply(str(i) / 16))
			s.append(hexchars.apply(str(i) % 16))
		}
	    s.toString()
	}

	def hexStringToCharArray(str:String):Array[Char] = {
		var r = Array[Char](0,0,0,0,0,0,0,0)
        if(str.length != 16)
            throw new RuntimeException("Couldn't parse Hex-Float, incorrect length.");
		
	    for(i:Int <- 0 to (str.length-1)/2) 
			r(i) = (16*hexToInt(str.charAt(2*i)) + hexToInt(str.charAt(2*i+1))).asInstanceOf[Char]
		r
	}
}