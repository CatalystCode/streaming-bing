package com.github.catalystcode.fortis.spark.streaming.bing.client

import com.github.catalystcode.fortis.spark.streaming.bing.BingAuth
import scalaj.http.Http

class BingCustomSearchClient(
                              searchInstanceId: String,
                              keywords: Seq[String],
                              auth: BingAuth,
                              recordCount: Int = 10,
                              freshness: String = "day")
extends BingClient(auth, keywords, recordCount) {
  override def fetchBingResponse(query: String, offset: Int, url: Option[String] = None): String = {
    val getUrl = url.getOrElse(s"https://${auth.apiHost}/${auth.customizedSearchApiService}/search?q=${query}&freshness=${freshness}&customconfig=${searchInstanceId}&count=${recordCount}&offset=${offset}&responseFilter=news&mkt=en-us&textFormat=Raw&textDecorations=true")
    logInfo(s"Fetching response from $getUrl")
    Http(getUrl)
      .headers(
        "Content-Type" -> "application/json",
        "Ocp-Apim-Subscription-Key" -> auth.accessToken)
      .asString
      .body
  }
}
