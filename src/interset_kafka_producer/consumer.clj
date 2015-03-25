(ns interset-kafka-producer.consumer
  (:require [abracad.avro :as avro]
            [taoensso.timbre :as log]
            [clj-kafka.core :as k]
            [clojure.java.io :refer :all]
            [clj-time.coerce :as tc]
            [clj-kafka.consumer.zk :refer :all]
            [clojure.core.async :as async :refer [>!! <!! chan thread]]
            [interset-kafka-producer.core :refer :all]))

;;Consumer part logic

(defn make-consumer
  [zk]
  (let [conf {"zookeeper.connect" zk
              "group.id" "test-group"
              "auto.offset.reset" "smallest"
              "auto.commit.interval.ms" "300"}]
    (consumer conf)))

(defn messages->channel
  "Create channels that represent each of the 'topics' using 'consumer' and return
  a map which has the topics as the keys and the channels as the values"
  [consumer topics]
  (let [topic-map (apply hash-map (interleave topics (repeat (Integer/valueOf 1))))
        channels (apply hash-map (interleave topics (repeatedly chan)))]
    (doseq [[topic [stream]] (.createMessageStreams (:kafka-consumer consumer) topic-map)]
      (thread (doseq [msg (iterator-seq (.iterator stream))]
                (>!! (channels topic) (k/to-clojure msg)))))
    channels))

(def endpoint-map (atom {}))
(def repository (atom {}))

(defn consume-logic
  [schema at topic]
  (let [cons (make-consumer "localhost:2181")
        [t c] (messages->channel cons [topic])]
    (loop []
      (let [{:keys [topic offset partition key value]} (<!! c)
            msg (avro/decode schema value)]
        (swap! at (fn [a b]
                    (let [v (a b)]
                      (if (nil? v)
                        (assoc a b 1)
                        (assoc a b (inc v))
                        ))) (:name msg)))
      (recur))))

(defn -main2
  [& args]
  (log/info "Start reading avro message")
  ;; read and print endpoint1 data
  (consume-logic endpoint-schema1 endpoint-map "endpoint1")
  ;; ;; read and print repository data
  (consume-logic repository-schema repository "repository"))
