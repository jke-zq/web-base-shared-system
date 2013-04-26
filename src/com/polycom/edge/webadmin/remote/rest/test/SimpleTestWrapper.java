/**
 *  COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
 *  STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc.
 */
package com.polycom.edge.webadmin.remote.rest.test;

public class SimpleTestWrapper
{
   public String TestGet(long id)
   {
      String currentMethod= getSelfString();
      return currentMethod+"\n"+"body: \n"+id+"\n";
   }
   
   
   public String TestPut(long id, String content, String user)
   {
      String currentMethod= getSelfString();
      return currentMethod+"\n"+"body: \n"+id+"\n user is:"+user;
   }
   
   public String TestPost(String content, String user)
   {
      
      String currentMethod= getSelfString();
      return currentMethod+"\n"+"body: \n"+content+"\n user is:"+user;
   }
   
   
   public String TestDelete(long id, String user)
   {
      String currentMethod= getSelfString();
      return currentMethod+"\n"+"You have posted: \n"+id+"\n user is:"+user;
   }
   
   private String getSelfString(){
      StackTraceElement[] trace = Thread.currentThread().getStackTrace();
      String methodName = trace[2].getMethodName();
      String className = this.getClass().getSimpleName();
      return "This is the " + methodName + " method in " + className;
   }
}
