import com.github.catalystcode.fortis.spark.streaming.bing.{BingAuth, BingUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

class BingDemoSpark(pageId: String, auth: BingAuth) {
  def run(): Unit = {
    // set up the spark context and streams
    val conf = new SparkConf().setAppName("Facebook Spark Streaming Demo Application")
    val sc = new SparkContext(conf)
    val searchInstanceId = System.getenv("BING_SEARCH_INSTANCE_ID")
    val ssc = new StreamingContext(sc, Seconds(1))
    val keywordSet = List("isis", "al shabaab")

    BingUtils.createPageStream(ssc, auth, searchInstanceId, keywordSet).map(x => s"Post: ${x.url}").print()

    // run forever
    ssc.start()
    ssc.awaitTermination()
  }

}
