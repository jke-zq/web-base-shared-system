/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
package cn.edu.ycu.webadmin.remote.rest.utils.runtimes;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;


/**
 * Monitors a SystemCommand and sets the eventual exit code in the SystemCommand when the Runtime's Process
 * has completed. Also logs the fact the command is still running every minute or so. Is generally used for
 * command's non-blocking, no time limit execute().
 *
 */
public class SystemCommandMonitor implements Runnable
{
   private static final Logger log = Logger.getLogger(SystemCommandMonitor.class);
   private static final int MONITOR_CHECK_TIME = 60;
   protected SystemCommand command;
   private final Process process;

   protected SystemCommandMonitor(SystemCommand cmd, Process proc)
   {
      Validate.notNull(cmd);
      Validate.notNull(proc);
      this.command = cmd;
      this.process = proc;
   }

   public void run()
   {
      try
      {
         final ScheduledFuture<?> longRunningTimer = SystemCommand.timer.scheduleAtFixedRate(new Runnable()
         {
            public void run()
            {
               log.info("still monitoring command:" + command);
            }
         }, MONITOR_CHECK_TIME, MONITOR_CHECK_TIME, TimeUnit.SECONDS);

         command.processTerminated(process.waitFor());
         longRunningTimer.cancel(false);
      }
      catch (final InterruptedException e)
      {
         // Do nothing
         if (log.isTraceEnabled())
         {
            log.trace("Ignored exception " + e.getMessage());
         }
      }

      if (log.isDebugEnabled())
      {
         log.debug("finished monitoring for:" + command);
      }
   }
}
