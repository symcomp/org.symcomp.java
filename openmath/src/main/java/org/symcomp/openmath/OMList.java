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

import java.util.*;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
/**
 * This is really ONLY to solve a type probelm with the Mathematica-Java link.
 */
public class OMList {

  LinkedList<OpenMathBase> list;

  public OMList() {
    list = new LinkedList<OpenMathBase>();
  }

  public OMList(Object om) {
    list = new LinkedList<OpenMathBase>();
    list.add((OpenMathBase) om);
  }

  public OMList add(Object om) {
    list.add((OpenMathBase) om);
    return this;
  }

  public OpenMathBase[] array() {
    OpenMathBase[] a = new OpenMathBase[list.size()];
    return list.toArray(a);
  }
}