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
 * Representing the OpenMath apply node <tt>&lt;OMA&gt;</tt>
 */
case class OMApply(head:OpenMathBase, params:Array[OpenMathBase]) extends OpenMathBase {

    def this(head:OpenMathBase, params:OpenMathBase*) =
        this(head, params.asInstanceOf[Array[OpenMathBase]])

    def this (cd:String, name:String, params:OpenMathBase*) = 
        this(new OMSymbol(cd,name), params.asInstanceOf[Array[OpenMathBase]])

    def getHead() = head
    def getParams():Array[OpenMathBase] = return params
    def getParamsLength() = params.length
    def getParam(n:Int) = params(n)

    override def equals(that:Any):Boolean = {
        if (!that.isInstanceOf[OMApply]) return false;
        val thatt = that.asInstanceOf[OMApply]
 		if(!this.sameAttributes(thatt)) return false;
        if (this.head != thatt.getHead()) return false;
 		if (this.params.length != thatt.params.length) return false
        !this.params.zip(thatt.params).exists(t => (t._1 != t._2))
    }

    // override def traverse(visitor:OpenMathVisitor):OpenMathBase = {
    //     visitor.visit(this);
    // }

    override def subTreeHash():Tuple2[java.lang.Integer, String] = {
        val eparams : Array[Pair[Int, String]] = (params.map { p => p.subTreeHash }).asInstanceOf[Array[Pair[Int, String]]]
        val depth = (eparams.map { p => p._1 }).max
        val pstr = (eparams.map { p => p._2 + "::" }).foldLeft("Apply::" + params.length + "::")((a,b) => a+b)
        (depth + 1,  OpenMathBase.b64md5String(pstr) )
    }

    
}