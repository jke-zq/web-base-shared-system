
/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
/**
 * 
 */
package com.polycom.edge.webadmin.remote.rest.utils.runtimes;


import java.util.concurrent.Callable;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.polycom.edge.webadmin.remote.rest.utils.runtimes.FinalizerReplacement.FinalizerCleanerSingleton;

public class CallableMonitor<T> implements Callable<T>
{
   private static FinalizerCleanerSingleton<CallableMonitor<?>, Monitor> cleaner = new FinalizerCleanerSingleton<CallableMonitor<?>, Monitor>(
         "CallableMonitor Cleaner");
   private Callable<T> task;
   private SafeMonitor<CallableMonitor<?>> qtMon;
   private final String executorName;

   public CallableMonitor(Callable<T> task, String executorName)
   {
      this.executorName = executorName;
      this.qtMon = new SafeMonitor<CallableMonitor<?>>(this, cleaner, MonitorFactory.start(executorName
            + "-QueueTime"));
      this.task = task;
   }

   public T call() throws Exception
   {
      qtMon.stop();
      qtMon = null;
      Monitor monitor1 = MonitorFactory.start(task.getClass().getName());
      Monitor monitor2 = MonitorFactory.start(executorName + "-RunTime");
      try
      {
         return task.call();
      }
      finally
      {
         task = null;
         monitor1.stop();
         monitor2.stop();
      }
   }
}