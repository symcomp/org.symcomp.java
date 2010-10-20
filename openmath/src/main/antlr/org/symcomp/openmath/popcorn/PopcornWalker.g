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

tree grammar PopcornWalker;

options {
	tokenVocab=PopcornAST;
	ASTLabelType=CommonTree;
}

@header{
	package org.symcomp.openmath.popcorn;
	import  org.symcomp.openmath.*;
}

start returns [OpenMathBase value]
		: ee=expr 			{ $value = (OpenMathBase) $ee.value; }
		;
		
expr returns [Object value]             
		: ^(';'   a1=expr a2=expr) 	{ $value = PopcornHelper.applyFlat("prog1", "block", $a1.value, $a2.value); }
		| ^(':='  b1=expr b2=expr) 	{ $value = PopcornHelper.apply("prog1", "assign", $b1.value, $b2.value); } 
		| ^('==>' c1=expr c2=expr) 	{ $value = PopcornHelper.apply("logic1", "implies", $c1.value, $c2.value); }
		| ^('<=>' d1=expr d2=expr) 	{ $value = PopcornHelper.apply("logic1", "equivalent", $d1.value, $d2.value); }
		| ^('or'  e1=expr e2=expr) 	{ $value = PopcornHelper.apply("logic1", "or", $e1.value, $e2.value); }
		| ^('and' f1=expr f2=expr) 	{ $value = PopcornHelper.apply("logic1", "and", $f1.value, $f2.value); }
		| ^('<'   g1=expr g2=expr) 	{ $value = PopcornHelper.apply("relation1", "lt", $g1.value, $g2.value); }
		| ^('<='  h1=expr h2=expr) 	{ $value = PopcornHelper.apply("relation1", "leq", $h1.value, $h2.value); }
		| ^('>'   i1=expr i2=expr) 	{ $value = PopcornHelper.apply("relation1", "gt", $i1.value, $i2.value); }
		| ^('='   j1=expr j2=expr) 	{ $value = PopcornHelper.apply("relation1", "eq", $j1.value, $j2.value); }
		| ^('>='  k1=expr k2=expr) 	{ $value = PopcornHelper.apply("relation1", "geq", $k1.value, $k2.value); }
		| ^('!='  l1=expr l2=expr) 	{ $value = PopcornHelper.apply("relation1", "neq", $l1.value, $l2.value); }
		| ^('<>'  m1=expr m2=expr) 	{ $value = PopcornHelper.apply("relation1", "neq", $m1.value, $m2.value); }
		| ^('~'   n1=expr n2=expr) 	{ $value = PopcornHelper.apply("relation2", "approx", $n1.value, $n2.value); }
		| ^(PREFIX '-'   o1=expr) 	{ $value = PopcornHelper.applyUnaryMinus($o1.value); }
		| ^('-'   o1=expr o2=expr) 	{ $value = PopcornHelper.apply("arith1", "minus", $o1.value, $o2.value); }
		| ^('/'   q1=expr q2=expr) 	{ $value = PopcornHelper.apply("arith1", "divide", $q1.value, $q2.value); }
		| ^('*'   r1=expr r2=expr) 	{ $value = PopcornHelper.applyFlat("arith1", "times", $r1.value, $r2.value); }
		| ^('^'   s1=expr s2=expr) 	{ $value = PopcornHelper.apply("arith1", "power", $s1.value, $s2.value); }
		| ^('|'   t1=expr t2=expr) 	{ $value = PopcornHelper.apply("complex1", "complex_cartesian", $t1.value, $t2.value); }
		| ^('+'   p1=expr p2=expr) 	{ $value = PopcornHelper.applyFlat("arith1", "plus", $p1.value, $p2.value); } 
		| ^('//'  u1=expr u2=expr) 	{ $value = PopcornHelper.apply("nums1", "rational", $u1.value, $u2.value); }
		| ^('..'  w1=expr w2=expr) 	{ $value = PopcornHelper.apply("interval1", "interval", $w1.value, $w2.value); }
		| ^(','   x1=expr x2=expr)	{ $value = PopcornHelper.flatten($x1.value, $x2.value); } 
		| ^('('   y1=expr y2=expr)	{ $value = PopcornHelper.apply((OpenMathBase) $y1.value, $y2.value); }
		| EMPTYCOMMALIST			{ $value = new OpenMathBase[] {}; }
		| ^('!'   z1=expr z2=expr) 	{ $value = PopcornHelper.error($z1.value, $z2.value); }
		| ^('['   ca=expr)			{ $value = PopcornHelper.apply("list1", "list", $ca.value); }
		| ^('{'   da=expr)			{ $value = PopcornHelper.apply("set1", "set", $da.value); }
		| ^('::'  kk=expr kt=expr)              { $value = PopcornHelper.setTypedef(kk, kt); }
                | ^(':'   ea=expr i=ID) 		{ $value = PopcornHelper.setId($ea.value, PopcornHelper.fixId($i.text)); }
		| ^('}'   aa=expr ab=attributionList) 	{ $value = PopcornHelper.setAttributes($aa.value, $ab.value); }
		| ^('->'  ba=expr bb=expr bc=expr)	{ $value = PopcornHelper.bind($ba.value, $bb.value, $bc.value); }
		| ^('if'  fa=expr fb=expr fc=expr)	{ $value = PopcornHelper.apply("prog1", "if", $fa.value, $fb.value, $fc.value); }
		| ^('while' ga=expr gb=expr)		{ $value = PopcornHelper.apply("prog1", "while", $ga.value, $gb.value); }
		| atom		                    	{ $value = $atom.value; }
		;
attributionList	returns [Object value]
		: ap=attributionPair		{ $value = $ap.value; }
		| ^(','  a1=attributionPair a2=attributionPair) 
						{ $value = PopcornHelper.flatten($a1.value, $a2.value); }
		;
attributionPair returns [Object value]
		: ^('->' a1=expr a2=expr)	{ $value = new OpenMathBase[] {(OpenMathBase)$a1.value, (OpenMathBase)$a2.value}; }
		;

atom returns [OpenMathBase value]		
		: id=ID					{ $value = PopcornHelper.id2symbol($id.text); }
		| ^('.' cd=ID name=ID)	{ $value = new OMSymbol(PopcornHelper.fixId($cd.text), PopcornHelper.fixId($name.text)); }
		| ^('$' n=ID)			{ $value = new OMVariable(PopcornHelper.fixId($n.text)); }
		| ^('#' i=ID)			{ $value = new OMReference("#"+PopcornHelper.fixId($i.text)); }
		| fo=FOREIGN            { $value = PopcornHelper.foreign($fo.text.substring(1, $fo.text.length() - 1)); }
		| t=GREF				{ $value = new OMReference($t.text.substring(2, $t.text.length() - 2)); }
		| s=OMB					{ $value = new OMBinary($s.text.substring(1, $s.text.length() - 1)); }
		| s=STRING				{ $value = PopcornHelper.string($s.text); }
		| i=DECINT				{ $value = new OMInteger($i.text); }
		| j=HEXINT				{ $value = new OMInteger("x" + $j.text.substring(2)); }
		| df=DECFLOAT			{ $value = new OMFloat(new Double($df.text)); }
		| j=HEXFLOAT			{ $value = new OMFloat($j.text.substring(2)); }
		;


