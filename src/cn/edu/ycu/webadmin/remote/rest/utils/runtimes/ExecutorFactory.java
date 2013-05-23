/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/
package cn.edu.ycu.webadmin.remote.rest.utils.runtimes;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;



/**
 * Functionality that should be in java.util.concurrent.Executors (but is not).
 * 
 * @author dlange
 * 
 */
public class ExecutorFactory
{
   public static final class ProxThreadFactory implements ThreadFactory
   {
      private final boolean daemonThread;
      private final String name;
      private int priority = Thread.NORM_PRIORITY;

      public ProxThreadFactory( boolean daemonThread, String name)
      {
         this.daemonThread = daemonThread;
         this.name = name;
      }

      public void setThreadPriority( int threadPriority)
      {
         priority = threadPriority;
      }
      
      public Thread newThread( Runnable r)
      {
         Thread thread = createThread(r, name, daemonThread);
         if (priority != Thread.NORM_PRIORITY)
         {
            thread.setPriority(priority);
         }
         return thread;
      }
      
      public String getName()
      {
         return name;
      }
      
      public static Thread createThread( Runnable r, String name, boolean daemonThread)
      {
         Thread thread;
         if (name != null)
         {
            thread = new Thread( r, name);
         }
         else
         {
            thread = new Thread( r);
         }
         thread.setDaemon( daemonThread);
         return thread;
      }
   }
   
   public static final class IncrementingNameThreadFactory implements ThreadFactory
   {
      private int incrementor = 0;
      private final String baseName;
      private boolean daemonThread = false;
      private int priority = Thread.NORM_PRIORITY;
      
      public IncrementingNameThreadFactory( String name)
      {
         this.baseName = name + ":";
      }
      
      public IncrementingNameThreadFactory( String name, boolean daemonThread)
      {
         this(name);
         this.daemonThread = daemonThread;
      }
      
      public void setThreadPriority( int threadPriority)
      {
         priority = threadPriority;
      }
      
      public Thread newThread( Runnable r)
      {
         Thread thread = ProxThreadFactory.createThread(r, baseName + ++incrementor, daemonThread);
         thread.setPriority(priority);
         return thread;
      }
      
      public String getName()
      {
         return baseName.substring(0, baseName.length()-1);
      }
   }

   private ExecutorFactory()
   {
   }

   /**
    * Returns a single threaded executor service. The thread will have the specified name.
    * 
    * @param name The name of the thread.
    * @param daemonThread Indicates whether the thread should be a daemon thread.
    */
   public static ExecutorService newNamedSingleThreadExecutor( final String name, final boolean daemonThread)
   {
      ProxThreadFactory threadFactory = new ProxThreadFactory( daemonThread, name);
      return newSingleThreadExecutor( threadFactory);
   }

   public static ExecutorService newSingleThreadExecutor( final String name)
   {
      return new ProximoExecutor( name, 1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            new ProxThreadFactory( false, name));
   }

   public static ExecutorService newSingleThreadExecutor( ProxThreadFactory threadFactory)
   {
      return new ProximoExecutor( threadFactory.getName(), 1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            threadFactory);
   }

   public static ScheduledExecutorService newSingleThreadScheduledExecutor( final String name)
   {
      return new ProximoScheduledExecutor( name, 1, new ProxThreadFactory( false, name));
   }

   public static ScheduledExecutorService newSingleThreadScheduledExecutor( ProxThreadFactory threadFactory)
   {
      return new ProximoScheduledExecutor( threadFactory.getName(), 1, threadFactory);
   }

   public static ExecutorService newCachedThreadPool( IncrementingNameThreadFactory factory)
   {
      return new ProximoExecutor( factory.getName(), 0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), factory);
   }

   public static ExecutorService newCachedThreadPool( final String name, final boolean daemonThread)
   {
      return newCachedThreadPool( new IncrementingNameThreadFactory( name, daemonThread));
   }

   public static ExecutorService newCachedThreadPool( final String name)
   {
      return newCachedThreadPool(name, false);
   }

   public static ScheduledExecutorService newScheduledThreadPool( final String name, int corePoolSize)
   {
      return new ProximoScheduledExecutor( name, corePoolSize, new IncrementingNameThreadFactory(name));
   }

   public static ExecutorService newFixedThreadPool( final String name, int nThreads)
   {
      return newFixedThreadPool( new IncrementingNameThreadFactory(name), nThreads);
   }

   public static ExecutorService newFixedThreadPool( final IncrementingNameThreadFactory factory, int nThreads)
   {
      return new ProximoExecutor( factory.getName(), nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), factory);
   }
}
