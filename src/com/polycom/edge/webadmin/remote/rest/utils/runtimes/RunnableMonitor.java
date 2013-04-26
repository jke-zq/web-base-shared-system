package com.polycom.edge.webadmin.remote.rest.utils.runtimes;

/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
/**
 * 
 */


import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.polycom.edge.webadmin.remote.rest.utils.runtimes.FinalizerReplacement.FinalizerCleanerSingleton;

public class RunnableMonitor implements Runnable
{
   private static FinalizerCleanerSingleton<RunnableMonitor, Monitor> cleaner = new FinalizerCleanerSingleton<RunnableMonitor, Monitor>(
         "RunnableMonitor Cleaner");
   private Runnable r;
   private SafeMonitor<RunnableMonitor> qtMon;
   private final String executorName;

   public RunnableMonitor(Runnable r, String executorName)
   {
      this.executorName = executorName;
      this.qtMon = new SafeMonitor<RunnableMonitor>(this, cleaner, MonitorFactory.start(executorName
            + "-QueueTime"));
      this.r = r;
   }

   public void run()
   {
      qtMon.dispose();
      qtMon = null;
      Monitor monitor1 = MonitorFactory.start(r.getClass().getName());
      Monitor monitor2 = MonitorFactory.start(executorName + "-RunTime");
      try
      {
         r.run();
      }
      finally
      {
         r = null;
         monitor1.stop();
         monitor2.stop();
      }
   }
}