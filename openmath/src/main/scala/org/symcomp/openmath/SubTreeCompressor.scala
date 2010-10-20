//---------------------------------------------------------------------------
//  Copyright 2006-2010
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

import collection.mutable.{LinkedListLike, LinkedList, HashMap}

class SubTreeCompressor(prefix:String) extends OpenMathVisitor {
    val subtrees = new HashMap[Tuple2[java.lang.Integer, String], OpenMathBase]

    def compress(om: OpenMathBase):OpenMathBase = {
        subtrees.clear
        val st = (new java.util.Date()).getTime()
        val res = om.traverse(this)
        println("Compression took:" + ((new java.util.Date()).getTime() - st))
        subtrees.clear
        res
    }
    
    def replacer(om: OpenMathBase):OpenMathBase = {
        val idx = om.subTreeHash
        var ret = om
        if (om.isReference) return om
        if(!subtrees.isDefinedAt(idx)) {
            subtrees.put(idx, ret)
        } else {
            val blueprint = subtrees(idx)
            if (blueprint.getId == null) blueprint.setId("#"+prefix+subtrees.size)
            ret = OMReference(blueprint.getId)
        }
        ret
    }
    override def visitApply(om:OMApply):OpenMathBase = {
        val res = replacer(om)
        if (res == om)
            super.visitApply(om)
        else
            res
    }
    override def visitBind(om:OMBind):OpenMathBase = {
        val res = replacer(om)
        if (res == om)
            super.visitBind(om)
        else
            res
    }
    override def visitSymbol(om:OMSymbol):OpenMathBase = replacer(om)
    override def visitInteger(om:OMInteger):OpenMathBase = 
        if (om.getIntValue.bitCount > 10*8) replacer(om) else om
    override def visitString(om:OMString):OpenMathBase = 
        if (om.getValue.length > 6) replacer(om) else om
    
}
