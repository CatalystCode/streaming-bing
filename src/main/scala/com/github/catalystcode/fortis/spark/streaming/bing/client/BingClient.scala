package com.github.catalystcode.fortis.spark.streaming.bing.client

import com.github.catalystcode.fortis.spark.streaming.bing.dto.{BingPost, BingResponse, BingWebPage}
import com.github.catalystcode.fortis.spark.streaming.bing.{BingAuth, Logger}
import net.liftweb.json

@SerialVersionUID(100L)
abstract class BingClient(auth: BingAuth,
                          keywords: Seq[String],
                          var recordCount: Int = 20)
extends Serializable with Logger {
  //Bing has a max result set count of 20 records at the moment
  final val MAX_RECORD_COUNT = 20
  final val BING_RATE_LIMIT_THROTTLE_RATE = 1000
  final val URL_ENCODE_SPACE = "%20"
  final val BING_QL_TERM_OR_SEPARATOR = ")||("

  def loadNewPostings(): Iterable[BingPost] = {
    try {
      recordCount = if(recordCount > MAX_RECORD_COUNT) MAX_RECORD_COUNT else recordCount
      loadBingPostings(keywords)
    } catch {
      case ex: Exception =>
        logError(s"Exception while loading bing search results", ex)
        None
    }
  }

  private def parseResponse(apiResponse: String): Option[BingWebPage] = {
    implicit val formats = json.DefaultFormats
    try{
      val response = json.parse(apiResponse).extract[BingResponse]
      Option(response.webPages)
    }catch {
      case ex @ (_ : net.liftweb.json.MappingException) =>
        logError(s"Unable to parse json payload from bing", ex)
        None
    }
  }

  private def loadBingPostings(keywords: Seq[String], offset: Int = 0, url: Option[String] = None): Iterable[BingPost] = {
    keywords match {
      case Nil => List()
      case k :: ks => parseResponse(fetchBingResponse(s"(${keywords.map(_.replace(" ", URL_ENCODE_SPACE)).mkString(BING_QL_TERM_OR_SEPARATOR)})", offset, url)) match {
        case None => List()
        case Some(response) => var payload = response.value
          logInfo(s"Got json response with ${response.value.length} entries")
          if (response.totalEstimatedMatches > offset + payload.size) {
            logInfo(s"Result page size ${response.value.size}")
            Thread.sleep(BING_RATE_LIMIT_THROTTLE_RATE)
            payload ++= loadBingPostings(keywords, offset + recordCount)
          }

          payload
      }
    }
  }

  protected def fetchBingResponse(query: String, offset: Int = 0, url: Option[String] = None): String
}
