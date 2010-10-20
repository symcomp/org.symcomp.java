package org.symcomp.openmath.popcorn;

import org.symcomp.openmath.*;

import java.util.HashMap;

public class PopcornHelper {

    public static OpenMathBase typeSymbol = null;

    private static HashMap<String, OMSymbol> translate;
    static {
  		translate = new HashMap<String, OMSymbol>();
  		// these are all unique names in the standard CDs
  		// Christoph Lange made this list using the SPARQL on request on https://svn.openmath.org/www/cdfiles2/cd/ 
  		//
      // select ?n1 ?cn1 {
      //          ?s1 a omo:SymbolDefinition ;
      //        dc:identifier ?n1 .
      //          ?c1 a omo:ContentDictionary ;
      //              dc:identifier ?cn1 ;
      //        omo:containsSymbolDefinition ?s1 .
      //          OPTIONAL {
      //        ?c2 a omo:ContentDictionary ;
      //            dc:identifier ?cn2 ;
      //            omo:containsSymbolDefinition [
      //          a omo:SymbolDefinition ;
      //          dc:identifier ?n1 ]
      //        FILTER (?c2 != ?c1)
      //          }
      //          FILTER (!bound(?c2))
      //       } order by ?cn1 ?n1
      
      translate.put("one", new OMSymbol("alg1", "one"));
      translate.put("LaTeX_encoding", new OMSymbol("altenc", "LaTeX_encoding"));
      translate.put("MathML_encoding", new OMSymbol("altenc", "MathML_encoding"));
      translate.put("abs", new OMSymbol("arith1", "abs"));
      translate.put("product", new OMSymbol("arith1", "product"));
      translate.put("root", new OMSymbol("arith1", "root"));
      translate.put("sum", new OMSymbol("arith1", "sum"));
      translate.put("unary_minus", new OMSymbol("arith1", "unary_minus"));
      translate.put("extended_gcd", new OMSymbol("arith3", "extended_gcd"));
      translate.put("bigfloat", new OMSymbol("bigfloat1", "bigfloat"));
      translate.put("bigfloatprec", new OMSymbol("bigfloat1", "bigfloatprec"));
      translate.put("defint", new OMSymbol("calculus1", "defint"));
      translate.put("diff", new OMSymbol("calculus1", "diff"));
      translate.put("int", new OMSymbol("calculus1", "int"));
      translate.put("nthdiff", new OMSymbol("calculus1", "nthdiff"));
      translate.put("partialdiff", new OMSymbol("calculus1", "partialdiff"));
      translate.put("int2flt", new OMSymbol("coercions", "int2flt"));
      translate.put("Bell", new OMSymbol("combinat1", "Bell"));
      translate.put("Fibonacci", new OMSymbol("combinat1", "Fibonacci"));
      translate.put("Stirling1", new OMSymbol("combinat1", "Stirling1"));
      translate.put("Stirling2", new OMSymbol("combinat1", "Stirling2"));
      translate.put("binomial", new OMSymbol("combinat1", "binomial"));
      translate.put("multinomial", new OMSymbol("combinat1", "multinomial"));
      translate.put("argument", new OMSymbol("complex1", "argument"));
      translate.put("complex_cartesian", new OMSymbol("complex1", "complex_cartesian"));
      translate.put("complex_polar", new OMSymbol("complex1", "complex_polar"));
      translate.put("conjugate", new OMSymbol("complex1", "conjugate"));
      translate.put("imaginary", new OMSymbol("complex1", "imaginary"));
      translate.put("real", new OMSymbol("complex1", "real"));
      translate.put("acceleration", new OMSymbol("dimensions1", "acceleration"));
      translate.put("area", new OMSymbol("dimensions1", "area"));
      translate.put("charge", new OMSymbol("dimensions1", "charge"));
      translate.put("concentration", new OMSymbol("dimensions1", "concentration"));
      translate.put("current", new OMSymbol("dimensions1", "current"));
      translate.put("density", new OMSymbol("dimensions1", "density"));
      translate.put("displacement", new OMSymbol("dimensions1", "displacement"));
      translate.put("energy", new OMSymbol("dimensions1", "energy"));
      translate.put("force", new OMSymbol("dimensions1", "force"));
      translate.put("mass", new OMSymbol("dimensions1", "mass"));
      translate.put("momentum", new OMSymbol("dimensions1", "momentum"));
      translate.put("pressure", new OMSymbol("dimensions1", "pressure"));
      translate.put("resistance", new OMSymbol("dimensions1", "resistance"));
      translate.put("speed", new OMSymbol("dimensions1", "speed"));
      translate.put("temperature", new OMSymbol("dimensions1", "temperature"));
      translate.put("time", new OMSymbol("dimensions1", "time"));
      translate.put("velocity", new OMSymbol("dimensions1", "velocity"));
      translate.put("voltage", new OMSymbol("dimensions1", "voltage"));
      translate.put("volume", new OMSymbol("dimensions1", "volume"));
      translate.put("decide", new OMSymbol("directives1", "decide"));
      translate.put("disprove", new OMSymbol("directives1", "disprove"));
      translate.put("evaluate_to_type", new OMSymbol("directives1", "evaluate_to_type"));
      translate.put("explore", new OMSymbol("directives1", "explore"));
      translate.put("find", new OMSymbol("directives1", "find"));
      translate.put("look_up", new OMSymbol("directives1", "look_up"));
      translate.put("prove", new OMSymbol("directives1", "prove"));
      translate.put("prove_in_theory", new OMSymbol("directives1", "prove_in_theory"));
      translate.put("response", new OMSymbol("directives1", "response"));
      translate.put("Tuple", new OMSymbol("ecc", "Tuple"));
      translate.put("unexpected_symbol", new OMSymbol("error", "unexpected_symbol"));
      translate.put("unhandled_symbol", new OMSymbol("error", "unhandled_symbol"));
      translate.put("unsupported_CD", new OMSymbol("error", "unsupported_CD"));
      translate.put("field", new OMSymbol("field1", "field"));
      translate.put("is_subfield", new OMSymbol("field1", "is_subfield"));
      translate.put("subfield", new OMSymbol("field1", "subfield"));
      translate.put("field_by_poly", new OMSymbol("field3", "field_by_poly"));
      translate.put("fraction_field", new OMSymbol("field3", "fraction_field"));
      translate.put("free_field", new OMSymbol("field3", "free_field"));
      translate.put("field_by_poly_map", new OMSymbol("field4", "field_by_poly_map"));
      translate.put("field_by_poly_vector", new OMSymbol("field4", "field_by_poly_vector"));
      translate.put("domainofapplication", new OMSymbol("fns1", "domainofapplication"));
      translate.put("image", new OMSymbol("fns1", "image"));
      translate.put("lambda", new OMSymbol("fns1", "lambda"));
      translate.put("left_inverse", new OMSymbol("fns1", "left_inverse"));
      translate.put("range", new OMSymbol("fns1", "range"));
      translate.put("right_inverse", new OMSymbol("fns1", "right_inverse"));
      translate.put("apply_to_list", new OMSymbol("fns2", "apply_to_list"));
      translate.put("function", new OMSymbol("fns3", "function"));
      translate.put("specification", new OMSymbol("fns3", "specification"));
      translate.put("character_table", new OMSymbol("gp1", "character_table"));
      translate.put("character_table_of_group", new OMSymbol("gp1", "character_table_of_group"));
      translate.put("declare_group", new OMSymbol("gp1", "declare_group"));
      translate.put("element_set", new OMSymbol("gp1", "element_set"));
      translate.put("is_abelian", new OMSymbol("gp1", "is_abelian"));
      translate.put("arrowset", new OMSymbol("graph1", "arrowset"));
      translate.put("digraph", new OMSymbol("graph1", "digraph"));
      translate.put("edgeset", new OMSymbol("graph1", "edgeset"));
      translate.put("graph", new OMSymbol("graph1", "graph"));
      translate.put("source", new OMSymbol("graph1", "source"));
      translate.put("target", new OMSymbol("graph1", "target"));
      translate.put("vertexset", new OMSymbol("graph1", "vertexset"));
      translate.put("right_inverse_multiplication", new OMSymbol("group2", "right_inverse_multiplication"));
      translate.put("GL", new OMSymbol("group3", "GL"));
      translate.put("GLn", new OMSymbol("group3", "GLn"));
      translate.put("SL", new OMSymbol("group3", "SL"));
      translate.put("SLn", new OMSymbol("group3", "SLn"));
      translate.put("alternatingn", new OMSymbol("group3", "alternatingn"));
      translate.put("centralizer", new OMSymbol("group3", "centralizer"));
      translate.put("free_group", new OMSymbol("group3", "free_group"));
      translate.put("normalizer", new OMSymbol("group3", "normalizer"));
      translate.put("symmetric_groupn", new OMSymbol("group3", "symmetric_groupn"));
      translate.put("are_conjugate", new OMSymbol("group4", "are_conjugate"));
      translate.put("conjugacy_class_representatives", new OMSymbol("group4", "conjugacy_class_representatives"));
      translate.put("conjugacy_classes", new OMSymbol("group4", "conjugacy_classes"));
      translate.put("left_coset", new OMSymbol("group4", "left_coset"));
      translate.put("left_coset_representative", new OMSymbol("group4", "left_coset_representative"));
      translate.put("left_cosets", new OMSymbol("group4", "left_cosets"));
      translate.put("left_transversal", new OMSymbol("group4", "left_transversal"));
      translate.put("right_coset", new OMSymbol("group4", "right_coset"));
      translate.put("right_coset_representative", new OMSymbol("group4", "right_coset_representative"));
      translate.put("right_cosets", new OMSymbol("group4", "right_cosets"));
      translate.put("left_quotient_map", new OMSymbol("group5", "left_quotient_map"));
      translate.put("right_quotient_map", new OMSymbol("group5", "right_quotient_map"));
      translate.put("generalized_quaternion_group", new OMSymbol("groupname1", "generalized_quaternion_group"));
      translate.put("IndType", new OMSymbol("icc", "IndType"));
      translate.put("indNat", new OMSymbol("indnat", "indNat"));
      translate.put("succ", new OMSymbol("indnat", "succ"));
      translate.put("euler", new OMSymbol("integer1", "euler"));
      translate.put("factorial", new OMSymbol("integer1", "factorial"));
      translate.put("factorof", new OMSymbol("integer1", "factorof"));
      translate.put("leading_monomial", new OMSymbol("integer1", "leading_monomial"));
      translate.put("leading_term", new OMSymbol("integer1", "leading_term"));
      translate.put("ord", new OMSymbol("integer1", "ord"));
      translate.put("integer_interval", new OMSymbol("interval1", "integer_interval"));
      translate.put("interval", new OMSymbol("interval1", "interval"));
      translate.put("interval_cc", new OMSymbol("interval1", "interval_cc"));
      translate.put("interval_co", new OMSymbol("interval1", "interval_co"));
      translate.put("interval_oc", new OMSymbol("interval1", "interval_oc"));
      translate.put("interval_oo", new OMSymbol("interval1", "interval_oo"));
      translate.put("Lambda", new OMSymbol("lc", "Lambda"));
      translate.put("PiType", new OMSymbol("lc", "PiType"));
      translate.put("above", new OMSymbol("limit1", "above"));
      translate.put("below", new OMSymbol("limit1", "below"));
      translate.put("both_sides", new OMSymbol("limit1", "both_sides"));
      translate.put("limit", new OMSymbol("limit1", "limit"));
      translate.put("null", new OMSymbol("limit1", "null"));
      translate.put("determinant", new OMSymbol("linalg1", "determinant"));
      translate.put("matrix_selector", new OMSymbol("linalg1", "matrix_selector"));
      translate.put("outerproduct", new OMSymbol("linalg1", "outerproduct"));
      translate.put("scalarproduct", new OMSymbol("linalg1", "scalarproduct"));
      translate.put("transpose", new OMSymbol("linalg1", "transpose"));
      translate.put("vector_selector", new OMSymbol("linalg1", "vector_selector"));
      translate.put("vectorproduct", new OMSymbol("linalg1", "vectorproduct"));
      translate.put("matrixrow", new OMSymbol("linalg2", "matrixrow"));
      translate.put("matrixcolumn", new OMSymbol("linalg3", "matrixcolumn"));
      translate.put("characteristic_eqn", new OMSymbol("linalg4", "characteristic_eqn"));
      translate.put("columncount", new OMSymbol("linalg4", "columncount"));
      translate.put("eigenvalue", new OMSymbol("linalg4", "eigenvalue"));
      translate.put("eigenvector", new OMSymbol("linalg4", "eigenvector"));
      translate.put("rowcount", new OMSymbol("linalg4", "rowcount"));
      translate.put("Hermitian", new OMSymbol("linalg5", "Hermitian"));
      translate.put("'anti-Hermitian'", new OMSymbol("linalg5", "anti-Hermitian"));
      translate.put("banded", new OMSymbol("linalg5", "banded"));
      translate.put("constant", new OMSymbol("linalg5", "constant"));
      translate.put("diagonal_matrix", new OMSymbol("linalg5", "diagonal_matrix"));
      translate.put("'lower-Hessenberg'", new OMSymbol("linalg5", "lower-Hessenberg"));
      translate.put("'lower-triangular'", new OMSymbol("linalg5", "lower-triangular"));
      translate.put("scalar", new OMSymbol("linalg5", "scalar"));
      translate.put("'skew-symmetric'", new OMSymbol("linalg5", "skew-symmetric"));
      translate.put("tridiagonal", new OMSymbol("linalg5", "tridiagonal"));
      translate.put("'upper-Hessenberg'", new OMSymbol("linalg5", "upper-Hessenberg"));
      translate.put("'upper-triangular'", new OMSymbol("linalg5", "upper-triangular"));
      translate.put("list_to_matrix", new OMSymbol("linalg7", "list_to_matrix"));
      translate.put("list_to_vector", new OMSymbol("linalg7", "list_to_vector"));
      translate.put("difference", new OMSymbol("list1", "difference"));
      translate.put("entry", new OMSymbol("list1", "entry"));
      translate.put("list", new OMSymbol("list1", "list"));
      translate.put("list_of_lengthn", new OMSymbol("list1", "list_of_lengthn"));
      translate.put("select", new OMSymbol("list1", "select"));
      translate.put("append", new OMSymbol("list2", "append"));
      translate.put("cons", new OMSymbol("list2", "cons"));
      translate.put("first", new OMSymbol("list2", "first"));
      translate.put("list_selector", new OMSymbol("list2", "list_selector"));
      translate.put("nil", new OMSymbol("list2", "nil"));
      translate.put("rest", new OMSymbol("list2", "rest"));
      translate.put("reverse", new OMSymbol("list2", "reverse"));
      translate.put("equivalent", new OMSymbol("logic1", "equivalent"));
      translate.put("false", new OMSymbol("logic1", "false"));
      translate.put("implies", new OMSymbol("logic1", "implies"));
      translate.put("not", new OMSymbol("logic1", "not"));
      translate.put("true", new OMSymbol("logic1", "true"));
      translate.put("xor", new OMSymbol("logic1", "xor"));
      translate.put("is_associative", new OMSymbol("magma1", "is_associative"));
      translate.put("is_identity", new OMSymbol("magma1", "is_identity"));
      translate.put("is_submagma", new OMSymbol("magma1", "is_submagma"));
      translate.put("left_divides", new OMSymbol("magma1", "left_divides"));
      translate.put("left_expression", new OMSymbol("magma1", "left_expression"));
      translate.put("right_divides", new OMSymbol("magma1", "right_divides"));
      translate.put("right_expression", new OMSymbol("magma1", "right_expression"));
      translate.put("submagma", new OMSymbol("magma1", "submagma"));
      translate.put("free_magma", new OMSymbol("magma3", "free_magma"));
      translate.put("complex_cartesian_type", new OMSymbol("mathmltypes", "complex_cartesian_type"));
      translate.put("complex_polar_type", new OMSymbol("mathmltypes", "complex_polar_type"));
      translate.put("constant_type", new OMSymbol("mathmltypes", "constant_type"));
      translate.put("fn_type", new OMSymbol("mathmltypes", "fn_type"));
      translate.put("integer_type", new OMSymbol("mathmltypes", "integer_type"));
      translate.put("list_type", new OMSymbol("mathmltypes", "list_type"));
      translate.put("matrix_type", new OMSymbol("mathmltypes", "matrix_type"));
      translate.put("rational_type", new OMSymbol("mathmltypes", "rational_type"));
      translate.put("real_type", new OMSymbol("mathmltypes", "real_type"));
      translate.put("set_type", new OMSymbol("mathmltypes", "set_type"));
      translate.put("vector_type", new OMSymbol("mathmltypes", "vector_type"));
      translate.put("CD", new OMSymbol("meta", "CD"));
      translate.put("CDBase", new OMSymbol("meta", "CDBase"));
      translate.put("CDDate", new OMSymbol("meta", "CDDate"));
      translate.put("CDDefinition", new OMSymbol("meta", "CDDefinition"));
      translate.put("CDReviewDate", new OMSymbol("meta", "CDReviewDate"));
      translate.put("CDRevision", new OMSymbol("meta", "CDRevision"));
      translate.put("CDStatus", new OMSymbol("meta", "CDStatus"));
      translate.put("CDUses", new OMSymbol("meta", "CDUses"));
      translate.put("CMP", new OMSymbol("meta", "CMP"));
      translate.put("Description", new OMSymbol("meta", "Description"));
      translate.put("Example", new OMSymbol("meta", "Example"));
      translate.put("FMP", new OMSymbol("meta", "FMP"));
      translate.put("Name", new OMSymbol("meta", "Name"));
      translate.put("Role", new OMSymbol("meta", "Role"));
      translate.put("CDGroup", new OMSymbol("metagrp", "CDGroup"));
      translate.put("CDGroupDescription", new OMSymbol("metagrp", "CDGroupDescription"));
      translate.put("CDGroupMember", new OMSymbol("metagrp", "CDGroupMember"));
      translate.put("CDGroupName", new OMSymbol("metagrp", "CDGroupName"));
      translate.put("CDGroupURL", new OMSymbol("metagrp", "CDGroupURL"));
      translate.put("CDGroupVersion", new OMSymbol("metagrp", "CDGroupVersion"));
      translate.put("CDSComment", new OMSymbol("metasig", "CDSComment"));
      translate.put("CDSReviewDate", new OMSymbol("metasig", "CDSReviewDate"));
      translate.put("CDSStatus", new OMSymbol("metasig", "CDSStatus"));
      translate.put("CDSignatures", new OMSymbol("metasig", "CDSignatures"));
      translate.put("Signature", new OMSymbol("metasig", "Signature"));
      translate.put("max", new OMSymbol("minmax1", "max"));
      translate.put("min", new OMSymbol("minmax1", "min"));
      translate.put("divisor_of", new OMSymbol("monoid1", "divisor_of"));
      translate.put("is_invertible", new OMSymbol("monoid1", "is_invertible"));
      translate.put("is_submonoid", new OMSymbol("monoid1", "is_submonoid"));
      translate.put("submonoid", new OMSymbol("monoid1", "submonoid"));
      translate.put("concatenation", new OMSymbol("monoid3", "concatenation"));
      translate.put("cyclic_monoid", new OMSymbol("monoid3", "cyclic_monoid"));
      translate.put("emptyword", new OMSymbol("monoid3", "emptyword"));
      translate.put("free_monoid", new OMSymbol("monoid3", "free_monoid"));
      translate.put("maps_monoid", new OMSymbol("monoid3", "maps_monoid"));
      translate.put("strings", new OMSymbol("monoid3", "strings"));
      translate.put("algorithm", new OMSymbol("moreerrors", "algorithm"));
      translate.put("asynchronousError", new OMSymbol("moreerrors", "asynchronousError"));
      translate.put("encodingError", new OMSymbol("moreerrors", "encodingError"));
      translate.put("limitation", new OMSymbol("moreerrors", "limitation"));
      translate.put("unexpected", new OMSymbol("moreerrors", "unexpected"));
      translate.put("multiset", new OMSymbol("multiset1", "multiset"));
      translate.put("NaN", new OMSymbol("nums1", "NaN"));
      translate.put("based_integer", new OMSymbol("nums1", "based_integer"));
      translate.put("e", new OMSymbol("nums1", "e"));
      translate.put("gamma", new OMSymbol("nums1", "gamma"));
      translate.put("i", new OMSymbol("nums1", "i"));
      translate.put("infinity", new OMSymbol("nums1", "infinity"));
      translate.put("pi", new OMSymbol("nums1", "pi"));
      translate.put("rational", new OMSymbol("nums1", "rational"));
      translate.put("bytearray", new OMSymbol("omtypes", "bytearray"));
      translate.put("float", new OMSymbol("omtypes", "float"));
      translate.put("integer", new OMSymbol("omtypes", "integer"));
      translate.put("omtype", new OMSymbol("omtypes", "omtype"));
      translate.put("string", new OMSymbol("omtypes", "string"));
      translate.put("symtype", new OMSymbol("omtypes", "symtype"));
      translate.put("base", new OMSymbol("permgp1", "base"));
      translate.put("generators", new OMSymbol("permgp1", "generators"));
      translate.put("is_in", new OMSymbol("permgp1", "is_in"));
      translate.put("orbits", new OMSymbol("permgp1", "orbits"));
      translate.put("schreier_tree", new OMSymbol("permgp1", "schreier_tree"));
      translate.put("stabilizer_chain", new OMSymbol("permgp1", "stabilizer_chain"));
      translate.put("vierer_group", new OMSymbol("permgp2", "vierer_group"));
      translate.put("action", new OMSymbol("permutation1", "action"));
      translate.put("are_distinct", new OMSymbol("permutation1", "are_distinct"));
      translate.put("cycle_type", new OMSymbol("permutation1", "cycle_type"));
      translate.put("cycles", new OMSymbol("permutation1", "cycles"));
      translate.put("endomap", new OMSymbol("permutation1", "endomap"));
      translate.put("endomap_left_compose", new OMSymbol("permutation1", "endomap_left_compose"));
      translate.put("endomap_right_compose", new OMSymbol("permutation1", "endomap_right_compose"));
      translate.put("fix", new OMSymbol("permutation1", "fix"));
      translate.put("is_bijective", new OMSymbol("permutation1", "is_bijective"));
      translate.put("is_endomap", new OMSymbol("permutation1", "is_endomap"));
      translate.put("is_list_perm", new OMSymbol("permutation1", "is_list_perm"));
      translate.put("is_permutation", new OMSymbol("permutation1", "is_permutation"));
      translate.put("list_perm", new OMSymbol("permutation1", "list_perm"));
      translate.put("listendomap", new OMSymbol("permutation1", "listendomap"));
      translate.put("permutationsn", new OMSymbol("permutation1", "permutationsn"));
      translate.put("sign", new OMSymbol("permutation1", "sign"));
      translate.put("Avogadros_constant", new OMSymbol("physical_consts1", "Avogadros_constant"));
      translate.put("Boltzmann_constant", new OMSymbol("physical_consts1", "Boltzmann_constant"));
      translate.put("Faradays_constant", new OMSymbol("physical_consts1", "Faradays_constant"));
      translate.put("Loschmidt_constant", new OMSymbol("physical_consts1", "Loschmidt_constant"));
      translate.put("Planck_constant", new OMSymbol("physical_consts1", "Planck_constant"));
      translate.put("absolute_zero", new OMSymbol("physical_consts1", "absolute_zero"));
      translate.put("gas_constant", new OMSymbol("physical_consts1", "gas_constant"));
      translate.put("gravitational_constant", new OMSymbol("physical_consts1", "gravitational_constant"));
      translate.put("light_year", new OMSymbol("physical_consts1", "light_year"));
      translate.put("magnetic_constant", new OMSymbol("physical_consts1", "magnetic_constant"));
      translate.put("mole", new OMSymbol("physical_consts1", "mole"));
      translate.put("speed_of_light", new OMSymbol("physical_consts1", "speed_of_light"));
      translate.put("zero_Celsius", new OMSymbol("physical_consts1", "zero_Celsius"));
      translate.put("zero_Fahrenheit", new OMSymbol("physical_consts1", "zero_Fahrenheit"));
      translate.put("otherwise", new OMSymbol("piece1", "otherwise"));
      translate.put("piece", new OMSymbol("piece1", "piece"));
      translate.put("piecewise", new OMSymbol("piece1", "piecewise"));
      translate.put("are_on_conic", new OMSymbol("plangeo1", "are_on_conic"));
      translate.put("are_on_line", new OMSymbol("plangeo1", "are_on_line"));
      translate.put("assertion", new OMSymbol("plangeo1", "assertion"));
      translate.put("configuration", new OMSymbol("plangeo1", "configuration"));
      translate.put("conic", new OMSymbol("plangeo1", "conic"));
      translate.put("incident", new OMSymbol("plangeo1", "incident"));
      translate.put("line", new OMSymbol("plangeo1", "line"));
      translate.put("point", new OMSymbol("plangeo1", "point"));
      translate.put("corner", new OMSymbol("plangeo2", "corner"));
      translate.put("endpoint", new OMSymbol("plangeo2", "endpoint"));
      translate.put("endpoints", new OMSymbol("plangeo2", "endpoints"));
      translate.put("halfline", new OMSymbol("plangeo2", "halfline"));
      translate.put("segment", new OMSymbol("plangeo2", "segment"));
      translate.put("altitude", new OMSymbol("plangeo3", "altitude"));
      translate.put("angle", new OMSymbol("plangeo3", "angle"));
      translate.put("arc", new OMSymbol("plangeo3", "arc"));
      translate.put("are_on_circle", new OMSymbol("plangeo3", "are_on_circle"));
      translate.put("center_of", new OMSymbol("plangeo3", "center_of"));
      translate.put("center_of_gravity", new OMSymbol("plangeo3", "center_of_gravity"));
      translate.put("circle", new OMSymbol("plangeo3", "circle"));
      translate.put("distance", new OMSymbol("plangeo3", "distance"));
      translate.put("is_midpoint", new OMSymbol("plangeo3", "is_midpoint"));
      translate.put("midpoint", new OMSymbol("plangeo3", "midpoint"));
      translate.put("parallel", new OMSymbol("plangeo3", "parallel"));
      translate.put("perpbisector", new OMSymbol("plangeo3", "perpbisector"));
      translate.put("perpendicular", new OMSymbol("plangeo3", "perpendicular"));
      translate.put("perpline", new OMSymbol("plangeo3", "perpline"));
      translate.put("polarline", new OMSymbol("plangeo3", "polarline"));
      translate.put("radius", new OMSymbol("plangeo3", "radius"));
      translate.put("radius_of", new OMSymbol("plangeo3", "radius_of"));
      translate.put("tangent", new OMSymbol("plangeo3", "tangent"));
      translate.put("affine_coordinates", new OMSymbol("plangeo4", "affine_coordinates"));
      translate.put("coordinates", new OMSymbol("plangeo4", "coordinates"));
      translate.put("is_affine", new OMSymbol("plangeo4", "is_affine"));
      translate.put("set_affine_coordinates", new OMSymbol("plangeo4", "set_affine_coordinates"));
      translate.put("set_coordinates", new OMSymbol("plangeo4", "set_coordinates"));
      translate.put("coordinatize", new OMSymbol("plangeo5", "coordinatize"));
      translate.put("is_coordinatized", new OMSymbol("plangeo5", "is_coordinatized"));
      translate.put("polynomial_assertion", new OMSymbol("plangeo5", "polynomial_assertion"));
      translate.put("convert", new OMSymbol("poly", "convert"));
      translate.put("degree_wrt", new OMSymbol("poly", "degree_wrt"));
      translate.put("discriminant", new OMSymbol("poly", "discriminant"));
      translate.put("factored", new OMSymbol("poly", "factored"));
      translate.put("partially_factored", new OMSymbol("poly", "partially_factored"));
      translate.put("resultant", new OMSymbol("poly", "resultant"));
      translate.put("squarefree", new OMSymbol("poly", "squarefree"));
      translate.put("squarefreed", new OMSymbol("poly", "squarefreed"));
      translate.put("ambient_ring", new OMSymbol("polyd1", "ambient_ring"));
      translate.put("variables", new OMSymbol("polyd1", "variables"));
      translate.put("collect", new OMSymbol("polyd3", "collect"));
      translate.put("list_to_poly_d", new OMSymbol("polyd3", "list_to_poly_d"));
      translate.put("poly_d_named_to_arith", new OMSymbol("polyd3", "poly_d_named_to_arith"));
      translate.put("poly_d_to_arith", new OMSymbol("polyd3", "poly_d_to_arith"));
      translate.put("groebner_basis", new OMSymbol("polygb", "groebner_basis"));
      translate.put("extended_in", new OMSymbol("polygb2", "extended_in"));
      translate.put("in_radical", new OMSymbol("polygb2", "in_radical"));
      translate.put("minimal_groebner_element", new OMSymbol("polygb2", "minimal_groebner_element"));
      translate.put("poly_r_rep", new OMSymbol("polyr", "poly_r_rep"));
      translate.put("polynomial_r", new OMSymbol("polyr", "polynomial_r"));
      translate.put("polynomial_ring_r", new OMSymbol("polyr", "polynomial_ring_r"));
      translate.put("const_node", new OMSymbol("polyslp", "const_node"));
      translate.put("depth", new OMSymbol("polyslp", "depth"));
      translate.put("inp_node", new OMSymbol("polyslp", "inp_node"));
      translate.put("left_ref", new OMSymbol("polyslp", "left_ref"));
      translate.put("monte_carlo_eq", new OMSymbol("polyslp", "monte_carlo_eq"));
      translate.put("node_selector", new OMSymbol("polyslp", "node_selector"));
      translate.put("op_node", new OMSymbol("polyslp", "op_node"));
      translate.put("poly_ring_SLP", new OMSymbol("polyslp", "poly_ring_SLP"));
      translate.put("polynomial_SLP", new OMSymbol("polyslp", "polynomial_SLP"));
      translate.put("prog_body", new OMSymbol("polyslp", "prog_body"));
      translate.put("return_node", new OMSymbol("polyslp", "return_node"));
      translate.put("right_ref", new OMSymbol("polyslp", "right_ref"));
      translate.put("slp_degree", new OMSymbol("polyslp", "slp_degree"));
      translate.put("polynomial_ring", new OMSymbol("polysts", "polynomial_ring"));
      translate.put("poly_u_rep", new OMSymbol("polyu", "poly_u_rep"));
      translate.put("polynomial_ring_u", new OMSymbol("polyu", "polynomial_ring_u"));
      translate.put("polynomial_u", new OMSymbol("polyu", "polynomial_u"));
      translate.put("exists", new OMSymbol("quant1", "exists"));
      translate.put("forall", new OMSymbol("quant1", "forall"));
      translate.put("antisymmetric", new OMSymbol("relation0", "antisymmetric"));
      translate.put("equivalence", new OMSymbol("relation0", "equivalence"));
      translate.put("irreflexive", new OMSymbol("relation0", "irreflexive"));
      translate.put("partial_equivalence", new OMSymbol("relation0", "partial_equivalence"));
      translate.put("pre_order", new OMSymbol("relation0", "pre_order"));
      translate.put("reflexive", new OMSymbol("relation0", "reflexive"));
      translate.put("relation", new OMSymbol("relation0", "relation"));
      translate.put("strict_order", new OMSymbol("relation0", "strict_order"));
      translate.put("transitive", new OMSymbol("relation0", "transitive"));
      translate.put("assignment", new OMSymbol("relation1", "assignment"));
      translate.put("block", new OMSymbol("relation1", "block"));
      translate.put("call_arguments", new OMSymbol("relation1", "call_arguments"));
      translate.put("classes", new OMSymbol("relation1", "classes"));
      translate.put("def_arguments", new OMSymbol("relation1", "def_arguments"));
      translate.put("eq", new OMSymbol("relation1", "eq"));
      translate.put("equivalence_closure", new OMSymbol("relation1", "equivalence_closure"));
      translate.put("for", new OMSymbol("relation1", "for"));
      translate.put("function_block", new OMSymbol("relation1", "function_block"));
      translate.put("function_call", new OMSymbol("relation1", "function_call"));
      translate.put("function_definition", new OMSymbol("relation1", "function_definition"));
      translate.put("geq", new OMSymbol("relation1", "geq"));
      translate.put("global_var", new OMSymbol("relation1", "global_var"));
      translate.put("gt", new OMSymbol("relation1", "gt"));
      translate.put("is_equivalence", new OMSymbol("relation1", "is_equivalence"));
      translate.put("is_reflexive", new OMSymbol("relation1", "is_reflexive"));
      translate.put("is_relation", new OMSymbol("relation1", "is_relation"));
      translate.put("is_symmetric", new OMSymbol("relation1", "is_symmetric"));
      translate.put("leq", new OMSymbol("relation1", "leq"));
      translate.put("local_var", new OMSymbol("relation1", "local_var"));
      translate.put("lt", new OMSymbol("relation1", "lt"));
      translate.put("matrix_tensor", new OMSymbol("relation1", "matrix_tensor"));
      translate.put("neq", new OMSymbol("relation1", "neq"));
      translate.put("procedure_block", new OMSymbol("relation1", "procedure_block"));
      translate.put("procedure_call", new OMSymbol("relation1", "procedure_call"));
      translate.put("procedure_definition", new OMSymbol("relation1", "procedure_definition"));
      translate.put("reflexive_closure", new OMSymbol("relation1", "reflexive_closure"));
      translate.put("symmetric_closure", new OMSymbol("relation1", "symmetric_closure"));
      translate.put("transitive_closure", new OMSymbol("relation1", "transitive_closure"));
      translate.put("vector_tensor", new OMSymbol("relation1", "vector_tensor"));
      translate.put("eqs", new OMSymbol("relation4", "eqs"));
      translate.put("is_subring", new OMSymbol("ring1", "is_subring"));
      translate.put("multiplicative_monoid", new OMSymbol("ring1", "multiplicative_monoid"));
      translate.put("negation", new OMSymbol("ring1", "negation"));
      translate.put("quaternions", new OMSymbol("ring1", "quaternions"));
      translate.put("ring", new OMSymbol("ring1", "ring"));
      translate.put("subring", new OMSymbol("ring1", "subring"));
      translate.put("free_ring", new OMSymbol("ring3", "free_ring"));
      translate.put("integers", new OMSymbol("ring3", "integers"));
      translate.put("is_ideal", new OMSymbol("ring3", "is_ideal"));
      translate.put("m_poly_ring", new OMSymbol("ring3", "m_poly_ring"));
      translate.put("matrix_ring", new OMSymbol("ring3", "matrix_ring"));
      translate.put("poly_ring", new OMSymbol("ring3", "poly_ring"));
      translate.put("principal_ideal", new OMSymbol("ring3", "principal_ideal"));
      translate.put("quotient_ring", new OMSymbol("ring3", "quotient_ring"));
      translate.put("is_domain", new OMSymbol("ring4", "is_domain"));
      translate.put("is_field", new OMSymbol("ring4", "is_field"));
      translate.put("is_maximal_ideal", new OMSymbol("ring4", "is_maximal_ideal"));
      translate.put("is_prime_ideal", new OMSymbol("ring4", "is_prime_ideal"));
      translate.put("is_zero_divisor", new OMSymbol("ring4", "is_zero_divisor"));
      translate.put("quotient_by_poly_map", new OMSymbol("ring5", "quotient_by_poly_map"));
      translate.put("quotient_map", new OMSymbol("ring5", "quotient_map"));
      translate.put("ceiling", new OMSymbol("rounding1", "ceiling"));
      translate.put("floor", new OMSymbol("rounding1", "floor"));
      translate.put("round", new OMSymbol("rounding1", "round"));
      translate.put("trunc", new OMSymbol("rounding1", "trunc"));
      translate.put("median", new OMSymbol("s_data1", "median"));
      translate.put("mode", new OMSymbol("s_data1", "mode"));
      translate.put("Semigroup", new OMSymbol("semigroup", "Semigroup"));
      translate.put("associative", new OMSymbol("semigroup", "associative"));
      translate.put("make_Semigroup", new OMSymbol("semigroup", "make_Semigroup"));
      translate.put("factor_of", new OMSymbol("semigroup1", "factor_of"));
      translate.put("is_subsemigroup", new OMSymbol("semigroup1", "is_subsemigroup"));
      translate.put("subsemigroup", new OMSymbol("semigroup1", "subsemigroup"));
      translate.put("cyclic_semigroup", new OMSymbol("semigroup3", "cyclic_semigroup"));
      translate.put("free_semigroup", new OMSymbol("semigroup3", "free_semigroup"));
      translate.put("maps_semigroup", new OMSymbol("semigroup3", "maps_semigroup"));
      translate.put("set", new OMSymbol("set1", "set"));
      translate.put("lift_binary", new OMSymbol("set2", "lift_binary"));
      translate.put("N", new OMSymbol("setname1", "N"));
      translate.put("P", new OMSymbol("setname1", "P"));
      translate.put("big_intersect", new OMSymbol("setname1", "big_intersect"));
      translate.put("big_union", new OMSymbol("setname1", "big_union"));
      translate.put("cartesian_power", new OMSymbol("setname1", "cartesian_power"));
      translate.put("inversion", new OMSymbol("setname1", "inversion"));
      translate.put("k_subsets", new OMSymbol("setname1", "k_subsets"));
      translate.put("map_with_condition", new OMSymbol("setname1", "map_with_condition"));
      translate.put("map_with_target", new OMSymbol("setname1", "map_with_target"));
      translate.put("map_with_target_and_condition", new OMSymbol("setname1", "map_with_target_and_condition"));
      translate.put("powerset", new OMSymbol("setname1", "powerset"));
      translate.put("subgroup", new OMSymbol("setname1", "subgroup"));
      translate.put("A", new OMSymbol("setname2", "A"));
      translate.put("Boolean", new OMSymbol("setname2", "Boolean"));
      translate.put("GFp", new OMSymbol("setname2", "GFp"));
      translate.put("GFpn", new OMSymbol("setname2", "GFpn"));
      translate.put("H", new OMSymbol("setname2", "H"));
      translate.put("QuotientField", new OMSymbol("setname2", "QuotientField"));
      translate.put("Setoid", new OMSymbol("setoid", "Setoid"));
      translate.put("make_Setoid", new OMSymbol("setoid", "make_Setoid"));
      translate.put("NumericalValue", new OMSymbol("sts", "NumericalValue"));
      translate.put("Object", new OMSymbol("sts", "Object"));
      translate.put("SetNumericalValue", new OMSymbol("sts", "SetNumericalValue"));
      translate.put("attribution", new OMSymbol("sts", "attribution"));
      translate.put("binder", new OMSymbol("sts", "binder"));
      translate.put("error", new OMSymbol("sts", "error"));
      translate.put("nary", new OMSymbol("sts", "nary"));
      translate.put("nassoc", new OMSymbol("sts", "nassoc"));
      translate.put("structure", new OMSymbol("sts", "structure"));
      translate.put("cos", new OMSymbol("transc1", "cos"));
      translate.put("cosh", new OMSymbol("transc1", "cosh"));
      translate.put("cot", new OMSymbol("transc1", "cot"));
      translate.put("coth", new OMSymbol("transc1", "coth"));
      translate.put("csc", new OMSymbol("transc1", "csc"));
      translate.put("csch", new OMSymbol("transc1", "csch"));
      translate.put("exp", new OMSymbol("transc1", "exp"));
      translate.put("sec", new OMSymbol("transc1", "sec"));
      translate.put("sech", new OMSymbol("transc1", "sech"));
      translate.put("sin", new OMSymbol("transc1", "sin"));
      translate.put("sinh", new OMSymbol("transc1", "sinh"));
      translate.put("tan", new OMSymbol("transc1", "tan"));
      translate.put("tanh", new OMSymbol("transc1", "tanh"));
      translate.put("unwind", new OMSymbol("transc2", "unwind"));
      translate.put("Prop", new OMSymbol("typesorts", "Prop"));
      translate.put("Type", new OMSymbol("typesorts", "Type"));
      translate.put("Type0", new OMSymbol("typesorts", "Type0"));
      translate.put("acre", new OMSymbol("units_imperial1", "acre"));
      translate.put("bar", new OMSymbol("units_imperial1", "bar"));
      translate.put("degree_Fahrenheit", new OMSymbol("units_imperial1", "degree_Fahrenheit"));
      translate.put("foot", new OMSymbol("units_imperial1", "foot"));
      translate.put("mile", new OMSymbol("units_imperial1", "mile"));
      translate.put("miles_per_hr", new OMSymbol("units_imperial1", "miles_per_hr"));
      translate.put("miles_per_hr_sqrd", new OMSymbol("units_imperial1", "miles_per_hr_sqrd"));
      translate.put("pint", new OMSymbol("units_imperial1", "pint"));
      translate.put("pound_force", new OMSymbol("units_imperial1", "pound_force"));
      translate.put("pound_mass", new OMSymbol("units_imperial1", "pound_mass"));
      translate.put("yard", new OMSymbol("units_imperial1", "yard"));
      translate.put("Coulomb", new OMSymbol("units_metric1", "Coulomb"));
      translate.put("Joule", new OMSymbol("units_metric1", "Joule"));
      translate.put("Newton", new OMSymbol("units_metric1", "Newton"));
      translate.put("Newton_per_sqr_metre", new OMSymbol("units_metric1", "Newton_per_sqr_metre"));
      translate.put("Pascal", new OMSymbol("units_metric1", "Pascal"));
      translate.put("Watt", new OMSymbol("units_metric1", "Watt"));
      translate.put("amp", new OMSymbol("units_metric1", "amp"));
      translate.put("degree_Celsius", new OMSymbol("units_metric1", "degree_Celsius"));
      translate.put("degree_Kelvin", new OMSymbol("units_metric1", "degree_Kelvin"));
      translate.put("gramme", new OMSymbol("units_metric1", "gramme"));
      translate.put("litre", new OMSymbol("units_metric1", "litre"));
      translate.put("litre_pre1964", new OMSymbol("units_metric1", "litre_pre1964"));
      translate.put("metre", new OMSymbol("units_metric1", "metre"));
      translate.put("metre_sqrd", new OMSymbol("units_metric1", "metre_sqrd"));
      translate.put("metres_per_second", new OMSymbol("units_metric1", "metres_per_second"));
      translate.put("metres_per_second_sqrd", new OMSymbol("units_metric1", "metres_per_second_sqrd"));
      translate.put("volt", new OMSymbol("units_metric1", "volt"));
      translate.put("prefix", new OMSymbol("units_ops1", "prefix"));
      translate.put("atto", new OMSymbol("units_siprefix1", "atto"));
      translate.put("centi", new OMSymbol("units_siprefix1", "centi"));
      translate.put("deci", new OMSymbol("units_siprefix1", "deci"));
      translate.put("deka", new OMSymbol("units_siprefix1", "deka"));
      translate.put("exa", new OMSymbol("units_siprefix1", "exa"));
      translate.put("femto", new OMSymbol("units_siprefix1", "femto"));
      translate.put("giga", new OMSymbol("units_siprefix1", "giga"));
      translate.put("hecto", new OMSymbol("units_siprefix1", "hecto"));
      translate.put("kilo", new OMSymbol("units_siprefix1", "kilo"));
      translate.put("mega", new OMSymbol("units_siprefix1", "mega"));
      translate.put("micro", new OMSymbol("units_siprefix1", "micro"));
      translate.put("milli", new OMSymbol("units_siprefix1", "milli"));
      translate.put("nano", new OMSymbol("units_siprefix1", "nano"));
      translate.put("peta", new OMSymbol("units_siprefix1", "peta"));
      translate.put("pico", new OMSymbol("units_siprefix1", "pico"));
      translate.put("tera", new OMSymbol("units_siprefix1", "tera"));
      translate.put("yocto", new OMSymbol("units_siprefix1", "yocto"));
      translate.put("yotta", new OMSymbol("units_siprefix1", "yotta"));
      translate.put("zepto", new OMSymbol("units_siprefix1", "zepto"));
      translate.put("zetta", new OMSymbol("units_siprefix1", "zetta"));
      translate.put("unit_prefix", new OMSymbol("units_sts", "unit_prefix"));
      translate.put("calendar_month", new OMSymbol("units_time1", "calendar_month"));
      translate.put("calendar_year", new OMSymbol("units_time1", "calendar_year"));
      translate.put("day", new OMSymbol("units_time1", "day"));
      translate.put("hour", new OMSymbol("units_time1", "hour"));
      translate.put("minute", new OMSymbol("units_time1", "minute"));
      translate.put("week", new OMSymbol("units_time1", "week"));
      translate.put("acre_us_survey", new OMSymbol("units_us1", "acre_us_survey"));
      translate.put("foot_us_survey", new OMSymbol("units_us1", "foot_us_survey"));
      translate.put("mile_us_survey", new OMSymbol("units_us1", "mile_us_survey"));
      translate.put("pint_us_dry", new OMSymbol("units_us1", "pint_us_dry"));
      translate.put("pint_us_liquid", new OMSymbol("units_us1", "pint_us_liquid"));
      translate.put("yard_us_survey", new OMSymbol("units_us1", "yard_us_survey"));
      translate.put("Laplacian", new OMSymbol("veccalc1", "Laplacian"));
      translate.put("curl", new OMSymbol("veccalc1", "curl"));
      translate.put("divergence", new OMSymbol("veccalc1", "divergence"));
      translate.put("grad", new OMSymbol("veccalc1", "grad"));
    }

