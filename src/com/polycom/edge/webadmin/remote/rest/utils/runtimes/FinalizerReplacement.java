package com.polycom.edge.webadmin.remote.rest.utils.runtimes;

/** 
*   COPYRIGHT: (C) Polycom, Inc. 2010-2012. All Rights Reserved.
*   STATEMENTS: No portion of this work may be copied for any purpose without the prior written permission of Polycom, Inc. 
*/

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Logger;

/**
 * A framework for objects to do cleanup at the time of garbage collection
 * without using finalizers.
 * 
 * @author enylander
 * 
 * @param <S>
 */
public abstract class FinalizerReplacement<S,T> extends WeakReference<S> implements
      Comparable<FinalizerReplacement<S,T>>
{
   private static Logger log = Logger.getLogger(FinalizerReplacement.class);
   private AtomicReference<T> atomicRef;
   private static AtomicInteger SerialGenerator = new AtomicInteger();
   private final FinalizerCleanerSingleton<S,T> mc;
   private int serialNumber;

   public static class FinalizerCleanerSingleton<U,V> extends Thread
   {
      ReferenceQueue<U> rq = new ReferenceQueue<U>();
      ConcurrentSkipListSet<FinalizerReplacement<U,V>> activeObjects = new ConcurrentSkipListSet<FinalizerReplacement<U,V>>();

      public FinalizerCleanerSingleton(String name)
      {
         super("FinalizerCleanerSingleton-" + name);
         setDaemon(true);
         start();
      }

      @Override
      public void run()
      {
         while (true)
         {
            try
            {
               while (true)
               {
                  @SuppressWarnings("unchecked")
                  FinalizerReplacement<U,V> deletedRef = (FinalizerReplacement<U,V>) rq.remove();
                  deletedRef.dispose();
               }
            }
            catch (Throwable t)
            {
               log.error("Unexpected Exception", t);
            }
         }
      }
   }

   public FinalizerReplacement(S referent, FinalizerCleanerSingleton<S,T> mc, T objToDispose)
   {
      super(referent, mc.rq);
      this.mc = mc;
      this.atomicRef = new AtomicReference<T>(objToDispose);
      serialNumber = SerialGenerator.incrementAndGet();
      while (mc.activeObjects.contains(this))
      {
         serialNumber = SerialGenerator.incrementAndGet();
      }
      mc.activeObjects.add(this);
   }

   public abstract void doDispose(T objToDispose);
   
   final void dispose()
   {
      T objToDispose = atomicRef.getAndSet(null);
      if (null != objToDispose)
      {
         mc.activeObjects.remove(this);
         doDispose(objToDispose);
      }
   }

   public int compareTo(FinalizerReplacement<S,T> o)
   {
      return serialNumber - o.serialNumber;
   }

}
