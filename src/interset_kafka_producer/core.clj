(ns interset-kafka-producer.core
  (:require [abracad.avro :as avro]
            [taoensso.timbre :as log]
            [clj-kafka.producer :refer :all]
            [clj-kafka.core :as k]
            [clojure.java.io :refer :all]
            [clj-time.coerce :as tc]
            [clj-kafka.consumer.zk :refer :all]
            [clojure.core.async :as async :refer [>!! <!! chan thread]])
  (:import [kafka.consumer ConsumerConfig Consumer KafkaStream]))

(def endpoint-schema1
  (avro/parse-schema
   {:name "endpoint-schema1"
    :type :record
    :fields [{:name "timestamp", :type "long"}
             {:name "user", :type "string"}
             {:name "host" :type "string"}
             {:name "file", :type ["string", "null"]}]}))

(def repository-schema
  (avro/parse-schema
   {:name "repository-schema"
    :type :record
    :fields [{:name "timestamp", :type "long"}
             {:name "user", :type "string"}
             {:name "project", :type "string"}
             {:name "action", :type ["string", "null"]}]}))

;; Producer part logic:
(defn make-producer
  "This function will create an instance of Kafka producer, pass in Kafka broker ip and service port"
  [conf]
  (let [config (assoc {}
                      "metadata.broker.list" conf
                      "producer.type" "async"
                      "key.serializer.class" "kafka.serializer.StringEncoder"
                      "message.send.max.retries" "5")]
    (producer config)))

(defn read-file-and-send-to-kafka
  "This function will parse the file, convert the DateTime to Epoch timestamp, convert
  the data to a list of avro object, and then send to the broker.
  Example:
  (read-file-and-send-to-kafka /data/file1 localhost:9092 endpoint-schema1 topic1)"
  [file-name broker-list schema topic]
  (let [avro-data (->> file-name
                       slurp
                       clojure.string/split-lines
                       (map (fn [ele] (clojure.string/split ele #",")))
                       (map #(let [data ((juxt (fn [d] (->> d first tc/to-long))
                                               second
                                               (fn [d] (nth d 2))
                                               last) %)
                                   msg (avro/binary-encoded schema data)]
                               (message (str topic) (->> data second str) msg))))
        prod (make-producer broker-list)]
    (send-messages prod avro-data)))


(defn -main
  [& args]
  (log/info "Start reading file and send as avro message")
  ;; send the endpoint data
  (read-file-and-send-to-kafka "endpoint-v1.csv" "localhost:9092" endpoint-schema1 "endpoint1")
  ;; Send the repository data
  (read-file-and-send-to-kafka "repository.csv" "localhost:9092" repository-schema "repository"))
