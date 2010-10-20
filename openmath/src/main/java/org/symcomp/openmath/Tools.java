/*
 *  Copyright 2009 hornp.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.symcomp.openmath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author hornp
 */
public class Tools {

    public static HashSet<String> keywords = new HashSet<String>(Arrays.asList("abstract", "continue", "for", "new",
        "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this",
        "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws",
        "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try",
        "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile",
        "const", "float", "native", "super", "while", "true", "false"));

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     * @param pckgname the package name to search
     * @return a list of classes that exist within that package
     * @throws ClassNotFoundException if something went wrong
     */
    public static List<Class> getClassesForPackage(String pckgname) throws ClassNotFoundException {
        // This will hold a list of directories matching the pckgname. There may be more than one if a package is split over multiple jars/paths
        ArrayList<File> directories = new ArrayList<File>();
        ArrayList<Class> classes = new ArrayList<Class>();
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = pckgname.replace('.', '/');
            // Ask for all resources for the path
            Enumeration<URL> resources = cld.getResources(path);
            while (resources.hasMoreElements()) {
                String url = URLDecoder.decode(resources.nextElement().getPath(), "UTF-8");
                if (url.startsWith("file:") && url.contains(".jar!")) {
                    String jarname = url.substring(5, url.indexOf(".jar")+4);
                    //System.out.println("+++++++++ JAR: "+jarname);
                    classes.addAll(getClassesForPackage(jarname, pckgname));
                } else if (url.startsWith("file:") && url.contains(".war!")) {
                    String jarname = url.substring(5, url.indexOf(".war")+4);
                    //System.out.println("+++++++++ WAR: "+jarname);
                    classes.addAll(getClassesForPackage(jarname, pckgname));
                } else
                    directories.add(new File(url));
            }
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
        } catch (UnsupportedEncodingException encex) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
        } catch (IOException ioex) {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
        }

        // For every directory identified capture all the .class files
        for (File directory : directories) {
            if (directory.exists()) {
                // Get the list of the files contained in the package
                String[] files = directory.list();
                for (String file : files) {
                    // we are only interested in .class files
                    if (file.endsWith(".class")) {
                        // removes the .class extension
                        classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
                    }
                }
            } else {
                throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
            }
        }
        return classes;
    }

        /**
     * Attempts to list all the classes in the specified package that are inside the
     * given JAR file
     * @param jarName name of the JAR file
     * @param pckgname the package name to search
     * @return a list of classes that exist within that package inside the jar
     * @throws ClassNotFoundException if something went wrong
     */
    public static List<Class> getClassesForPackage(String jarName, String pckgname) throws ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<Class>();
        pckgname = pckgname.replaceAll("\\.", "/");
        try {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
            JarEntry jarEntry;

            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) break;
                String nam = jarEntry.getName();
                if ((nam.startsWith(pckgname)) && (nam.endsWith(".class"))) {
                    nam = nam.replaceAll("/", "\\.");
                    nam = nam.substring(0, nam.length()-6);
                    classes.add(Class.forName(nam));
                }
            }
        }
        catch (Exception e) {
            throw new ClassNotFoundException("Couldn't read file '"+jarName+"' for some reason.");
        }
        return classes;
    }

    

}
