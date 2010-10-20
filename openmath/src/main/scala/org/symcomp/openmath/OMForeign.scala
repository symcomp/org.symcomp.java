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

import java.math.BigInteger

case class OMForeign(content:String, encoding:String) extends OpenMathBase {

    def this(cont:String) = this(cont, null)

    def getEncoding() = encoding
    def getContent() = content

    override def equals(that:Any):Boolean = {
        if (!that.isInstanceOf[OMForeign]) return false;
        val s = that.asInstanceOf[OMForeign];
		if (!(this.sameAttributes(s))) return false;
		if (this.encoding == null && s.encoding != null) return false;
		if (this.encoding != null && !(this.encoding.equals(s.encoding))) return false;
		this.content.equals(s.content);
 	}

  override def subTreeHash():Tuple2[java.lang.Integer, String] = {
    (1, OpenMathBase.b64md5String(content+":"+encoding) + ":Foreign" )
  }


}
