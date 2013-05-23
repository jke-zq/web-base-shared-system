/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
package cn.edu.ycu.webadmin.remote.rest.utils.runtimes;



public enum SystemOSType
{
   UNKNOWN,
   UNIX_LIKE("linux", "unix", "solaris", "mac os x"),
   WINDOWS("windows", "Win", "WIN");

   private SystemOSType(String... types)
   {
      this.types = types;
   }

   public static String getOsTypeName()
   {
      return System.getProperty("os.name");
   }

   private static SystemOSType osType = null;
   public static SystemOSType determineSystemType()
   {
      if (osType == null)
      {
         String osname = getOsTypeName();
         osname = osname.toLowerCase();

         osType = UNKNOWN;
         for(final SystemOSType systemOSType : values())
         {
            for(final String type : systemOSType.types)
            {
               if(osname.contains(type))
               {
                  osType = systemOSType;
                  break;
               }
            }
         }
      }

      return osType;
   }

   /*
    * method to determine runtime context used in conjunction with OS type to programmatically
    * select mock objects when possible
    */

   private static Boolean isJUnitContext = null;

   public static boolean isJUnitContext()
   {
      if (null == isJUnitContext)
      {
         isJUnitContext = Boolean.FALSE;
         final StackTraceElement[] ste = new Exception().getStackTrace();

         for (final StackTraceElement stackTraceElement : ste)
         {
            if(stackTraceElement.getClassName().contains("junit"))
            {
               isJUnitContext = Boolean.TRUE;
               break;
            }
         }
      }
      return isJUnitContext.booleanValue();
   }

   private final String[] types;
}
