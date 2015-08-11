(ns interset-kafka-producer.streaming
  (:require [flambo.conf :as c]
            [flambo.streaming :as s]
            [flambo.api :as f]
            [flambo.function :as fu])
  (:import [org.apache.spark SparkConf]
           scala.Tuple2))

(defn return-spark-conf
  []
  (-> (SparkConf.)
      (c/master "spark://127.0.0.1:7077")
      (c/app-name "streaming test")
      (c/jars ["/*/target/interset-kafka-producer-0.1.0-SNAPSHOT-standalone.jar"])))

(defn do-count
  []
  (let [sconf (return-spark-conf)
        ssc (s/streaming-context sconf 5000)
        _ (.checkpoint ssc "/Users/EdwinGuo/checkp")
        topic-map {"endpoint1" 1 "repqository" 1}
        s1 (.map

            (s/kafka-stream
             :streaming-context ssc
             :zk-connect "127.0.0.1:2181"
             :group-id "test-streaming"
             :topic-map topic-map)

            (fu/function (fn [s] (._2 s))))

        s2 (.flatMap s1 (fu/flat-map-function (fn [s] (clojure.string/split s #" "))))
        s3 (.map s2 (fu/function (fn [s] (Tuple2. s 1))))
        ;; s4 (s/reduce-by-key-and-window s3 (fn [a b] (+ a b)) 5000 5000)
        ]

    (do
      (println "HELLO EDWIN 1: ")
      (.print s3)
      (.start ssc)
      (.awaitTermination ssc))))









(defn -main
  [& args]
  (println "Start spark streaming!")
  (do-count))
