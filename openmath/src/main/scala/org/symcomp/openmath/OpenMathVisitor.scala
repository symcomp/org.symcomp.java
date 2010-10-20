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

abstract class OpenMathVisitor {

    def visit(om:OpenMathBase):OpenMathBase = {
        om match {
            case oma:OMApply     => visitApply(oma)
            case omy:OMBinary    => visitBinary(omy)
            case omb:OMBind      => visitBind(omb)
            case omc:OMContainer => visitContainer(omc)
            case ome:OMError     => visitError(ome)
            case omf:OMFloat     => visitFloat(omf)
            case omz:OMForeign   => visitForeign(omz)
            case omi:OMInteger   => visitInteger(omi)
            case omo:OMObject    => visitObject(omo)
            case omr:OMReference => visitReference(omr)
            case omx:OMString    => visitString(omx)
            case oms:OMSymbol    => visitSymbol(oms)
            case omv:OMVariable  => visitVariable(omv)
            case _               => throw new RuntimeException("This should never happen");
        }
    }

    def visitApply(om:OMApply):OpenMathBase = {
        val nhead:OpenMathBase = this.visit(om.head)
        val nparams:Array[OpenMathBase] = om.params.map(_.traverse(this))
        OMApply(nhead, nparams) 
    }
    def visitBinary(om:OMBinary):OpenMathBase = { om }
    def visitBind(om:OMBind):OpenMathBase = {
        val nsymbol = this.visit(om.symbol)
        val nparam = this.visit(om.param)
        val nbvars = om.bvars.map(_.traverse(this))
        OMBind(nsymbol.asInstanceOf[OMSymbol], nbvars.map(_.asInstanceOf[OMVariable]), nparam) 
    }
    def visitContainer(om:OMContainer):OpenMathBase = { om }
    def visitError(om:OMError):OpenMathBase = {
        val nhead = this.visit(om.head)
        val nparams = om.params.map(_.traverse(this))
        OMError(nhead, nparams) 
    }
    def visitFloat(om:OMFloat):OpenMathBase = { om }
    def visitForeign(om:OMForeign):OpenMathBase = { om }
    def visitInteger(om:OMInteger):OpenMathBase = { om }
    def visitObject(om:OMObject):OpenMathBase = { om }
    def visitReference(om:OMReference):OpenMathBase = { om }
    def visitString(om:OMString):OpenMathBase = { om }
    def visitSymbol(om:OMSymbol):OpenMathBase = { om }
    def visitVariable(om:OMVariable):OpenMathBase = { om }
    
}
