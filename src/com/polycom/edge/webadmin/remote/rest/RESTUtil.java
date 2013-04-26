package com.polycom.edge.webadmin.remote.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
public class RESTUtil
{
   public static void logErrorStack(Logger logger, Throwable e)
   {
      if (e.getCause() != null)
      {
         RESTUtil.logErrorStack(logger, e.getCause());
      }
      StringWriter sw = new StringWriter();
      PrintWriter pw = null;
      try
      {
         pw = new PrintWriter(sw);
         e.printStackTrace(pw);
         logger.error(sw.getBuffer().toString());
      }
      finally
      {
         if (pw != null)
            pw.close();
      }
   }

}
