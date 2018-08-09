```
/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent;
import java.util.List;
import java.util.Collection;

/**
 * An {@link Executor} that provides methods to manage termination and
 * methods that can produce a {@link Future} for tracking progress of
 * one or more asynchronous tasks.
 ```
  这个接口提供一些管理任务中止的方法，另外还提供一些具有Future 返回对象的方法，这些方法跟踪一个或多们异步任务的执行行过程。
 ```
 *
 * <p>An {@code ExecutorService} can be shut down, which will cause
 * it to reject new tasks.  Two different methods are provided for
 * shutting down an {@code ExecutorService}. The {@link #shutdown}
 * method will allow previously submitted tasks to execute before
 * terminating, while the {@link #shutdownNow} method prevents waiting
 * tasks from starting and attempts to stop currently executing tasks.
 * Upon termination, an executor has no tasks actively executing, no
 * tasks awaiting execution, and no new tasks can be submitted.  An
 * unused {@code ExecutorService} should be shut down to allow
 * reclamation of its resources.
 ```
  ExecutorService能够被shutdown(关闭),这个方法会导致新任务被拒绝加入，关闭有两个不同的方法，
  分别是shutdown 和shutdownNow。
  shutdown方法允许在中止之前提供的任务继续执行，而shutdownNow方法阻止等待中的任务，并试图阻止正在执行的任务,
  当一个executor要中止的时侯，没有活动的任务执行，没有任务等待执行，没有新的任务被提交，一个未使用的ExecutorService 将被shutdown,以允许资源被回收。
 
 ```
 * <p>Method {@code submit} extends base method {@link
 * Executor#execute(Runnable)} by creating and returning a {@link Future}
 * that can be used to cancel execution and/or wait for completion.
 * Methods {@code invokeAny} and {@code invokeAll} perform the most
 * commonly useful forms of bulk execution, executing a collection of
 * tasks and then waiting for at least one, or all, to
 * complete. (Class {@link ExecutorCompletionService} can be used to
 * write customized variants of these methods.)
 ```
   submit 方法基于execute 方法的扩展，通过创建并返回一个Future 对象,
  这个Future对象通常可以取消执行或等待直到任务完成。
  invokeAny和invokeAll执行最普便有用的批量处理形式，执行一批任务然后等待一个或多个任务执行结束。
 　ExecutorCompletionService通常用于写这些方法的自定义变量。
 ```
 * <p>The {@link Executors} class provides factory methods for the
 * executor services provided in this package.
 ```
  在这个包下 Executors 这个类提供工厂方法为了提供executor 服务
  ```
 * <h3>Usage Examples</h3>
 *
 * Here is a sketch of a network service in which threads in a thread
 * pool service incoming requests. It uses the preconfigured {@link
 * Executors#newFixedThreadPool} factory method:
 ```
 　下边这段代码是一个网络服务的大概描述,在这个服务里的线程中的所有线程为传送的请示提供服务，它就用了Executors#newFixedThreadPool的工厂方法
 ```
 *
 *  <pre> {@code
 * class NetworkService implements Runnable {
 *   private final ServerSocket serverSocket;
 *   private final ExecutorService pool;
 *
 *   public NetworkService(int port, int poolSize)
 *       throws IOException {
 *     serverSocket = new ServerSocket(port);
 *     pool = Executors.newFixedThreadPool(poolSize);
 *   }
 *
 *   public void run() { // run the service
 *     try {
 *       for (;;) {
 *         pool.execute(new Handler(serverSocket.accept()));
 *       }
 *     } catch (IOException ex) {
 *       pool.shutdown();
 *     }
 *   }
 * }
 *
 * class Handler implements Runnable {
 *   private final Socket socket;
 *   Handler(Socket socket) { this.socket = socket; }
 *   public void run() {
 *     // read and service request on socket
 *   }
 * }}</pre>
 *
 * The following method shuts down an {@code ExecutorService} in two phases,
 * first by calling {@code shutdown} to reject incoming tasks, and then
 * calling {@code shutdownNow}, if necessary, to cancel any lingering tasks:
 ```
  下面的方法分两个阶段关闭ExecutorService,首先通过调用shutdown方法先拒绝所有任务的提交，然后调用shutdownNow方法，如果有必须，则取消任何一个徘徊的任务。
 ```
 *  <pre> {@code
 * void shutdownAndAwaitTermination(ExecutorService pool) {
 *   pool.shutdown(); // Disable new tasks from being submitted
 *   try {
 *     // Wait a while for existing tasks to terminate
 *     if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
 *       pool.shutdownNow(); // Cancel currently executing tasks
 *       // Wait a while for tasks to respond to being cancelled
 *       if (!pool.awaitTermination(60, TimeUnit.SECONDS))
 *           System.err.println("Pool did not terminate");
 *     }
 *   } catch (InterruptedException ie) {
 *     // (Re-)Cancel if current thread also interrupted
 *     pool.shutdownNow();
 *     // Preserve interrupt status
 *     Thread.currentThread().interrupt();
 *   }
 * }}</pre>
 *
 * <p>Memory consistency effects: Actions in a thread prior to the
 * submission of a {@code Runnable} or {@code Callable} task to an
 * {@code ExecutorService}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * any actions taken by that task, which in turn <i>happen-before</i> the
 * result is retrieved via {@code Future.get()}.
 *　　略
 * @since 1.5
 * @author Doug Lea
 */
public interface ExecutorService extends Executor {

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
```
      启动一个有秩序的shutdown,在这种场景下之前提供的任务都被执行，但没有新的任务被接受。
      如果已经关闭，再次调用没有其他影响。
