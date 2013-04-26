/**
 *  COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
 *  STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc.
 */
package com.polycom.edge.webadmin.remote.rest.utils.runtimes;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public abstract class SystemUtil
{
   private static final Logger logger = Logger.getLogger(SystemUtil.class);
   private static final int DEFAULT_TIME_LIMIT = 20;

   /**
    * exec command for snmp params
    * 
    * @param cmd
    * @return String
    */
   public static String execCmd(String... cmd)
   {
      logger.debug("now enter SystemUtil execCmd method! cmd:" + ArrayUtils.toString(cmd));
      StringBuilder sb = new StringBuilder();

      final ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.redirectErrorStream(true);
      final SystemCommand command = new SystemCommand(pb);
      try
      {
         final boolean cmdRet = command.execute(true, 4 * DEFAULT_TIME_LIMIT);
         SystemCommandStatus cmdStatus = command.getStatus();
         logger.debug("cmdRet:" + cmdRet + ", status:" + cmdStatus);
         if (!cmdRet
               || ((SystemCommandStatus.ENDED_SUCCESS != cmdStatus) && (SystemCommandStatus.RUNNING != cmdStatus)))
         {
            logger.error("failed command: " + command);
         }
         else
         {
            BufferedReader in = command.getStdOutReader();
            String str = "";
            if (null != in)
            {
               while ((str = in.readLine()) != null)
               {
                  if (!StringUtils.isBlank(str))
                  {
                     str = str.trim();
                     logger.debug("line str:" + str);
                     sb.append(str).append("\n");
                  }
               }
            }
         }
      }
      catch (IOException e)
      {
         logger.error("failed to run command!");
      }
      finally
      {
         command.destroy();
      }
      logger.debug("now leave SystemUtil execCmd method!");
      return sb.toString();
   }

   public static boolean execKmd(String... cmd)
   {
      logger.debug("now enter SystemUtil execCmd method! cmd:" + ArrayUtils.toString(cmd));
      boolean flag = false;
      final ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.redirectErrorStream(true);
      final SystemCommand command = new SystemCommand(pb);
      try
      {
         final boolean cmdRet = command.execute(true, 4 * DEFAULT_TIME_LIMIT);
         SystemCommandStatus cmdStatus = command.getStatus();
         logger.debug("cmdRet:" + cmdRet + ", status:" + cmdStatus);
         if (!cmdRet
               || ((SystemCommandStatus.ENDED_SUCCESS != cmdStatus) && (SystemCommandStatus.RUNNING != cmdStatus)))
         {
            logger.error("failed command: " + command);
         }
         else
         {
            BufferedReader in = command.getStdOutReader();
            logger.debug("buffered is:" + in);
            if (null != in)
            {
               StringBuilder sb = new StringBuilder();
               String str = "";
               while ((str = in.readLine()) != null)
               {
                  if (!StringUtils.isBlank(str))
                  {
                     str = str.trim();
                     logger.debug("line str:" + str);
                     sb.append(str).append("\n");
                  }
               }
               if (!StringUtils.containsIgnoreCase(sb.toString(), "fail"))
                  flag = true;
            }
         }
      }
      catch (IOException ex)
      {
         logger.error("failed to run command!");
      }
      finally
      {
         command.destroy();
      }
      logger.debug("now leave SystemUtil execCmd method! flag:" + flag);
      return flag;
   }
}
