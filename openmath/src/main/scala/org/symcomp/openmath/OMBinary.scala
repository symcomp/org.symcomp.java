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

package org.symcomp.openmath

case class OMBinary(value:Array[Byte]) extends OpenMathBase {

    def this(base64:String) = this(Base64.decode(base64))

    def getByteValue() = value
    def getBase64Value() = Base64.encodeBytes(value);

 	override def equals(that:Any):Boolean = {
        if (!that.isInstanceOf[OMBinary]) return false;
        val b = that.asInstanceOf[OMBinary]
 		if(!this.sameAttributes(b)) return false;
 		java.util.Arrays.equals(value, b.value);
 	}

    def subTreeHash():Tuple2[java.lang.Integer, String] = {
        (1,  OpenMathBase.b64md5String(OpenMathBase.b64md5String(value) + ":Binary") )
    }

}
