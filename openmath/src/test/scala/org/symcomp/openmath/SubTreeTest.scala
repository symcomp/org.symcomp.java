package org.symcomp.openmath

import collection.mutable.{LinkedListLike, LinkedList, HashMap}

class SubTreeTest {
    

    def testApply():Unit = {
        val cpr = new SubTreeCompressor("stc")
        val muff = cpr.compress(OpenMathBase.parse("a.b(1+1, 1+1)"))
        println(muff.toXml)
    }

    def testBind():Unit = {
        val cpr = new SubTreeCompressor("stc")
        val muff = cpr.compress(OpenMathBase.parse("a.b(1+$x, lambda[$x -> 1+$x])"))
        println(muff.toXml)
    }
    
}