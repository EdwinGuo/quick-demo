(defproject interset-kafka-producer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :aot :all
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [clj-kafka/clj-kafka "0.2.8-0.8.1.1"]
                 [com.damballa/abracad "0.4.11"]
                 [org.clojure/data.json "0.2.2"]
                 [com.taoensso/timbre "3.2.1"]
                 [clj-time "0.9.0"]
                 [org.apache.spark/spark-core_2.10 "1.3.0"]
		 [org.apache.spark/spark-streaming_2.10 "1.3.0"]
                 [org.apache.spark/spark-streaming-kafka_2.10 "1.3.0"]
                 [org.apache.spark/spark-streaming-flume_2.10 "1.3.0"]            
    		 [yieldbot/flambo "0.6.0-SNAPSHOT"]])
