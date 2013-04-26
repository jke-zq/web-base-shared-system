/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
package com.polycom.edge.webadmin.remote.rest.utils.runtimes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * A utility to execute commands in the host OS using either the Runtime.exec or a ProcessBuilder. Users
 * should call the destroy() method when done to ensure prompt cleanup of all held resources. Also wraps a
 * java.lang.Process. This object is intended to facilitate shelling out from the JVM to the base OS to
 * execute some sort of command in a blocking or non-blocking fashion, and provide programatic hooks to that
 * command's input and output (stdin, stdout, stderr). It also contains facilities to time-limit or monitor
 * that command's execution and provide detailed status and feedback to interested parties.
 */
public final class SystemCommand
{
   private static final Logger log = Logger.getLogger(SystemCommand.class);
   private static final int SUCCESSFUL_EXIT_CODE = 0;
   public static final int UNSET_EXIT_VALUE = -2;
   public static final ScheduledExecutorService timer = ExecutorFactory.newScheduledThreadPool(
         "SystemCommands", 20);

   private final String commandToExecute;
   private volatile SystemCommandStatus status;
   private int exitValue = UNSET_EXIT_VALUE;
   private BufferedWriter stdInWriter;
   private BufferedReader stdErrReader;
   private BufferedReader stdOutReader;
   private List<SystemCommandStatusWchInf> statusWatchers;
   private Process process;
   // private SystemCommandMonitor processMonitor;
   private ProcessBuilder processBuilder;
   private ScheduledFuture< ? > timeout;
   private byte[] synObj = new byte[0];

   public SystemCommand(String commandToExecute)
   {
      Validate.notEmpty(commandToExecute);

      this.commandToExecute = commandToExecute;
      init();
   }

   public SystemCommand(String commandToExecute, SystemCommandStatusWchInf watcher)
   {
      this(commandToExecute);

      Validate.notNull(watcher);

      registerStatusWatcher(watcher);
   }

   public SystemCommand(ProcessBuilder processBuilder, SystemCommandStatusWchInf watcher)
   {
      this(processBuilder);
      Validate.notNull(watcher);

      registerStatusWatcher(watcher);
   }

   public SystemCommand(ProcessBuilder processBuilder)
   {
      Validate.notNull(processBuilder);

      init();
      this.processBuilder = processBuilder;

      List<String> pbCmd = processBuilder.command();

      Validate.notEmpty(pbCmd, "a command must be specified to run");
      Validate.notEmpty(pbCmd.get(0), "a command must be specified to run");

      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < pbCmd.size(); i++)
      {
         buf.append(pbCmd.get(i));

         if (i < pbCmd.size() - 1)
         {
            buf.append(" ");
         }
      }

