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

class NotificationSender implements Runnable{

    NotificationReceiver receiver;
    Notification n;

    private NotificationSender(NotificationReceiver receiver, Notification n) {
        this.receiver = receiver;
        this.n = n;
    }

    static void send(NotificationReceiver receiver, Notification n) {
        NotificationSender ns = new NotificationSender(receiver, n);
        ns.run();
        //Thread runner = new Thread(ns)
        //runner.start()
    }

    public void run() {
        //println("[NC] sending Notification '${n.getMessage()}' to '${this.receiver}'")
        this.receiver.receiveNotification(this.n);
    }


}