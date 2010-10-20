package org.symcomp.openmath;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;


public class TraverseTest {

    public void test1() throws OpenMathException {
        OMObject o = OpenMathBase.parsePopcorn("1+2").toOMObject();
        OMObject o2 = (OMObject) o.traverse(new OpenMathVisitor() {
            public OpenMathBase visitInteger(OMInteger om) { return new OMInteger(om.getIntValue().add(BigInteger.ONE)); }
        });
        assert o2.toPopcorn().equals("2 + 3");
    }

    public void test2() throws OpenMathException {
        OMObject o = OpenMathBase.parsePopcorn("1+$a").toOMObject();
        OpenMathBase oo = o.traverse(new OpenMathVisitor() {
            public OpenMathBase visitInteger(OMInteger om) { return new OMInteger(om.getIntValue().add(new BigInteger("1"))); }
            public OpenMathBase visitVariable(OMVariable om) { return new OMVariable(om.getName()+"b"); }
        });
        assert oo.toPopcorn().equals("2 + $ab");
    }


    public void test3() throws OpenMathException {
        OMObject o = OpenMathBase.parsePopcorn("sin(1+$a)").toOMObject();
        OpenMathBase oo = o.traverse(new OpenMathVisitor() {
            public OpenMathBase visitInteger(OMInteger om) { return new OMInteger(om.getIntValue().add(new BigInteger("1"))); }
            public OpenMathBase visitVariable(OMVariable om) { return new OMVariable(om.getName()+"b"); }
            public OpenMathBase visitSymbol(OMSymbol om) { return new OMSymbol(om.getName(), om.getCd()); }
        });
        assert oo.isObject();
        assert oo.toPopcorn().equals("sin.transc1(plus.arith1(2, $ab))");

    }

    class SymbolExtractor extends OpenMathVisitor {
            public Set<OMSymbol> symbs = new HashSet();
            public OpenMathBase visitSymbol(OMSymbol oms) {
                symbs.add(oms);
                return oms;
            }
    };

    public Set<OMSymbol> getSymbolList(String str) throws Exception {
        OpenMathBase o = OpenMathBase.parsePopcorn(str);
        //System.out.println(o.toXml());
        SymbolExtractor visitor = new SymbolExtractor();
        o.traverse(visitor);
        return visitor.symbs;
        //for (OMSymbol s : symbs) {
        //    System.out.println(s.toXml());
        //}
    }

    public void testExtract() throws Exception {
        Set<OMSymbol> ss = getSymbolList("sds.sdsd(1+sds.sdsd,2,abc.def,lambda[$x->1/$x^2+abc.def])");
        assert ss.contains(new OMSymbol("arith1", "power"));
        assert ss.contains(new OMSymbol("fns1", "lambda"));
        assert ss.contains(new OMSymbol("abc", "def"));
        assert ss.contains(new OMSymbol("arith1", "plus"));
        assert ss.contains(new OMSymbol("sds", "sdsd"));
        assert ss.contains(new OMSymbol("arith1", "divide"));
        assert ss.size() == 6;
    }


}
