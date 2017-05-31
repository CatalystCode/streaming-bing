package com.github.catalystcode.fortis.spark.streaming.bing

import java.util.concurrent.TimeUnit
import com.github.catalystcode.fortis.spark.streaming.PollingSchedule
import com.github.catalystcode.fortis.spark.streaming.bing.client.BingCustomSearchClient
import com.github.catalystcode.fortis.spark.streaming.bing.dto.BingPost
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.ReceiverInputDStream

object BingUtils {
  def createPageStream(
                        ssc: StreamingContext,
                        auth: BingAuth,
                        searchInstanceId: String,
                        keywords: Seq[String],
                        pollingSchedule: PollingSchedule = PollingSchedule(30, TimeUnit.SECONDS),
                        pollingWorkers: Int = 1,
                        storageLevel: StorageLevel = StorageLevel.MEMORY_ONLY
  ): ReceiverInputDStream[BingPost] = {
    new BingInputDStream(
      ssc = ssc,
      client = new BingCustomSearchClient(
        searchInstanceId = searchInstanceId,
        keywords = keywords,
        auth = auth),
      pollingSchedule = pollingSchedule,
      pollingWorkers = pollingWorkers,
      storageLevel = storageLevel)
  }
}
