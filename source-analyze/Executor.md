```
/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent;

/**
 * An object that executes submitted {@link Runnable} tasks. This
 * interface provides a way of decoupling task submission from the
 * mechanics of how each task will be run, including details of thread
 * use, scheduling, etc.  An {@code Executor} is normally used
 * instead of explicitly creating threads. For example, rather than
 * invoking {@code new Thread(new(RunnableTask())).start()} for each
 * of a set of tasks, you might use:
```
   这是一个可以执行被提交任务(任务要实现Runnable接口)的对象，Executor 接口提供一种解耦的任务提交方式，
  这种方式源于每个任务如何被执行的机制，包括线程的使用细节，调度等等。这个接口通常被用于明确创建线程的替代形式。举个例子,
  相对于原来的 {@code new Thread(new(RunnableTask())).start()} 直接调用方式，你可能用以下方式：
 ```
 * <pre>
 * Executor executor = <em>anExecutor</em>;
 * executor.execute(new RunnableTask1());
 * executor.execute(new RunnableTask2());
 * ...
 * </pre>
 *
 * However, the {@code Executor} interface does not strictly
 * require that execution be asynchronous. In the simplest case, an
 * executor can run the submitted task immediately in the caller's
 * thread:
 ```
  然而，
 　Executor接口要求并不严格要求任务被异步的执行，在最简单的场景下,
 　一个Executor能在当前调用的线程中立刻被执行。
 ```
 *  <pre> {@code
 * class DirectExecutor implements Executor {
 *   public void execute(Runnable r) {
 *     r.run();
 *   }
 * }}</pre>
 *
 * More typically, tasks are executed in some thread other
 * than the caller's thread.  The executor below spawns a new thread
 * for each task.
 ```
  更多典型的场景下,任务被一些线程执行，而并不在当前线程下,下面的例子中，为每一个任务就产生了一个新的线程。
 ```
 *  <pre> {@code
 * class ThreadPerTaskExecutor implements Executor {
 *   public void execute(Runnable r) {
 *     new Thread(r).start();
 *   }
 * }}</pre>
 *
 * Many {@code Executor} implementations impose some sort of
 * limitation on how and when tasks are scheduled.  The executor below
 * serializes the submission of tasks to a second executor,
 * illustrating a composite executor.
 ```
 　一些Executor的实现强加某种关于任务何时和如何被调度的限制，下边的串行化的任务交给第二个executor去执行。一个组合的executor的示例如下：
 ```
 *  <pre> {@code
 * class SerialExecutor implements Executor {
 *   final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
 *   final Executor executor;
 *   Runnable active;
 *
 *   SerialExecutor(Executor executor) {
 *     this.executor = executor;
 *   }
 *
 *   public synchronized void execute(final Runnable r) {
 *     tasks.offer(new Runnable() {
 *       public void run() {
 *         try {
 *           r.run();
 *         } finally {
 *           scheduleNext();
 *         }
 *       }
 *     });
 *     if (active == null) {
 *       scheduleNext();
 *     }
 *   }
 *
 *   protected synchronized void scheduleNext() {
 *     if ((active = tasks.poll()) != null) {
 *       executor.execute(active);
 *     }
 *   }
 * }}</pre>
 *
 * The {@code Executor} implementations provided in this package
 * implement {@link ExecutorService}, which is a more extensive
 * interface.  The {@link ThreadPoolExecutor} class provides an
 * 个thread pool implementation. The {@link Executors} class
 * provides convenient factory methods for these Executors.
```
在当前包下提供了一些实现ExecutorService接口的实现，这个接口是一个相对比较广泛的接口，
 ThreadPoolExecutor　该类提供了一个可扩展的线程池实现。Execturos静态类提供一些方便的工厂方法去创建不同的Executor

```
 * <p>Memory consistency effects: Actions in a thread prior to
 * submitting a {@code Runnable} object to an {@code Executor}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * its execution begins, perhaps in another thread.
```
  内存一致性影响：在当前线程的动作要优先于提交给Executor的任务对象，
  它们在真正开始执行行的时侯，可能在另一个线程里了。
```
 * @since 1.5
 * @author Doug Lea
 */
public interface Executor {

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
```
执行给定的命令任务在未来的某个时间点，这个命令可能在一个新的线程里，或者在一个线程池里，也有可能就在前调用线程中。
 这些源于Executor的灵活实现。
```
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     * accepted for execution
     * @throws NullPointerException if command is null
     */
    void execute(Runnable command);
}
```