	public static void addTranslation(String s, OMSymbol o) {
		translate.put(s, o);
	}

    public static OpenMathBase[] flatten(Object o1, Object o2) {
        OpenMathBase[] result;
        if (o1 instanceof OpenMathBase && o2 instanceof OpenMathBase) {
			result = new OpenMathBase[] { (OpenMathBase) o1, (OpenMathBase) o2 };
            return result;
		}
        if (o1 instanceof OpenMathBase[] && o2 instanceof OpenMathBase) {
            OpenMathBase[] l = (OpenMathBase[]) o1;
            result = new OpenMathBase[l.length+1];
            System.arraycopy(l, 0, result, 0, l.length);
            result[l.length] = (OpenMathBase) o2;
            return result;
        }
        if (o1 instanceof OpenMathBase && o2 instanceof OpenMathBase[]) {
            OpenMathBase[] l = (OpenMathBase[]) o2;
            result = new OpenMathBase[l.length+1];
            result[0] = (OpenMathBase) o1;
            System.arraycopy(l, 0, result, 1, l.length);
            return result;
        }
        if (o1 instanceof OpenMathBase[] && o2 instanceof OpenMathBase[]) {
            OpenMathBase[] l1 = (OpenMathBase[]) o1;
            OpenMathBase[] l2 = (OpenMathBase[]) o2;
            result = new OpenMathBase[l1.length+l2.length];
            System.arraycopy(l1, 0, result, 0, l1.length);
            System.arraycopy(l2, 0, result, l1.length, l2.length);
            return result;
        }
        throw new RuntimeException("Wrong Input Format");
    }

