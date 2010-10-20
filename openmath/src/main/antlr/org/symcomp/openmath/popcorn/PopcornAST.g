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

grammar PopcornAST;

options {
	//backtrack=true;
	output=AST;
	ASTLabelType=CommonTree; // type of $stat.tree ref etc...
}

tokens {
    PREFIX;
	EMPTYCOMMALIST;
}

@parser::header {
	package org.symcomp.openmath.popcorn; 
}

@lexer::header {
	package org.symcomp.openmath.popcorn; 
}

@members { 
	protected void mismatch(IntStream input, int ttype, BitSet follow) 
	throws RecognitionException { 
		throw new MismatchedTokenException(ttype, input); 
	} 

	public void recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) 
	throws RecognitionException { 
		throw e; 
	} 
} 

// Alter code generation so catch-clauses get replace with 
// this action. 
@rulecatch { 
	catch (RecognitionException e) { 
		throw new RuntimeException(e.getMessage());
	} 
} 

start		      : expr ;
expr		      : blockExpr ;
blockExpr 	  : assignExpr (';'^ assignExpr)* ;
assignExpr 	  : implExpr (':='^ implExpr)? ;
implExpr 	    : orExpr (('==>'^|'<=>'^) orExpr)? ;
orExpr		    : andExpr ('or'^ andExpr)* ;
andExpr 	    : relExpr ('and'^ relExpr)* ;
relExpr 	    : intervalExpr (('='^|'<'^|'<='^|'>'^|'>='^|'!='^|'<>'^|'~'^) intervalExpr)? ;
intervalExpr	: addExpr ('..'^ addExpr)? ;
addExpr		    : negExpr (('-'^|'+'^) negExpr)* ;
negExpr			: unaryOp multExpr -> ^(PREFIX unaryOp multExpr)
					| multExpr;
multExpr 	    : powerExpr (('/'^|'*'^) powerExpr)* ;
powerExpr 	  : complexExpr ('^'^ complexExpr)? ;
complexExpr	  : rationalExpr ('|'^ rationalExpr)? ;
rationalExpr 	: compExpr ('//'^ compExpr)? ;
compExpr	    : (anchor '(')				=> call
			        | (anchor '!' '(') 			=> ecall
			        | (anchor '{') 				=> attribution
			        | (anchor '[') 				=> binding			
			        | ('[')						=> listExpr
			        | ('{')						=> setExpr
			        | anchor
		 	        ;
commalist	    : -> EMPTYCOMMALIST
			        | expr (','^ expr)*
			        ;
call		      : anchor '('^ commalist ')'! ;
ecall		      : anchor '!'^ '('! commalist ')'! ;
listExpr	    : '['^ commalist ']'! ;
setExpr		    : '{'^ commalist '}'! ;
foreignExpr   : '`'^ FOREIGN '`'! ;
attribution	  : anchor '{'! attributionList '}'^ ;
attributionList	: attributionPair (','^ attributionPair)* ;
attributionPair : expr '->'^ expr ;
binding 	    : anchor '['! commalist '->'^ expr ']'! ;
anchor 		    : atom ((':'^ ID) | ('::'^ atom))? ;
atom		      : ('(')	 		=> paraExpr
			        | ID
			        | STRING
			        | symbol
			        | var
			        | intt
			        | floatt
			        | ref
			        | OMB
			        | FOREIGN
			        | ('if') 		=> ifExpr
			        | ('while') 	=> whileExpr
			        ;
paraExpr 	    : '('! expr ')'! ;
ifExpr		    : 'if'^ c=expr 'then'! expr 'else'! expr 'endif'! ;
whileExpr	    : 'while'^ expr 'do'! expr 'endwhile'! ;

unaryOp		    : '-';

ID			      : (('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* 
              | ('\'' (~'\'')* '\''))
              ;
//(':' | 'A'..'Z' | '_' | 'a'..'z' | '\u00c0'..'\u00d6' | '\u00d8'..'\u00f6' |
//				        '\u00f8'..'\u02ff' | '\u0370'..'\u037d' | '\u037f'..'\u1fff' | '\u200c'..'\u200d' |
//				        '\u2070'..'\u218f' | '\u2c00'..'\u2fef' | '\u3001'..'\ud7ff' |
//				        '\uf900'..'\ufdcf' | '\ufdf0'..'\ufffd' )
//				        (':' | 'A'..'Z' | '_' | 'a'..'z' | '\u00c0'..'\u00d6' | '\u00d8'..'\u00f6' | '\u00f8'..'\u02ff' |
//				        '\u0370'..'\u037d' | '\u037f'..'\u1fff' | '\u200c'..'\u200d' | '\u2070'..'\u218f' |
//				        '\u2c00'..'\u2fef' | '\u3001'..'\ud7ff' | '\uf900'..'\ufdcf' | '\ufdf0'..'\ufffd' |
//				        '-' | '.' | '0'..'9' | '\u00b7' | '\u0300'..'\u036f' | '\u203f'..'\u2040')*
//			          '\''))
//			        ;
symbol  	    : ID '.'^ ID;
var		        : '$'^ ID;
ref			      : lref | GREF ;
lref		      : '#'^ ID;
GREF          : '##' .+ '##';
OMB			      : '\%'('a'..'z'|'A'..'Z'|'0'..'9'|'=')+'\%';
STRING     	  : '"' (~('"'|'\\'))* (('\\"'|'\\\\')(~('"'|'\\'))*)* '"';
intt 		      : HEXINT | DECINT ;
DECINT 		    : '0'..'9'+ ;
HEXINT 		    : '0x'('a'..'f'|'A'..'F'|'0'..'9')+;
floatt		    : HEXFLOAT | DECFLOAT;
HEXFLOAT 	    : '0f'('a'..'f'|'A'..'F'|'0'..'9');
DECFLOAT 	    : '0'..'9'+ '.' '0'..'9'+ ('e' '-'? '0'..'9'+)?;
FOREIGN       : '`' .* '<' .+ '>`';
WS 			      : (' '|'\t'|'\n'|'\r')+ {skip();} ;
COMMENT		    : '/*' .* '*/' { skip(); };