```
     * <p>This method does not wait for previously submitted tasks to
     * complete execution.  Use {@link #awaitTermination awaitTermination}
     * to do that.
```
      这个方法不会等待已经提交的任务被执行完成，你可以用awaitTermination 方法去这样做。
```
     * @throws SecurityException if a security manager exists and
     *         shutting down this ExecutorService may manipulate
     *         threads that the caller is not permitted to modify
     *         because it does not hold {@link
     *         java.lang.RuntimePermission}{@code ("modifyThread")},
     *         or the security manager's {@code checkAccess} method
     *         denies access.
     */
    void shutdown();

    /**
     * Attempts to stop all actively executing tasks, halts the
     * 的 of waiting tasks, and returns a list of the tasks
     * that were awaiting execution.
```
      试图停止所有活动的正在执行的任务，中止等待中的任务处理，并返回等待执行任务的列表。
```
     * <p>This method does not wait for actively executing tasks to
     * terminate.  Use {@link #awaitTermination awaitTermination} to
     * do that.
```
     本方法不会等待活动的执行中的任务中止，用awaitTermination方法可以。
     
```
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.  For example, typical
     * implementations will cancel via {@link Thread#interrupt}, so any
     * task that fails to respond to interrupts may never terminate.
```
     　除了尽量去尝试去中止处理活动的执行中的任务，没有任何保证。举例了，经典的实现通过Thread#interrupt方法取消一个线程的执行，
      所以对interrupt方法响应失败的任务可能永远无法中止。
     
      
```
    * @return list of tasks that never commenced execution
     * @throws SecurityException if a security manager exists and
     *         shutting down this ExecutorService may manipulate
     *         threads that the caller is not permitted to modify
     *         because it does not hold {@link
     *         java.lang.RuntimePermission}{@code ("modifyThread")},
     *         or the security manager's {@code checkAccess} method
     *         denies access.
     */
    List<Runnable> shutdownNow();

    /**
     * Returns {@code true} if this executor has been shut down.
     *
     * @return {@code true} if this executor has been shut down
     * 如果executor 已经关闭则返回true 
     */
    boolean isShutdown();

    /**
     * Returns {@code true} if all tasks have completed following shut down.
     * Note that {@code isTerminated} is never {@code true} unless
     * either {@code shutdown} or {@code shutdownNow} was called first.
```
     如果所有任务在shutdown 之后全部执行完成则返回true
     注意：isTerminated 可能永远为true,除非调用了shutdown或shutdownNow 方法被调用
```
     * @return {@code true} if all tasks have completed following shut down
     */
    boolean isTerminated();

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
```
      阻塞直到
1. 在shutdown 方法执行之后，所有任务完成执行
2. 时间超时
3. 当前线程interrupted 

三个条件任一个先发生
```
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return {@code true} if this executor terminated and
     *         {@code false} if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task. The
     * Future's {@code get} method will return the task's result upon
     * successful completion.
```
    提交一个有返回值的任务，执行并返回一个Future对象，代表着任务的待定结果。Future的get  方法会返回成功完成任务的执行结果。
```
     * <p>
     * If you would like to immediately block waiting
     * for a task, you can use constructions of the form
     * {@code result = exec.submit(aCallable).get();}
```
     如果你想立刻地阻塞等待任务的结果，你可以用如下构造形式。
    
