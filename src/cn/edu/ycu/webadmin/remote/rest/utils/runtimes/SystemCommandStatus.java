/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
package cn.edu.ycu.webadmin.remote.rest.utils.runtimes;



/**
 * Enumeration of possible status values for a SystemCommand
 */
public enum SystemCommandStatus
{
   IDLE, RUNNING, FAILED_TO_START, ENDED_SUCCESS, ENDED_FAILURE, ENDED_TIMEOUT, ENDED_KILLED;

   /**
    * Determines if the passed in status denotes an "ended" state.
    * 
    * @param status SystemCommandStatus to check
    * @return true if parameter denotes "ended", false if not.
    */
   public static boolean isEnded(SystemCommandStatus status)
   {
      return SystemCommandStatus.FAILED_TO_START.ordinal() <= status.ordinal();
   }
}
