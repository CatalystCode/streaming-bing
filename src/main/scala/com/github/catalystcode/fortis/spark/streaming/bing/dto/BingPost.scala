package com.github.catalystcode.fortis.spark.streaming.bing.dto

case class BingPost(
  name: String,
  url: String,
  snippet: String,
  dateLastCrawled: String
)
