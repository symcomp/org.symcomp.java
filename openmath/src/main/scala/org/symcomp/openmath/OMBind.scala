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
 * Representing the OpenMath bind node <tt>&lt;OMBIND&gt;</tt>, note that there is no
 * class to represent the <tt>&lt;OMBVAR&gt;</tt> node, the semantics for this is
 * integrated into OMBind
 */
case class OMBind(symbol:OMSymbol, bvars:Array[OMVariable], param:OpenMathBase) extends OpenMathBase {

    def getSymbol() = symbol
    def getBvars() = bvars
    def getParam() = param

 	override def equals(that:Any):Boolean = {
        if (!that.isInstanceOf[OMBind]) return false;
        val t = that.asInstanceOf[OMBind];
 		if(!this.sameAttributes(t)) return false;
 		if (!this.symbol.equals(t.getSymbol()) || !this.param.equals(t.getParam())) return false;
        val omv = t.getBvars();
 		if (this.bvars.length != omv.length) return false;
        !this.bvars.zip(omv).exists(t => (t._1 != t._2))
 	}

    override def subTreeHash():Tuple2[java.lang.Integer, String] = {
        val eparam = param.subTreeHash
        val depth:Int = eparam._1.asInstanceOf[Int]
        val vstr = (bvars.map { b => b.subTreeHash._2 + "::" }).foldLeft("Bvars::" + bvars.length + "::")((a,b) => a+b)
        (depth + 1,  OpenMathBase.b64md5String(eparam + ":Param--" + vstr + ":Binding") )
    }

}