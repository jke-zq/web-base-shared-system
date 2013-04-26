package com.polycom.edge.webadmin.remote.rest.utils.runtimes;

/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/


import com.jamonapi.Monitor;

/**
 * A Wrapper for Monitor that will stop the monitor if the Object being
 * referenced gets garbage collected.
 * 
 * @author enylander
 * 
 * @param <T>
 */
public class SafeMonitor<T> extends FinalizerReplacement<T, Monitor>
{

   public SafeMonitor(T referent, FinalizerCleanerSingleton<T, Monitor> mc, Monitor objToDispose)
   {
      super(referent, mc, objToDispose);
   }

   @Override
   public void doDispose(Monitor objToDispose)
   {
      objToDispose.stop();
   }
   
   public void stop()
   {
      dispose();
   }

}
