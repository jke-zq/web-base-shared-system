/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
package com.polycom.edge.webadmin.remote.rest.utils.runtimes;

public interface SystemCommandStatusWchInf
{
   /**
    * Called by a SystemCommand object when its status changes. It may be advisable to execute the contents of
    * this on a separate thread as SystemCommand objects will most likely call this on each registered watcher
    * using a single thread, and any one watcher performing too much on that thread could prevent other
    * watchers from being notified in a timely manner.
    * 
    * @param command SystemCommand that has its status changed
    * @param newStatus
    */
   void commandStatusChanged(SystemCommand command, SystemCommandStatus newStatus);
}
