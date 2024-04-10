 package com.chieffu.pocker.util;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
import java.util.concurrent.*;





 public class ThreadUtil {
   static Map<String, ThreadPool> pools = new ConcurrentHashMap<>();
   private static final ThreadLocal session = new ThreadLocal();
 
 
 
 
 
   
   public static void pause(long min, long max) {
     if (min > max || max <= 0L)
       return; 
     try {
       Thread.sleep(StringUtils.newRandomLong(min, max));
     } catch (InterruptedException interruptedException) {}
   }
   
   public static void set(Object value) {
     session.set(value);
   }
   
   public static Object get() {
     return session.get();
   }
   public static ThreadPool getThreadPool(String name, int threadCount) {
     if (pools.containsKey(name)) {
       ThreadPool pool = pools.get(name);
       if (!pool.isShutdown() && !pool.isTerminating()) {
         return pool;
       }
     } 
     ThreadPool newPool = new ThreadPool(threadCount);
     pools.put(name, newPool);
     return newPool;
   }
   
   public static ThreadPool getDefaultPool() {
     return getThreadPool("DEFAULT", 50);
   }
   
   public static <T> List<T> batchOperate(ExecutorService pool, List<Callable<T>> callables) {
     List<T> results = new ArrayList<>();
     if (callables == null || callables.size() == 0) {
       return results;
     }
     List<Future<T>> futures = new ArrayList<>();
     for (Callable<T> c : callables) {
       futures.add(pool.submit(c));
     }
     for (Future<T> f : futures) {
       try {
         T r = f.get();
         if (r != null)
           results.add(r); 
       } catch (InterruptedException e) {
         e.printStackTrace();
       } catch (ExecutionException e) {
         e.printStackTrace();
       } 
     } 
     return results;
   }
 }


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\ThreadUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */