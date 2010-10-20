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

package org.symcomp.openmath.binary;

/**
 * Created by IntelliJ IDEA.
 * User: hornp
 * Date: Jan 23, 2009
 * Time: 4:23:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BinaryConstants {
    // Flags
    final static int FLAG_STATUS = 32;
    final static int FLAG_ID     = 64;
    final static int FLAG_LONG   = 128;
    // Bit-masks
    final static int TYPE_MASK     = 31;
    final static int MASK_SIGN_POS = '+'; // 0x2b
    final static int MASK_SIGN_NEG = '-'; // 0x2d
    final static int MASK_BASE_10  = 0;
    final static int MASK_BASE_16  = 0x40;
    final static int MASK_BASE_256 = 0x80;
    // Atomic objects
    final static int TYPE_INT_SMALL  =  1;
    final static int TYPE_INT_BIG    =  2;
    final static int TYPE_FLOAT      =  3;
    final static int TYPE_BYTES      =  4;
    final static int TYPE_VARIABLE   =  5;
    final static int TYPE_STRING_ISO =  6;
    final static int TYPE_STRING_UTF =  7;
    final static int TYPE_SYMBOL     =  8;
    final static int TYPE_CDBASE     =  9;
    final static int TYPE_FOREIGN    = 12;
    // Compound objects
    final static int TYPE_APPLICATION      = 16;
    final static int TYPE_APPLICATION_END  = 17;
    final static int TYPE_ATTRIBUTION      = 18;
    final static int TYPE_ATTRIBUTION_END  = 19;
    final static int TYPE_ATTRPAIRS        = 20;
    final static int TYPE_ATTRPAIRS_END    = 21;
    final static int TYPE_ERROR            = 22;
    final static int TYPE_ERROR_END        = 23;
    final static int TYPE_OBJECT           = 24;
    final static int TYPE_OBJECT_END       = 25;
    final static int TYPE_BINDING          = 26;
    final static int TYPE_BINDING_END      = 27;
    final static int TYPE_BVARS            = 28;
    final static int TYPE_BVARS_END        = 29;
    // References
    final static int TYPE_REFERENCE_INT    = 30;
    final static int TYPE_REFERENCE_EXT    = 31;

}
