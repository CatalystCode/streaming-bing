package com.github.catalystcode.fortis.spark.streaming.bing

import com.github.catalystcode.fortis.spark.streaming.bing.client.BingClient
import com.github.catalystcode.fortis.spark.streaming.bing.dto.BingPost
import com.github.catalystcode.fortis.spark.streaming.{PollingReceiver, PollingSchedule}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.receiver.Receiver

private class BingReceiver(
                            client: BingClient,
                            pollingSchedule: PollingSchedule,
                            storageLevel: StorageLevel,
                            pollingWorkers: Int
) extends PollingReceiver[BingPost](pollingSchedule, pollingWorkers, storageLevel) with Logger {

  @volatile private var lastIngestedDate = Long.MinValue

  override protected def poll(): Unit = {
    client
      .loadNewPostings
      .filter(x => {
        logDebug(s"Received Bing result ${x.name} from time ${x.dateLastCrawled}")
        isNew(x)
      })
      .foreach(x => {
        logInfo(s"Storing bing result ${x.url}")
        store(x)
        markStored(x)
      })
  }

  private def isNew(item: BingPost) = {
    val createdAt = item.dateLastCrawled.toLong
    createdAt > lastIngestedDate
  }

  private def markStored(item: BingPost): Unit = {
    val itemCreatedAt = item.dateLastCrawled.toLong

    if (isNew(item)) {
      lastIngestedDate = itemCreatedAt
      logDebug(s"Updating last ingested date to ${item.dateLastCrawled}")
    }
  }
}

class BingInputDStream(
                        ssc: StreamingContext,
                        client: BingClient,
                        pollingSchedule: PollingSchedule,
                        pollingWorkers: Int,
                        storageLevel: StorageLevel
) extends ReceiverInputDStream[BingPost](ssc) {

  override def getReceiver(): Receiver[BingPost] = {
    logDebug("Creating bing receiver")
    new BingReceiver(client, pollingSchedule, storageLevel, pollingWorkers)
  }
}
