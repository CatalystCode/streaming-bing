package com.github.catalystcode.fortis.spark.streaming.bing.dto

/**
  * Created by erisch on 5/30/2017.
  */
case class BingWebPage(totalEstimatedMatches: Int, value: List[BingPost])
case class BingResponse(webPages: BingWebPage)
