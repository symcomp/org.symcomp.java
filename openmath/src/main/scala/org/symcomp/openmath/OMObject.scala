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

case class OMObject(element:OpenMathBase) extends OpenMathBase {
    
    def getElement():OpenMathBase = element

    override def equals(that:Any):Boolean = {
        if (!that.isInstanceOf[OMObject]) return false
        val s = that.asInstanceOf[OMObject]
        this.sameAttributes(s) && (this.element == s.element)
    }


    override def traverse(visitor:OpenMathVisitor):OpenMathBase = {
        OMObject(element.traverse(visitor))
    }

    override def subTreeHash():Tuple2[java.lang.Integer, String] = {
        val enc = element.subTreeHash
        (enc._1.asInstanceOf[Int] + 1, OpenMathBase.b64md5String(enc._2 + ":Object") )
    }



}