    public static OpenMathBase apply(String cd, String name, Object ... params) {
		return apply(new OMSymbol(cd, name), params);
    }

    public static OpenMathBase apply(OpenMathBase om, Object ... params) {
		//Not sure whether this is OK -- should be careful -- but that is the
		// drawback of these variable length arguments. 
		//Or did something go awry in the flattening-business? Don't really think so..
		if (params.length == 1 && params[0] instanceof OpenMathBase[] ) {
			return om.apply((OpenMathBase[]) params[0]);
		} else if (params.length == 1 && params[0] == null) {
			return om.apply( new OpenMathBase[] { });
		} else {
			OpenMathBase[] paramsom = new OpenMathBase[params.length];
			for(int i = 0; i < params.length; ++i) {
				paramsom[i] = (OpenMathBase) params[i];
			}
        	return om.apply(paramsom);
		}
    }

    public static OpenMathBase error(Object s, Object ... params) {
		if (params.length == 1 && params[0] instanceof OpenMathBase[] ) {
			return ((OpenMathBase) s).error((OpenMathBase[]) params[0]);
		} else if (params.length == 1 && params[0] == null) {
			return ((OpenMathBase) s).error( new OpenMathBase[] { });			
		} else {
			OpenMathBase[] paramsom = new OpenMathBase[params.length];
			for(int i = 0; i < params.length; ++i) {
				paramsom[i] = (OpenMathBase) params[i];
			}
	        return ((OpenMathBase)s).error(paramsom);
		}
    }

