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

class MatchTest {

    def matchPop(pop:String):String = matchOM(OpenMathBase.parsePopcorn(pop))

    def matchOM(om:OpenMathBase):String =  {
        om match {
            case OMApply(h, p)      => "Application"
            case OMBind(s, v, p)    => "Binding"
            case OMError(h, p)      => "Error"
            case OMFloat(d)         => "Float"
            case OMInteger(x)       => "Integer"
            case OMObject(elem)     => "Object"
            case OMReference(href)  => "Reference"
            case OMString(s)        => "String"
            case OMSymbol(cd,name)  => "Symbol"
            case OMVariable(name)   => "Variable"
            case _                  => "NOT_IMPLEMENTED"
        }
   }


    def testMatch1() = assert (matchOM(OMSymbol("as","def")) == "Symbol")
    def testMatch2() = assert (matchOM(new OMInteger(12)) == "Integer")
    def testMatch3() = assert (matchOM((OMSymbol("as","def"))(new OMInteger(12))) == "Application")
    def testMatch4() = assert (matchOM((new OMSymbol("ping","pong"))(new OMInteger(12))) == "Application")
    def testMatch5() = assert (matchPop("$x") == "Variable")
    def testMatch6() = assert (matchPop("#x") == "Reference")
    def testMatch7() = assert (matchPop("lambda[$x, $y -> $x+$y]") == "Binding")
    def testMatch8() = assert (matchPop("po.pa!(1)") == "Error")
    def testMatch9() = assert (matchPop("1.23") == "Float")
    def testMatch10() = assert (matchOM(new OMObject(new OMInteger(11))) == "Object")

    def testConstructApply() = {
        val om:OpenMathBase = OMApply(OMSymbol("ping", "pong"), Array(new OMInteger(1), OMVariable("x")))
        assert (om.toPopcorn == "ping.pong(1, $x)")
        om match {
            case OMApply(_, _) => assert(true)
            case _ => assert(false)
        }

        om match {
            case OMApply(OMSymbol(_,_), _) => assert(true)
            case _ => assert(false)
        }

        om match {
            case OMApply(OMSymbol(_,"pong"), _) => assert(true)
            case _ => assert(false)
        }

        om match {
            case OMApply(OMSymbol("ping",_), _) => assert(true)
            case _ => assert(false)
        }

        om match {
            case OMApply(OMSymbol("ping","plopp"), _) => assert(false)
            case _ => assert(true)
        }

        om match {
            case OMApply(_, Array(OMInteger(bi), _)) => assert(bi == BigInteger.ONE)
            case _ => assert(false)
        }

        om match {
            case OMApply(_, Array(_, OMVariable(vn))) => assert(vn == "x")
            case _ => assert(false)
        }

        om match {
            case OMApply(_, Array(_, OMVariable("vn"))) => assert(false)
            case _ => assert(true)
        }
    }

    def testConstructBind() = {
        val arg = OpenMathBase.parsePopcorn("$x + $y + 1")
        val om:OpenMathBase = OMBind(OMSymbol("ping", "pong"), 
                                     Array(OMVariable("x"), OMVariable("y")),
                                     arg)
        assert (om.toPopcorn == "ping.pong[$x,$y -> $x + $y + 1]")

        om match {
            case OMBind(_, _, _) => assert(true)
            case _ => assert(false)
        }

        om match {
            case OMApply(_, _) => assert(false)
            case _ => assert(true)
        }

        om match {
            case OMBind(s, Array(v1, v2), e) =>
                assert(s.isSymbol("ping", "pong"))
                assert(v1.isVariable("x"))
                assert(v2.isVariable("y"))
                e match {
                    case (OMApply(OMSymbol("arith1", "plus"),
                                  Array(
                                    OMVariable("x"),
                                    OMVariable("y"),
                                    OMInteger(BigInteger.ONE)))) => assert(true)
                    case _ => assert(false)
                }
            case _ => assert(false)
        }

        om match {
            case OMBind(_, Array(_, OMVariable("y")), OMApply(_, _)) => assert(true)
            case _ => assert(false)
        }

        om match {
            case OMBind(_, Array(_, OMVariable("x")), OMApply(_, _)) => assert(false)
            case _ => assert(true)
        }
    }

    def testConstructError() = {
        val om:OpenMathBase = OMError(OMSymbol("ping", "pong"), Array(OMString("baaad")))
        assert (om.toPopcorn == "ping.pong!(\"baaad\")")
        om match {
            case OMError(_, _) => assert(true)
            case _ => assert(false)
        }

        om match {
            case OMError(OMSymbol("ping",_), _) => assert(true)
            case _ => assert(false)
        }

        om match {
            case OMError(OMSymbol(_,"baad"), _) => assert(false)
            case _ => assert(true)
        }

        om match {
            case OMError(_, Array(OMString("baaad"))) => assert(true)
            case _ => assert(false)
        }

        om match {
            case OMError(_, Array(OMString(s))) => assert(s == "baaad")
            case _ => assert(false)
        }
    }

    def testApply0Arg() = {
        val s = OMSymbol("scala", "rulez")
        val o:OpenMathBase = s()
        assert(o.toPopcorn == "scala.rulez()")
        o match {
            case OMApply(OMSymbol("scala", "rulez"), Array()) =>
                assert(true)
            case _ => assert(false)
        }
    }

    def testApply1Arg() = {
        val i = OMInteger(BigInteger.ONE)
        val s = OMSymbol("scala", "rulez")
        val o:OpenMathBase = s(i)
        assert(o.toPopcorn == "scala.rulez(1)")
        o match {
            case OMApply(OMSymbol("scala", "rulez"), Array(OMInteger(ii))) =>
                assert(ii==BigInteger.ONE)
            case _ => assert(false)
        }
    }

    def testApply2Arg() = {
        val i1 = OMInteger(BigInteger.ONE)
        val i2 = OMInteger(BigInteger.ZERO)
        val s = OMSymbol("scala", "rulez")
        val o:OpenMathBase = s(i1, i2)
        assert(o.toPopcorn == "scala.rulez(1, 0)")
        o match {
            case OMApply(OMSymbol("scala", "rulez"), Array(OMInteger(ii), OMInteger(BigInteger.ZERO))) =>
                assert(ii==BigInteger.ONE)
            case _ => assert(false)
        }
    }

    def testPlus() = {
        val i1 = OMInteger(BigInteger.ONE)
        val i2 = OMInteger(BigInteger.ZERO)
        val o:OpenMathBase = i1 + i2
        assert(o.toPopcorn == "1 + 0")
        o match {
            case OMApply(OMSymbol("arith1", "plus"), _) => assert(true)
            case _ => assert(false)
        }
    }
    
//
//
//    def testQQ0() = assert ("1/2" == Omscala.evaluateToQQ((OpenMathBase.parsePopcorn("1//2"))).toString())
//    def testQQ1() = {
//       val om:OpenMathBase = new OMInteger(22);
//       println(Omscala.evaluateToQQ(om).denominator())
//    }
}
