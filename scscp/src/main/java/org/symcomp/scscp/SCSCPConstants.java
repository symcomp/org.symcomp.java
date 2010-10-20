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

package org.symcomp.scscp;

/**
 * An interface that contains the constants necessary for SCSCP-communication.
 *
 * <p>Used to offer the implementing classes <code>C3PO</code> and <code>ScscspClient</code>
 * some constants. There are neither methods nor any publicity interesting constants defined 
 * in here. 
 */
public interface SCSCPConstants {

	final static String PI_SCSCP_LEFT    = "<?scscp ";
	final static String PI_SCSCP_RIGHT   = " ?>";
	final static String PI_SCSCP_HELLO   = PI_SCSCP_LEFT + "service_name=\"%s\" service_version=\"%s\" service_id=\"%s\" scscp_versions=\"%s\"" + PI_SCSCP_RIGHT;
	final static String PI_SCSCP_HELOPATT = "\\<\\?scscp service_name=\"([^\"]+)\" service_version=\"([^\"]+)\" service_id=\"([^\"]+)\" scscp_versions=\"([^\"]+)\" \\?\\>";
	final static String PI_SCSCP_VERSION = PI_SCSCP_LEFT + "version=\"%s\"" + PI_SCSCP_RIGHT;
	final static String PI_SCSCP_START   = PI_SCSCP_LEFT + "start" + PI_SCSCP_RIGHT;
	final static String PI_SCSCP_VERPATT = "\\<\\?scscp version=\"([^\"]+)\" \\?\\>";
	final static String PI_SCSCP_END     = PI_SCSCP_LEFT + "end" + PI_SCSCP_RIGHT;
	final static String PI_SCSCP_CANCEL  = PI_SCSCP_LEFT + "cancel" + PI_SCSCP_RIGHT; 
	final static String PI_SCSCP_QUIT    = PI_SCSCP_LEFT + "quit" + PI_SCSCP_RIGHT;
	final static String PI_SCSCP_KILL    = PI_SCSCP_LEFT + "kill" + PI_SCSCP_RIGHT;
	final static String PI_SCSCP_ACK     = PI_SCSCP_LEFT + "ack" + PI_SCSCP_RIGHT;
	final static String PI_SCSCP_INFO    = PI_SCSCP_LEFT + "info=\"%s\"" + PI_SCSCP_RIGHT;
	
	final static String PI_SCSCP_INFOPATT= "\\<\\?scscp info=\"([^\"]+)\" \\?\\>";
	final static String PI_SCSCP_TERM    = PI_SCSCP_LEFT + "terminate call_id=\"%s\"" + PI_SCSCP_RIGHT;
	final static String PI_SCSCP_TERMPATT= "\\<\\?scscp terminate call_id=\"([^\"]+)\" \\?\\>";

	final static String PI_SCSCP_ENCODINGS_SUPPORTED = PI_SCSCP_LEFT + "encodings supported=\"%s\"" + PI_SCSCP_RIGHT;
	final static String PI_SCSCP_ENCODINGS_SUPPORTED_PATT = "\\<\\?scscp encodings supported=\"([^\"]+)\" \\?\\>";
	
	final static Integer STATE_UNITIALIZED   = 0;
	final static Integer STATE_NEGOTIATING   = 1;
	final static Integer STATE_WAITING       = 2;
	final static Integer STATE_INMESSAGE     = 3;
	final static Integer STATE_MSG_COMPLETED = 4;
	final static Integer STATE_COMPUTING     = 5;
    final static Integer STATE_FINISHED      = 99;

    public final static Integer CLIENT_IDLE = 0;
    public final static Integer CLIENT_COMPUTING = 1;
    public final static Integer CLIENT_DEAD = 3;
    public final static Integer CLIENT_QUIT = 2;
    public final static Integer CLIENT_UNINITIALIZED = -1;

}
