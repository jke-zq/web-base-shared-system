/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
package cn.edu.ycu.webadmin.remote.rest.utils.runtimes;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;




public class SystemCommandHelper
{
   private static final Logger log = Logger.getLogger(SystemCommandHelper.class);

   private final AtomicInteger asyncCmdId = new AtomicInteger();
   private final Map<Integer, SystemCommand> asyncCmds = new ConcurrentHashMap<Integer, SystemCommand>();
   private final Timer timer = new Timer("Async cmd cleaner");

   public SystemCommandHelper()
   {
      timer.schedule(new TimerTask()
      {
         @Override
         public void run()
         {
            destroyOldCommands();
         }
      }, 4 * 60 * 1000, 60 * 1000);
   }

   public int asyncExecute(SystemCommand command, int timeLimit) throws IOException
   {
      final int id = asyncCmdId.getAndIncrement();
      final boolean ret = command.execute(false, timeLimit);
      if (!ret)
      {
         logFailedCommand(command);
         throw new IOException();
      }
      asyncCmds.put(Integer.valueOf(id), command);
      return id;
   }

   public String getAsyncCommandOutput(int id)
   {
      final SystemCommand command = asyncCmds.get(Integer.valueOf(id));
      if (null == command)
      {
         return null;
      }

      // wait for command only if it does not block
      if (!SystemCommandStatus.isEnded(command.getStatus()) && !command.isRunning())
      {
         try
         {
            command.waitFor();
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
      }
      final boolean done = SystemCommandStatus.isEnded(command.getStatus());
      final BufferedReader reader = command.getStdOutReader();
      final StringBuilder sb = new StringBuilder();
      try
      {
         char[] buf = new char[4096];
         int cnt;
         while (reader.ready() && -1 != (cnt = reader.read(buf)))
         {
            sb.append(buf, 0, cnt);
         }
      }
      catch (IOException e)
      {
         log.error("error reading cmd output", e);
      }
      if (done)
      {
         command.destroy();
         asyncCmds.remove(Integer.valueOf(id));
      }
      return (done && 0 == sb.length()) ? null : sb.toString();
   }

   protected void destroyOldCommands()
   {
      for (int id : asyncCmds.keySet())
      {
         SystemCommand command = asyncCmds.get(Integer.valueOf(id));
         if (SystemCommandStatus.isEnded(command.getStatus()))
         {
            boolean ready = true;
            try
            {
               ready = (null != command.getStdOutReader()) && command.getStdOutReader().ready();
            }
            catch (IOException e)
            {
               log.warn("ready() failed", e);
            }
            if (!ready)
            {
               asyncCmds.remove(Integer.valueOf(id));
               log.debug("cleaning cmd " + id + ": " + command.getCommandToExecute());
               command.destroy();
            }
         }
      }
   }

   public static SystemCommand mkSystemCommand(String cmd, String... args)
   {
      String[] cmd2 = cmd.split(" ");
      String[] new_args = new String[cmd2.length + args.length];
      System.arraycopy(cmd2, 0, new_args, 0, cmd2.length);
      System.arraycopy(args, 0, new_args, cmd2.length, args.length);
      return new SystemCommand(new ProcessBuilder(new_args));
   }

   public static int executeSystemCommand(int timeLimitSeconds, String... cmdArgs) throws IOException
   {
      SystemCommand command = new SystemCommand(new ProcessBuilder(cmdArgs));

      try
      {
         boolean ret = command.execute(true, timeLimitSeconds);

         if (!ret || SystemCommandStatus.ENDED_SUCCESS != command.getStatus())
         {
            logFailedCommand(command);
         }
         if (SystemCommandStatus.ENDED_KILLED == command.getStatus()
               || SystemCommandStatus.ENDED_TIMEOUT == command.getStatus())
         {
            throw new IOException();
         }
         int exitValue = command.getExitValue();
         return exitValue;
      }
      finally
      {
         command.destroy();
      }
   }

    public static void logFailedCommand(SystemCommand cmd) {
        if (cmd != null) {
            log.error("failed command: " + cmd);
            if (cmd.getStdErrReader() != null && cmd.getStdOutReader() != null) {
                log.error("stderr: " + readerToString(cmd.getStdErrReader()));
                log.error("stdout: " + readerToString(cmd.getStdOutReader()));
            }
        }
    }

   public static void appendFromReader(StringBuilder sb, BufferedReader reader, Pattern regExfilter)
   {
      if (null != reader)
      {
         try
         {
            String line;
            while (reader.ready() && null != (line = reader.readLine()))
            {
               Matcher m = null;
               if (null != regExfilter)
               {
                  m = regExfilter.matcher(line);
               }
               if (null == m || !m.find())
               {
                  sb.append(line);
                  sb.append("\n");
               }
            }
         }
         catch (Throwable e)
         {
            log.error("", e);
         }
      }
   }

   public static void appendFromReader(StringBuilder sb, BufferedReader reader)
   {
      appendFromReader(sb, reader, null);
   }

   public static String readerToString(BufferedReader reader, Pattern regExfilter)
   {
      StringBuilder sb = new StringBuilder();
      appendFromReader(sb, reader, regExfilter);
      return sb.toString();
   }

   public static String readerToString(BufferedReader reader)
   {
      return readerToString(reader, null);
   }
}
