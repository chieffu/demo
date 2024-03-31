 package com.chieffu.pocker.util;
 
 import java.util.concurrent.ScheduledThreadPoolExecutor;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.concurrent.locks.Condition;
 import java.util.concurrent.locks.ReentrantLock;
 
 public class ThreadPool
   extends ScheduledThreadPoolExecutor {
   static AtomicInteger poolNumber = new AtomicInteger();
   static AtomicInteger threadNumber = new AtomicInteger(1);
   private boolean pause = false;
   private ReentrantLock pauseLock = new ReentrantLock();
   private Condition unpaused = this.pauseLock.newCondition();
 
   
   public ThreadPool(int coreThread) {
     super(coreThread, new ThreadFactory()
         {
           public Thread newThread(Runnable r) {
             SecurityManager s = System.getSecurityManager();
             
             ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
             String name = String.format("p%02d-t%03d", new Object[] { Integer.valueOf(ThreadPool.poolNumber.get()), Integer.valueOf(ThreadPool.threadNumber.getAndIncrement()) });
             
             Thread t = new Thread(group, r, name, 0L);
             if (t.isDaemon()) {
               t.setDaemon(false);
             }
             if (t.getPriority() != 5) {
               t.setPriority(5);
             }
             return t;
           }
         });
     poolNumber.getAndIncrement();
   }
   
   public void pause() {
     this.pauseLock.lock();
     try {
       this.pause = true;
     } finally {
       this.pauseLock.unlock();
     } 
   }
   
   public void resume() {
     this.pauseLock.lock();
     try {
       this.pause = false;
       this.unpaused.signalAll();
     } finally {
       this.pauseLock.unlock();
     } 
   }
   
   protected void beforeExecute(Thread thread, Runnable runnable) {
     this.pauseLock.lock();
     try {
       for (; this.pause; this.unpaused.await());
     } catch (InterruptedException interruptedException) {
     
     } finally {
       this.pauseLock.unlock();
     } 
     super.beforeExecute(thread, runnable);
   }
 }


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\ThreadPool.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */