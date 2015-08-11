(ns t-re
  (:require [prac.p1 :refer :all]
            [clojure.test :refer :all]))

(def c1 "2014/10/25 23:11:00:::1414294660411:::159.175.50.50:::80000004233:::12340000:::2:::180:::test@null.com:::N:::3.771:::email_change_last4hr=1.771,item_sku=1,ipaddress_europe=1")

(def c2 "2014/11/25 23:21:00:::141312360411:::157.175.50.50:::80456004233:::12340777:::7:::181:::test@null.ca:::N:::4.71:::email_change_last4hr=1.13,item_sku=2,ipaddress_europe=6")

(def c3 "2014/12/25 22:11:33:::554231660411:::159.222.50.50:::80312004233:::12340312:::2:::178:::test@hello.com:::N:::3.72:::email_change_last4hr=333,item_sku=1,ipaddress=6")

(def d1 "2015-03-01 14:01:11,725 [PERF] [pool-4-thread-1] [INFO ] - PERF-END   TX[80000004233,000000000000,5000480905] PROCESS-ISO-REQUEST execution took 173 ms [ 0 : 2110, 2 : 80000004233, 3 : 300000, 4 : 9990000000000000, 5 : 9990000000002054, 7 : 0327020111, 8 : 9990000000000000, 9 : 1242000000000000, 11 : 000000000000, 12 : 20150327130001, 32 : 130011, 39 : 0000, 41 : 5000480905      , 42 : 100320, 63 : , ]")

(def d2 "2015-03-02 15:01:11,725 [PERF] [pool-4-thread-1] [INFO ] - PERF-END   TX[80000004233,000000000000,5000480905] PROCESS-ISO-REQUEST execution took 173 ms [ 0 : 2111, 1 : 60000004233, 3 : 300220, 4 : 3210000000000000, 5 : 1110000000002054, 7 : 7034560111, 8 : 1230000000000000, 9 : 8320000000000, 11 : 0001230000, 12 : 2015456730001, 32 : 122011, 39 : 1100, 41 : 500123     , 42 : 60320, 63 : , ]")

(def d3 "2015-03-03 16:01:11,725 [PERF] [pool-4-thread-1] [INFO ] - PERF-END   TX[80000004233,000000000000,5000480905] PROCESS-ISO-REQUEST execution took 173 ms [ 0 : 2112, 2 : 80000004233, 3 : 300000, 4 : 9990005678000000, 5 : 123000002054, 7 : 5547020111, 8 : 1530000000000000, 9 : 88420000000000, 11 : 12100000000, 12 : 2000327130001, 32 : 111211, 39 : 30000, 41 : 1480905      , 42 : 700320, 63 : , 64 : 3992]")

(deftest test-construct-field-result
  (testing "testing ::: delimited data parse for c1"
    (let [data (construct-field c1)]
      (is (= (get data "datetime") "2014/10/25 23:11:00"))
      (is (= (get data "collectorNumber") "80000004233"))
      (is (= (get data "email") "test@null.com"))
      (is (= (get data "orderID") "1414294660411"))
      (is (= (get data "ipAddress") "159.175.50.50"))))

  (testing "testing ::: delimited data parse for c2"
    (let [data (construct-field c2)]
      (is (= (get data "datetime") "2014/11/25 23:21:00"))
      (is (= (get data "sku") "12340777"))
      (is (= (get data "email") "test@null.ca"))
      (is (= (get data "orderID") "141312360411"))
      (is (= (get data "score_details") "email_change_last4hr=1.13,item_sku=2,ipaddress_europe=6"))))

  (testing "testing ::: delimited data parse for c3"
    (let [data (construct-field c3)]
      (is (= (get data "datetime") "2014/12/25 22:11:33"))
      (is (= (get data "collectorNumber") "80312004233"))
      (is (= (get data "email") "test@hello.com"))
      (is (= (get data "orderID") "554231660411"))
      (is (= (get data "ipAddress") "159.222.50.50")))))

(deftest test-parse-log-date
  (testing "retrieve : delimited field from d1"
    (let [data (parse-log-data d1)]
      (is (= (get data "9") "1242000000000000"))
      (is (= (get data "3") "300000"))
      (is (= (get data "4") "9990000000000000"))
      (is (= (get data "41") "5000480905"))
      (is (= (get data "63") nil))))

  (testing "retrieve : delimited field from d2"
    (let [data (parse-log-data d2)]
      (is (= (get data "9") "8320000000000"))
      (is (= (get data "12") "2015456730001"))
      (is (= (get data "32") "122011"))
      (is (= (get data "5") "1110000000002054"))
      (is (= (get data "63") nil))))

  (testing "retrieve : delimited field from d3"
    (let [data (parse-log-data d3)]
      (is (= (get data "9") "88420000000000"))
      (is (= (get data "12") "2000327130001"))
      (is (= (get data "32") "111211"))
      (is (= (get data "5") "123000002054"))
      (is (= (get data "63") nil)))))
