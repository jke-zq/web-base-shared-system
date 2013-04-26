/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
package com.polycom.edge.webadmin.remote.rest.utils.runtimes;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProximoExecutor extends ThreadPoolExecutor
{
   private String executorName;
   
   protected ProximoExecutor( String executorName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
         BlockingQueue<Runnable> workQueue)
   {
      super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
      this.executorName = executorName;
   }

   protected ProximoExecutor( String executorName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
         BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory)
   {
      super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
      this.executorName = executorName;
   }

   protected ProximoExecutor( String executorName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
         BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler)
   {
      super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
      this.executorName = executorName;
   }

   protected ProximoExecutor( String executorName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
         BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler)
   {
      super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
      this.executorName = executorName;
   }

   @Override
   public void execute( final Runnable r)
   {
      Runnable newR;
      if (r instanceof Future< ? >)
      {
         newR = r;
      }
      else
      {
         newR = new RunnableMonitor( r, executorName);
      }
      super.execute( newR);
   }
   
   @Override
   public <T> Future<T> submit( final Callable<T> task)
   {
      return super.submit( new CallableMonitor<T>( task, executorName));
   }
   
   @Override
   public Future< ? > submit( Runnable task)
   {
      return super.submit( new RunnableMonitor( task, executorName));
   }

   @Override
   public <T> Future<T> submit( Runnable task, T result)
   {
      return super.submit( new RunnableMonitor( task, executorName), result);
   }
}
