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

package org.symcomp.omconvert;

import org.symcomp.openmath.OpenMathBase;
import org.symcomp.openmath.binary.*;

import java.io.*;
import java.nio.charset.Charset;

public class OMConvert {

    public static void main(String[] args) {
        if(args.length != 2 || (!args[0].equals("XML") && !args[0].equals("LATEX") && !args[0].equals("BIN") & !args[0].equals("CBIN") && !args[0].equals("POP"))) {
            System.out.println("Will read infile and export to given format.\nusage: <XML|LATEX|BIN|CBIN|POP> infile");
            System.exit(1);
        }
        String fname = args[1];
        Reader in = null;
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(fname));
            in = new InputStreamReader(dis, Charset.forName("UTF-8"));
        } catch (FileNotFoundException e) {
            System.out.println("Error: Infile '"+fname+"' not found");
            System.exit(1);
        }
        System.out.println("Reading file: "+fname);
        OpenMathBase omo = null;
        long ms = System.currentTimeMillis();
        try {
            omo = OpenMathBase.parse(in);
        } catch (Exception e) {
            System.out.println("Error: Parsing '"+fname+"' went wrong: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Parsing took "+(System.currentTimeMillis()-ms)+"ms");

        Writer out = null;
        String outfile = fname+"."+args[0];
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(outfile));
            out = new OutputStreamWriter(dos, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.out.println("Error: Can't write to '"+outfile+"'");
            System.exit(1);
        }

        System.out.println("Writing to file: '"+outfile+"'");

        ms = System.currentTimeMillis();
        try {
            if (args[0].equals("XML"))
                omo.toXml(out);
            else if (args[0].equals("LATEX"))
                omo.toLatex(out);
            else if (args[0].equals("BIN"))
                omo.toBinary(out);
            else if (args[0].equals("POP"))
                omo.toPopcorn(out);
            else
                BinaryRenderer.render(omo, true);
            out.close();
        } catch (Exception e){
            System.out.println("ERROR: Rendering went wrong!");
            System.exit(1);
        }
        System.out.println("Rendering took "+(System.currentTimeMillis()-ms)+"ms");

        System.out.println("Done.");
    }


}