    public static OpenMathBase applyFlat(String cd, String name, Object oo1, Object oo2) {
        OpenMathBase o1 = (OpenMathBase) oo1;
        OpenMathBase o2 = (OpenMathBase) oo2;

        if (o1.isApplication(cd,name) && o2.isApplication(cd, name))
            return new OMSymbol(cd, name).apply(flatten(((OMApply) o1).getParams(), ((OMApply) o2).getParams()));
        if (o1.isApplication(cd,name))
            return new OMSymbol(cd, name).apply(flatten(((OMApply) o1).getParams(), o2));
        if (o2.isApplication(cd,name))
            return new OMSymbol(cd, name).apply(flatten(o1, ((OMApply) o2).getParams()));

        return (new OMSymbol(cd, name)).apply(flatten(o1, o2));
    }

    public static OMSymbol id2symbol(String id) {
        OMSymbol s = translate.get(id);
        if (null != s)
            return s;
        throw new RuntimeException("ID not found (" + id + ")");
    }

    public static OpenMathBase setAttributes(Object o, Object a) {
        OpenMathBase om = (OpenMathBase) o;
        OpenMathBase[] at = (OpenMathBase[]) a;
        for (int i = 0; i < at.length; i+=2) {
            om.putAt(at[i], at[i+1]);
        }
        return om;
    }

