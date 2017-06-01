# streaming-bing
[![Travis CI status](https://travis-ci.org/CatalystCode/streaming-bing.svg?branch=master)](https://travis-ci.org/CatalystCode/streaming-bing)

A library for reading public web news results from [Bing Custom Search](https://customsearch.ai/) using Spark Streaming.
![bing-custom-search](https://cloud.githubusercontent.com/assets/7635865/26688550/8f6f2800-46b8-11e7-907f-8aba0691647e.png)

## Usage example ##

Run a demo via:

```sh
# set up all the requisite environment variables
export BING_SEARCH_INSTANCE_ID="..."
export BING_AUTH_TOKEN="..."

# compile scala, run tests, build fat jar
sbt assembly

# run locally
java -cp target/scala-2.11/streaming-bing-assembly-0.0.7.jar BingDemo standalone

# run on spark
spark-submit --class BingDemo --master local[2] target/scala-2.11/streaming-bing-assembly-0.0.7.jar spark
```

## How does it work? ##

Bing Custom Search doesn't support streamed web results so we currently poll the service based on a polling interval rate. The BingReceiver pings the Bing Search API every few
seconds and pushes any newly indexed web results into Spark Streaming for further processing.

## Release process ##

1. Configure your credentials via the `SONATYPE_USER` and `SONATYPE_PASSWORD` environment variables.
2. Update `version.sbt`
3. Run `sbt sonatypeOpen "enter staging description here"`
4. Run `sbt publishSigned`
5. Run `sbt sonatypeRelease`