```
     * <p>Note: The {@link Executors} class includes a set of methods
     * that can convert some other common closure-like objects,
     * for example, {@link java.security.PrivilegedAction} to
     * {@link Callable} form so they can be submitted.
```
     Executors 这个类包含一组方法，这些方法能转换成一些其他的闭包对象，比如将PrivilegedAction转成Callable形式，所以它们也可以被提交
```     
        /**
            * Returns a {@link Callable} object that, when
            * called, runs the given privileged action and returns its result.
            * @param action the privileged action to run
            * @return a callable object
            * @throws NullPointerException if action null
            */
           public static Callable<Object> callable(final PrivilegedAction<?> action) {
               if (action == null)
                   throw new NullPointerException();
               return new Callable<Object>() {
                   public Object call() { return action.run(); }};
           }
     *
     * @param task the task to submit
     * @param <T> the type of the task's result
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
     */
    <T> Future<T> submit(Callable<T> task);

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return the given result upon successful completion.
```
      提交一个任务执行并返回一个Future对象，Future的get 方法在任务执行成功并完成时获取返回结果
      **问题**　为什么runnable不需要返回结果，还需要传result 对象?
     解释地址: https://stackoverflow.com/questions/6456504/what-does-result-in-executorservice-submitrunnable-task-t-result-do
```
@param task the task to submit
     * @param result the result to return
     * @param <T> the type of the result
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
     */
    <T> Future<T> submit(Runnable task, T result);

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return {@code null} upon <em>successful</em> completion.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
     */
    Future<?> submit(Runnable task);

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results when all complete.
     * {@link Future#isDone} is {@code true} for each
     * element of the returned list.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
```
      执行给定义的任务列表，当所有任务都执行完的时侯,返回Future  列表，这些Future 保留任务的状态和结果。返回结果列表(FutureList)的每个Future的isDone 结果都为true
      注意：这个已经完成的任务已经中止，或者正常的执行完成，或者抛出异常。
     　如果在该操作的进行过程中，给定的任务集合被修改，那么返回结果为undefined
```
     *
     * @param tasks the collection of tasks
     * @param <T> the type of the values returned from the tasks
     * @return a list of Futures representing the tasks, in the same
     *         sequential order as produced by the iterator for the
     *         given task list, each of which has completed
     * @throws InterruptedException if interrupted while waiting, in
     *         which case unfinished tasks are cancelled
     * @throws NullPointerException if tasks or any of its elements are {@code null}
     * @throws RejectedExecutionException if any task cannot be
     *         scheduled for execution
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException;

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results
     * when all complete or the timeout expires, whichever happens first.
     * {@link Future#isDone} is {@code true} for each
     * element of the returned list.
     * Upon return, tasks that have not completed are cancelled.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     *
     * @param tasks the collection of tasks
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @param <T> the type of the values returned from the tasks
     * @return a list of Futures representing the tasks, in the same
     *         sequential order as produced by the iterator for the
     *         given task list. If the operation did not time out,
     *         each task will have completed. If it did time out, some
     *         of these tasks will not have completed.
     * @throws InterruptedException if interrupted while waiting, in
     *         which case unfinished tasks are cancelled
     * @throws NullPointerException if tasks, any of its elements, or
     *         unit are {@code null}
     * @throws RejectedExecutionException if any task cannot be scheduled
     *         for execution
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do. Upon normal or exceptional return,
     * tasks that have not completed are cancelled.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
```
  执行一些给定的任务
  1. 返回任一个已经成功完成任务的结果，也就是说没有抛出异常。
  2. 在正常或者异常返回时，未完成的任务将被取消。
  3. 在执行过程中如果任务列表被修改，则方法的返回结果为undefined
```
     * @param tasks the collection of tasks
     * @param <T> the type of the values returned from the tasks
     * @return the result returned by one of the tasks
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if tasks or any element task
     *         subject to execution is {@code null}
     * @throws IllegalArgumentException if tasks is empty
     * @throws ExecutionException if no task successfully completes
     * @throws RejectedExecutionException if tasks cannot be scheduled
     *         for execution
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException;

    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do before the given timeout elapses.
     * Upon normal or exceptional return, tasks that have not
     * completed are cancelled.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     *
     * @param tasks the collection of tasks
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @param <T> the type of the values returned from the tasks
     * @return the result returned by one of the tasks
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if tasks, or unit, or any element
     *         task subject to execution is {@code null}
     * @throws TimeoutException if the given timeout elapses before
     *         any task successfully completes
     * @throws ExecutionException if no task successfully completes
     * @throws RejectedExecutionException if tasks cannot be scheduled
     *         for execution
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```