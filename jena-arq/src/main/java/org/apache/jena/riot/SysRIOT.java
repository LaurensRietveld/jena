/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.riot;

import org.apache.jena.atlas.lib.IRILib ;
import org.apache.jena.base.Sys ;
import org.apache.jena.riot.system.IRIResolver ;
import org.apache.jena.util.FileUtils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class SysRIOT
{
    public static final String riotLoggerName = "org.apache.jena.riot" ;
    private static Logger riotLogger = LoggerFactory.getLogger(riotLoggerName) ;
    
    public static boolean StrictXSDLexicialForms      = false ;
    public static boolean strictMode                  = false ;
    
    /** Some people argue that absolute URIs should not be normalized.
     * This flag puts IRI resolution in that mode.
     * Bewared: inconisstencies arise - relative URIs are still normalized so
     * where the unnormalized part is in a prefix name changes the outcome.
     * Jena has always normalized abolute URIs.  
     */
    public static boolean AbsURINoNormalization       = false ;
    public static final String BNodeGenIdPrefix       = "genid" ;
    
    /**
     * @deprecated Use Sys.isWindows
     */
    @Deprecated
    public static final boolean isWindows = Sys.isWindows ;
    
    public static void setStrictMode(boolean state) {
        SysRIOT.strictMode = state ;
        SysRIOT.StrictXSDLexicialForms = state ;
        //SysRIOT.AbsURINoNormalization = state ;
    }

    public static boolean isStrictMode() {
        return SysRIOT.strictMode ;
    }

    static public String fmtMessage(String message, long line, long col)
    {
        if ( col == -1 && line == -1 )
                return message ;
        if ( col == -1 && line != -1 )
            return String.format("[line: %d] %s", line, message) ;
        if ( col != -1 && line == -1 )
            return String.format("[col: %d] %s", col, message) ;
        // Mild attempt to keep some alignment
        return String.format("[line: %d, col: %-2d] %s", line, col, message) ;
    }

    public static Logger getLogger()
    {
        return riotLogger ;
    }
    
    public static String chooseBaseIRI()
    {
        return IRIResolver.chooseBaseURI().toString() ;
    }
    
    /** Return a URI suitable for a baseURI, based on some input (which may be null) */
    public static String chooseBaseIRI(String baseURI)
    {
      String scheme = FileUtils.getScheme(baseURI) ;
      // Assume scheme of one letter are Windows drive letters. 
      if ( scheme != null && scheme.length() == 1 ) 
          scheme = "file" ;
      if ( scheme != null && scheme.equals("file") )
          return IRILib.filenameToIRI(baseURI) ;
      return IRIResolver.resolveString(baseURI) ;
    }

    public static String filename2baseIRI(String filename)
    {
        if ( filename == null || filename.equals("-") )
            return "http://localhost/stdin/" ;
        String x = IRILib.filenameToIRI(filename) ;
        return x ;
    }

    /** Choose base IRI, from a given one and a filename.
     *  Prefer the given base ; turn any filename into an IRI.   
     */
    public static String chooseBaseIRI(String baseIRI, String filename)
    {
        if ( baseIRI != null )
            return baseIRI ;
        return filename2baseIRI(filename) ;
    }
}
