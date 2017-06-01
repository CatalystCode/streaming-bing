package com.github.catalystcode.fortis.spark.streaming.bing

import org.apache.log4j.LogManager

trait Logger {
  @transient private lazy val log = LogManager.getLogger("lib-bing")

  def logDebug(message: String): Unit = log.debug(message)
  def logInfo(message: String): Unit = log.info(message)
  def logError(message: String, throwable: Throwable): Unit = log.error(message, throwable)
}
