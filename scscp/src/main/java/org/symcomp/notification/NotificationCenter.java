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

package org.symcomp.notification;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class NotificationCenter {
    private static NotificationCenter center;
    private Map<String, List<NotificationReceiver>> listeners;

    private NotificationCenter() {
        listeners = new HashMap<String, List<NotificationReceiver>>();
        listeners.put("*", new ArrayList<NotificationReceiver>());
    }

    public static NotificationCenter getNC() {
        if (null == center)
            center = new NotificationCenter();
        return center;
    }

    public void sendNotification(Notification n) {
        //println("[NC] preparing to send Notification '${n.getMessage()}'")
        List<NotificationReceiver> receivers = listeners.get(n.getMessage());
        if (null == receivers)
            return;
        for (NotificationReceiver r : receivers) {
            //println("[NC] sending Notification '${n.getMessage()}' to '${r}'")
            NotificationSender.send(r, n);
        }
    }

    public void sendNotification(Object sender, String message, HashMap data) {
        Notification n = new Notification(sender, message, data);
        this.sendNotification(n);
    }

    public void register(NotificationReceiver nr, String message) {
        //println("[NC] registring '${nr}' for message '${message}'")
        if (null == listeners.get(message)) {
            listeners.put(message, new ArrayList<NotificationReceiver>());
        }
        listeners.get(message).add(nr);
    }

    public void register(NotificationReceiver nr) {
        listeners.get("*").add(nr);
    }

}