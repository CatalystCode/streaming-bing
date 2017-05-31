package com.github.catalystcode.fortis.spark.streaming.bing

case class BingAuth(
  accessToken: String,
  apiHost: String = "api.cognitive.microsoft.com",
  customizedSearchApiService: String = "/bingcustomsearch/v5.0/"
)
