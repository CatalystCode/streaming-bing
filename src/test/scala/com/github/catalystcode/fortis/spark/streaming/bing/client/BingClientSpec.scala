package com.github.catalystcode.fortis.spark.streaming.bing.client

/**
  * Created by erisch on 5/31/2017.
  */

import java.io.IOException

import com.github.catalystcode.fortis.spark.streaming.bing.BingAuth
import com.github.catalystcode.fortis.spark.streaming.bing.dto._
import org.scalatest.FlatSpec

class TestBingClient(responses: Map[Option[String], String]) extends BingClient(BingAuth(accessToken = "token"), List("news"), 3) {
  override def fetchBingResponse(query: String, offset: Int, url: Option[String] = None): String = responses(url)
}

class TestExceptionBingClient(exception: Exception) extends BingClient(BingAuth(accessToken = "token"), List("news")) {
  override protected def fetchBingResponse(query: String, offset: Int, url: Option[String] = None): String = throw exception
}

class BingClientSpec extends FlatSpec {
  private val resultRecord = (newsItem: BingPost) => s"""
                                                        |{
                                                        |        "name": "${newsItem.name}",
                                                        |        "url": "${newsItem.url}",
                                                        |        "snippet": "${newsItem.snippet}",
                                                        |        "dateLastCrawled": "${newsItem.dateLastCrawled}"
                                                        |}
    """.stripMargin

  private val itemObj = (url: String) => BingPost(
    name = "Sample Bing News Article Title",
    snippet = "This represents a news full text search matched snippet blurb...",
    url = url,
    dateLastCrawled = "2017-06-01T00:01:00")

  private val mockedResponse = (articleURLs: List[String]) => s"""{
                                                                 |  "_type": "SearchResponse",
                                                                 |  "instrumentation": {
                                                                 |    "pingUrlBase": "https://www.bingapis.com/api/ping?IG=FEC796CF65DA4B8CB1C1570EA&CID=1C58647EECC66B921DAE6EEAED996A69&ID=",
                                                                 |    "pageLoadPingUrl": "https://www.bingapis.com/api/ping/pageload?IG=FEC798CB1C15702770F68EA&CID=1C58647EECC66B921DAE6EEAED996A69&Type=Event.CPT&DATA=0"
                                                                 |  },
                                                                 |  "webPages": {
                                                                 |    "webSearchUrl": "https://www.bing.com/search?q=(al+shabaab)%7c%7c(isis)",
                                                                 |    "webSearchUrlPingSuffix": "DevEx,5182.1",
                                                                 |    "totalEstimatedMatches": ${articleURLs.size},
                                                                 |    "value": [${articleURLs.map(url=>resultRecord(itemObj(url))).mkString(",")}]
                                                                 |  }
                                                                 |}
    """.stripMargin

  "The bing client" should "produce domain objects from the json api response" in {
    val url = "http://www.cnn.com/a-returned-bing-url.html"
    val client = new TestBingClient(Map(
      None -> mockedResponse(List(url))))

    val response = client.loadNewPostings().toList

    assert(response.length === 1)
    assert(response.head === itemObj(url))
  }

  /*"The bing client" should " be able to support pagination and return an accumulated list of domain objects across all requested pages" in {
    val (bingRequestPage1, bingRequestPage2, bingRequestPage3) = ("aka.ms/bingApi.1", "aka.ms/bingApi.2", "aka.ms/bingApi.3")
    val urlList = List("http://www.cnn.com/a-returned-bing-url1.html", "http://www.cnn.com/a-returned-bing-url2.html", "http://www.cnn.com/a-returned-bing-url3.html")
    val urlList2 = List("http://www.cnn.com/a-returned-bing-url4.html", "http://www.cnn.com/a-returned-bing-url5.html", "http://www.cnn.com/a-returned-bing-url6.html")
    val client = new TestBingClient(Map(
      None -> mockedResponse(urlList),
      Some(bingRequestPage1) -> mockedResponse(urlList),
      Some(bingRequestPage2) -> mockedResponse(urlList2)))

    val response = client.loadNewPostings().toList

    assert(response.length === urlList.size * 3)*/

  it should "handle exceptions" in {
    val client = new TestExceptionBingClient(new IOException())

    val response = client.loadNewPostings()

    assert(response === List())
  }
}
