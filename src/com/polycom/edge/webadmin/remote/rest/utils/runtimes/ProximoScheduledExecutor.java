package com.polycom.edge.webadmin.remote.rest.utils.runtimes;

/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/


import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ProximoScheduledExecutor extends ScheduledThreadPoolExecutor
{
   private String executorName;

   protected ProximoScheduledExecutor( String executorName, int corePoolSize)
   {
      super( corePoolSize);
      this.executorName = executorName;
      init();
   }

   protected ProximoScheduledExecutor( String executorName, int corePoolSize, ThreadFactory threadFactory)
   {
      super( corePoolSize, threadFactory);
      this.executorName = executorName;
      init();
   }

   protected ProximoScheduledExecutor( String executorName, int corePoolSize, RejectedExecutionHandler handler)
   {
      super( corePoolSize, handler);
      this.executorName = executorName;
      init();
   }

   protected ProximoScheduledExecutor( String executorName, int corePoolSize, ThreadFactory threadFactory,
         RejectedExecutionHandler handler)
   {
      super( corePoolSize, threadFactory, handler);
      this.executorName = executorName;
      init();
   }

   private void init()
   {
      scheduleWithFixedDelay( new Runnable()
      {
         public void run()
         {
            purge();
         }
      }, 60, 60, TimeUnit.SECONDS);
   }

   @Override
   public <V> ScheduledFuture<V> schedule( Callable<V> callable, long delay, TimeUnit unit)
   {
      return super.schedule( new CallableMonitor<V>( callable, executorName), delay, unit);
   }

   @Override
   public ScheduledFuture< ? > schedule( Runnable command, long delay, TimeUnit unit)
   {
      return super.schedule( new RunnableMonitor( command, executorName), delay, unit);
   }

   @Override
   public ScheduledFuture< ? > scheduleAtFixedRate( Runnable command, long initialDelay, long period,
         TimeUnit unit)
   {
      return super.scheduleAtFixedRate( new ReRunnableMonitor( command, executorName), initialDelay, period,
            unit);
   }

   @Override
   public ScheduledFuture< ? > scheduleWithFixedDelay( Runnable command, long initialDelay, long delay,
         TimeUnit unit)
   {
      return super.scheduleWithFixedDelay( new ReRunnableMonitor( command, executorName), initialDelay, delay,
            unit);
   }

   @Override
   public <T> Future<T> submit( Runnable task, T result)
   {
      return schedule( new RunnableAdapter<T>( new RunnableMonitor( task, executorName), result), 0,
            TimeUnit.NANOSECONDS);
   }

   // copied from Executors since that one is too private and we need to do an instanceOf so we don't double
   // wrap.
   static final class RunnableAdapter<T> implements Callable<T>
   {
      final Runnable task;
      final T result;

      RunnableAdapter( Runnable task, T result)
      {
         this.task = task;
         this.result = result;
      }

      public T call()
      {
         task.run();
         return result;
      }
   }

}
