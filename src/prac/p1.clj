(ns prac.p1)

(defn construct-field
  "will parse the log data delimited by ::: and return a map"
  [data & {:keys [kk] :or {kk "datetime:::orderID:::ipAddress:::collectorNumber:::sku:::quantity:::amrm_redeemed:::email:::fraud:::score:::score_details"}}]
  (let [tr-data (clojure.string/split data #":::")
        tr-keys (clojure.string/split kk #":::")]
    (into {} (mapcat hash-map tr-keys tr-data))))

(defn parse-log-data
  "will parse the data into a map of string -> string and return a map"
  [data]
  (let [tr-data (re-seq #"\d+\s:\s\d+" data)]
    (into {} (map #(let [[k v] (clojure.string/split % #" : ")] (hash-map k v)) tr-data))))
