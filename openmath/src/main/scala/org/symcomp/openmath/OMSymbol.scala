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

import java.math.BigInteger;

/**
 * Representing the OpenMath symbol node <tt>&lt;OMS ... /&gt;</tt> 
 */
case class OMSymbol(cd:String, name:String) extends OpenMathBase {
	
    def getName() = name;
    def getCd() = cd;
	def fullname() = cd + "." + name;

 	override def equals(that:Any):Boolean = {
        if (that.isInstanceOf[String] || that.isInstanceOf[java.lang.String]) {
            val s = that.asInstanceOf[String]
            s.trim().equals(this.fullname())
        } else {
            if (!that.isInstanceOf[OMSymbol]) return false
            val s:OMSymbol = that.asInstanceOf[OMSymbol]
            this.sameAttributes(s) && this.name == s.getName() && this.cd == s.getCd()
        }
    }

  override def subTreeHash():Tuple2[java.lang.Integer, String] = {
    (1, OpenMathBase.b64md5String(name + "." + cd + ":Symbol") )
  }

}
