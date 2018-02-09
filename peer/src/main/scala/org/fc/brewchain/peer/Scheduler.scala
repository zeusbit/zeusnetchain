package org.fc.brewchain.peer

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.ScheduledFuture

object Scheduler {
  val scheduler = new ScheduledThreadPoolExecutor(20);
  def shutdown() {
    scheduler.shutdown()
  }

  def scheduleWithFixedDelay(command: Runnable,
    initialDelay: Long,
    delay: Long,
    unit: TimeUnit): ScheduledFuture[_] = {
    scheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit);
  }
}