    public static OpenMathBase bind(Object s, Object v, Object e) {
		OMVariable[] vs;
		if ( v instanceof OMVariable ) {
			vs = new OMVariable[] { (OMVariable) v };
		} else if (v instanceof Object[]) {
			Object[] vo = (Object[]) v;
			vs = new OMVariable[vo.length];
			for(int i = 0; i < vo.length; ++i ) vs[i] = (OMVariable) vo[i];
		} else {
	        throw new RuntimeException("PopcornHelper.bind: Invalid variable array.");			
		}
        return ((OpenMathBase) s).bind(vs, (OpenMathBase) e);
    }

    public static OMForeign foreign(String s) {
        int spl = s.indexOf('<');
        String f, e;
        if (spl > 0) {
            // encoding given
            f = s.substring(spl);
            e = s.substring(0,spl);
        } else {
            // no encoding
            f = s;
            e = null;
        }
        return new OMForeign(f, e);
    }

    public static OpenMathBase setId(Object o, String id) {
        OpenMathBase om = (OpenMathBase) o;
        om.setId(id);
        return om;
    }

    public static OpenMathBase setTypedef(Object o, Object tp) {
        OpenMathBase om = (OpenMathBase) o;
        if (typeSymbol == null) {
            throw new RuntimeException("To use the :: operator first set the PopcornHelper.typeSymbol");
        }
        om.putAt(typeSymbol,(OpenMathBase) tp);
        return om;
    }

	public static OpenMathBase applyUnaryMinus(Object s) {
		OpenMathBase om = (OpenMathBase) s;
		if (om.isInteger()) {
			OMInteger omi = (OMInteger) om;
			//omi.setValue(omi.getIntValue().negate());
			return new OMInteger(omi.getIntValue().negate());
		}
		if (om.isFloat()) {
			return new OMFloat(-((OMFloat)om).getDec());
		}
		return (new OMSymbol("arith1", "unary_minus")).apply(new OpenMathBase[] {om});
	}
	
	public static OpenMathBase string(Object s) {
		//This is needed to map \\ to \ and \" to "
		String t = (String) s;
		t = t.substring(1, t.length()-1);
		t = t.replace("\\\\", "\\");
		t = t.replace("\\\"", "\"");
		return new OMString(t);
	}

        public static String fixId(String id) {
            if (id.charAt(0) == '\'') {
                return id.substring(1, id.length()-1);
            }
            return id;
        }
}
