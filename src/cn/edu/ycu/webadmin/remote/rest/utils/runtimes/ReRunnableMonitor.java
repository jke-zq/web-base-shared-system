package cn.edu.ycu.webadmin.remote.rest.utils.runtimes;

/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
/**
 * 
 */

import cn.edu.ycu.webadmin.remote.rest.utils.runtimes.FinalizerReplacement.FinalizerCleanerSingleton;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;


public class ReRunnableMonitor implements Runnable
{
   private static FinalizerCleanerSingleton<ReRunnableMonitor, Monitor> cleaner = new FinalizerCleanerSingleton<ReRunnableMonitor, Monitor>(
         "ReRunnableMonitor Cleaner");
   private final Runnable r;
   private SafeMonitor<ReRunnableMonitor> qtMon;
   private final String runTimeKey;
   private final String classKey;
   private final String queueTimeKey;

   public ReRunnableMonitor(Runnable r, String executorName)
   {
      this.runTimeKey = executorName + "-RunTime";
      queueTimeKey = executorName + "-QueueTime";
      this.r = r;
      classKey = r.getClass().getName();
      qtMon = new SafeMonitor<ReRunnableMonitor>(this, cleaner, MonitorFactory.start(queueTimeKey));
   }

   public void run()
   {
      qtMon.stop();
      qtMon = new SafeMonitor<ReRunnableMonitor>(this, cleaner, MonitorFactory.start(queueTimeKey));

      Monitor monitor1 = MonitorFactory.start(classKey);
      Monitor monitor2 = MonitorFactory.start(runTimeKey);
      try
      {
         r.run();
      }
      finally
      {
         monitor1.stop();
         monitor2.stop();
      }
   }
}