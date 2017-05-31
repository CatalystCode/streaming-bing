import com.github.catalystcode.fortis.spark.streaming.bing.BingAuth
import org.apache.log4j.{BasicConfigurator, Level, Logger}

object BingDemo {
  def main(args: Array[String]) {
    val mode = args.headOption.getOrElse("")

    // configure page for which to ingest posts
    val pageId = "aljazeera"

    // configure interaction with facebook api
    val auth = BingAuth(accessToken = System.getenv("BING_AUTH_TOKEN"))

    // configure logging
    BasicConfigurator.configure()
    Logger.getRootLogger.setLevel(Level.ERROR)
    Logger.getLogger("libfacebook").setLevel(Level.DEBUG)

    if (mode.contains("standalone")) new BingDemoStandalone(pageId, auth).run()
    if (mode.contains("spark")) new BingDemoSpark(pageId, auth).run()
  }
}
