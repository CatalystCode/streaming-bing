package com.github.catalystcode.fortis.spark.streaming.bing.client

import java.io.IOException

import com.github.catalystcode.fortis.spark.streaming.bing.dto.{BingPost, BingResponse, BingWebPage}
import com.github.catalystcode.fortis.spark.streaming.bing.{BingAuth, Logger}
import net.liftweb.json

@SerialVersionUID(100L)
abstract class BingClient(auth: BingAuth,
                          keywords: Seq[String],
                          recordCount: Int = 40)
extends Serializable with Logger {

  def loadNewPostings(): Iterable[BingPost] = {
    try {
      loadBingPostings(keywords)
    } catch {
      case ex: IOException =>
        logError(s"Exception while loading bing search results", ex)
        List()
    }
  }

  private def parseResponse(apiResponse: String): BingWebPage = {
    implicit val formats = json.DefaultFormats
    val response = json.parse(apiResponse).extract[BingResponse]
    response.webPages
  }

  private def loadBingPostings(keywords: Seq[String], url: Option[String] = None): Iterable[BingPost] = {
    keywords match {
      case Nil => List()
      case k :: ks => val response = parseResponse(fetchBingResponse(keywords.mkString(" OR "), url))

        var payload = response.value
        logInfo(s"Got json response with ${response.value.length} entries")
        if (response.totalEstimatedMatches == recordCount) {
          logInfo(s"Result page size ${response.value.size}")
          payload ++= loadBingPostings(keywords)
        }

        payload
    }
  }

  protected def fetchBingResponse(query: String, url: Option[String] = None, offset: Int = 0): String
}