      commandToExecute = buf.toString();
   }

   private void init()
   {
      status = SystemCommandStatus.IDLE;
      statusWatchers = new CopyOnWriteArrayList<SystemCommandStatusWchInf>();
   }

   public String getCommandToExecute()
   {
      return commandToExecute;
   }

   public SystemCommandStatus getStatus()
   {
      return status;
   }

   private void setStatus(SystemCommandStatus newStatus)
   {
      log.debug("now enter SystemCommand setStatus method!");

      /*
       * If we are changing to an ended status and we were ended already we don't have to do anything else.
       */
      boolean isEnded;
      synchronized (synObj)
      {
         if (SystemCommandStatus.isEnded(status) && SystemCommandStatus.isEnded(newStatus))
         {
            status = newStatus;
            return;
         }

         log.debug("change status from:" + status + " to:" + newStatus);
         status = newStatus;

         if (SystemCommandStatus.ENDED_SUCCESS != status && SystemCommandStatus.RUNNING != status)
         {
            log.warn("status changed for " + this);
         }
         else if (log.isDebugEnabled())
         {
            log.debug("status changed for " + this);
         }
         isEnded = SystemCommandStatus.isEnded(status);
         log.debug("isEnded:" + isEnded);
      }

      log.debug("statusWatchers:" + statusWatchers);
      for (SystemCommandStatusWchInf watcher : statusWatchers)
      {
         watcher.commandStatusChanged(this, newStatus);
      }
      log.debug("after watch status changed!isEnded:" + isEnded);

      /*
       * Auto-unregister watches on a status that indicates the Command has ended.
       */
      if (isEnded)
      {
         log.debug("statusWatchers:" + statusWatchers);
         statusWatchers.clear();

         log.debug("timeout:" + timeout);
         if (null != timeout)
         {
            timeout.cancel(false);
            timeout = null;
         }
         log.debug("after cancel timeout");
      }
      log.debug("now leave SystemCommand setStatus method!");
   }

   public int getExitValue()
   {
      return exitValue;
   }

   public boolean isRunning()
   {
      if (null == process)
      {
         return false;
      }

      try
      {
         processTerminated(process.exitValue()); // will set exit code correctly if terminated
         return false;
      }
      catch (IllegalThreadStateException e)
      {
         e.printStackTrace();
      }
      return true;
   }

   public BufferedReader getStdOutReader()
   {
      return stdOutReader;
   }

   public BufferedReader getStdErrReader()
   {
      return stdErrReader;
   }

   public BufferedWriter getStdInWriter()
   {
      return stdInWriter;
   }

   public boolean isAWatcher(SystemCommandStatusWchInf watcher)
   {
      return statusWatchers.contains(watcher);
   }

   public boolean registerStatusWatcher(SystemCommandStatusWchInf watcher)
   {
      Validate.notNull(watcher);
      return statusWatchers.add(watcher);
   }

   public boolean unregisterStatusWatcher(SystemCommandStatusWchInf watcher)
   {
      Validate.notNull(watcher);
      return statusWatchers.remove(watcher);
   }

   public int getWatcherCount()
   {
      return statusWatchers.size();
   }

   /**
    * Should be used only by helper and test classes.
    * 
    * @return
    */
   protected Process getProcess()
   {
      return process;
   }

   protected void processTerminated(int code)
   {
      log.debug("now enter processTerminated method! code:" + code);
      exitValue = code;

      /*
       * Only set the status on the term code if the command was not already in an ended state.
       */
      synchronized (synObj)
      {
         boolean st = SystemCommandStatus.isEnded(status);
         log.debug("st:" + st);
         if (!st)
         {
            if (SUCCESSFUL_EXIT_CODE == exitValue)
            {
               setStatus(SystemCommandStatus.ENDED_SUCCESS);
            }
            else
            {
               setStatus(SystemCommandStatus.ENDED_FAILURE);
            }
         }
      }
      log.debug("now leave processTerminated method!");
   }

   protected void processFailed2Run()
   {
      exitValue = -1;
      setStatus(SystemCommandStatus.FAILED_TO_START);
   }

   /**
    * Execute a command in the OS as using the Runtime. Remember to call destroy() to ensure Process death and
    * resource cleanup after you are finished with stdIn, Out, and Err. If execution has no time limit a
    * monitoring thread will be spawned to "watch" the execution and display periodic log messages to that
    * fact.
    * 
    * @param blockUntilCompletion true to wait on return until command finishes (one way or another), false to
    *           return ASAP.
    * @param timeLimit seconds to allow command to run before timing out, 0 for never. If time limit is
    *           reached then status will reflect that.
    * @return true if execute completed Runtime hand-off of the command, false if there was some error with
    *         the Runtime Process spawn/hand-off or timer/monitor scheduling.
    */
   public synchronized boolean execute(boolean blockUntilCompletion, int timeLimit)
   {
      boolean result = execute(timeLimit, blockUntilCompletion);
      try
      {
         log.debug("result:" + result + ", blockUntilCompletion:" + blockUntilCompletion);
         if (result && blockUntilCompletion)
         {
            waitFor();
         }
         return result;
      }
      catch (InterruptedException e)
      {
         log.error("Wait for " + this + " with timeLimit[" + timeLimit + "] failed", e);
      }
      return false;
   }

   // private synchronized boolean execute(int timeLimit, boolean block)
   private boolean execute(int timeLimit, boolean block)
   {
      setStatus(SystemCommandStatus.RUNNING);

      log.debug("Executing " + this + " with timeLimit[" + timeLimit + "], block[" + block + "]");

      try
      {
         if (null == processBuilder)
         {
            log.debug("exec command");
            process = Runtime.getRuntime().exec(commandToExecute);
         }
         else
         {
            log.debug("start command");
            process = processBuilder.start();
         }
      }
      catch (IOException e)
      {
         log.error(this + " failed with " + e, e);
         processFailed2Run();
         return false;
      }
      /*
       * see the javadoc for Process as this can get confusing: OutputStream == StdIn, ErrStream == StdErr,
       * InputStream == stdOut
       */
      stdInWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
      stdErrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      stdOutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      if (timeLimit > 0)
      {
         timeout = timer.schedule(new Runnable()
         {
            public void run()
            {
               timedOut();
            }
         }, timeLimit, TimeUnit.SECONDS);
      }
      log.debug("after set timer");

      // monitor all non blocking commands
      if (!block)
      {
         timer.submit(new SystemCommandMonitor(this, process));
      }
      log.debug("now leave execute method!");
      return true;
   }

   // block and wait until the process has finished execution
   public void waitFor()
      throws InterruptedException
   {
      processTerminated(process.waitFor());
   }

   protected void timedOut()
   {
      log.info("timeout for " + this + "status:" + getStatus() + ",process:" + process);
      if (getStatus() == SystemCommandStatus.RUNNING)
      {
         log.debug("change status!");
         setStatus(SystemCommandStatus.ENDED_TIMEOUT);
         if (null != process)
         {
            log.debug("before destroy process!");
            process.destroy();
            log.debug("after destroy process!");
         }
      }
      else
      {
         log.error("change status!");
      }
      log.info("leave timeout");
   }

   /**
    * Users of exec() should call this method when all streams are complete to end the process and close down
    * all its resources. Placing this in a finally block is also recommended.
    */
   public void destroy()
   {
      try
      {
         if (null != stdErrReader)
         {
            stdErrReader.close();
            stdErrReader = null;
         }
      }
      catch (IOException e)
      {
         log.error("could not close stderr", e);
      }

      try
      {
         if (null != stdOutReader)
         {
            stdOutReader.close();
            stdOutReader = null;
         }
      }
      catch (IOException e)
      {
         log.error("could not close stdout", e);
      }

      try
      {
         if (null != stdInWriter)
         {
            stdInWriter.close();
            stdInWriter = null;
         }
      }
      catch (IOException e)
      {
         log.error("could not close stdin", e);
      }

      if (isRunning() || status == SystemCommandStatus.IDLE)
      {
         if (null != process)
         {
            process.destroy();
         }
         setStatus(SystemCommandStatus.ENDED_KILLED);
      }
   }

   @Override
   public boolean equals(Object obj)
   {
      if (!(obj instanceof SystemCommand))
      {
         return false;
      }

      SystemCommand rhs = (SystemCommand) obj;

      return new EqualsBuilder().append(commandToExecute, rhs.commandToExecute)
            .append(exitValue, rhs.exitValue).append(status, rhs.status).isEquals();
   }

   @Override
   public int hashCode()
   {
      return new HashCodeBuilder().append(commandToExecute).append(exitValue).append(status).toHashCode();
   }

   @Override
   public String toString()
   {
      StringBuilder ret = new StringBuilder();

      ret.append("cmd[");
      ret.append(commandToExecute);
      if (UNSET_EXIT_VALUE != exitValue)
      {
         ret.append("] exitValue[");
         ret.append(exitValue);
      }
      ret.append("] status[");
      ret.append(status);
      ret.append("]");

      return ret.toString();
   }
}
