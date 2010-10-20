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

class ContainerTest {

    def test0:Unit = {

        case class II(i:Int) extends OMContainer {
            def toOpenMath:OpenMathBase = new OMInteger(i)
        }

        val oo = OMApply(OMSymbol("arith1", "plus"), Array(new OMInteger(1), II(9)))

        assert(oo.toPopcorn == "1 + 9")
        assert(oo.toLatex == "1 + 9")
        assert(oo.toXml == "<OMA><OMS cd=\"arith1\" name=\"plus\"/><OMI>1</OMI><OMI>9</OMI></OMA>")
        val l = oo.toBinary
        val ll = l.map({ x:Char => x.toInt }).mkString("Array(", ", ", ")")
        assert (ll == "Array(24, 16, 8, 6, 4, 97, 114, 105, 116, 104, 49, 112, 108, 117, 115, 1, 1, 1, 9, 17, 25)")
    }

    def test1:Unit = {

        case class VV(i:Int) extends OMContainer {
            def toOpenMath:OpenMathBase = OMVariable("X"+i)
        }

        val oo = OMApply(OMSymbol("arith1", "plus"), Array(new OMInteger(1), VV(9)))

        assert(oo.toPopcorn == "1 + $X9")
        assert(oo.toLatex == "1 + {X9}")
        assert(oo.toXml == "<OMA><OMS cd=\"arith1\" name=\"plus\"/><OMI>1</OMI><OMV name=\"X9\"/></OMA>")
        val l = oo.toBinary
        val ll = l.map({ x:Char => x.toInt }).mkString("Array(", ", ", ")")
        assert (ll == "Array(24, 16, 8, 6, 4, 97, 114, 105, 116, 104, 49, 112, 108, 117, 115, 1, 1, 5, 2, 88, 57, 17, 25)")
    }
}
