(ns recalls.core
  (:require
    [monger.collection :as mc]
    [recalls.db :as db]
    [clojure.data.xml :as xml]
    )
  (:use
    [recalls.env]
    [monger.result :only [ok? has-error?]]
    [clj-time.format :only [parse formatters]]
    )
  )

(def DATE-SEARCH-URL "http://www.cpsc.gov/cgibin/CPSCUpcWS/CPSCUpcSvc.asmx/getRecallByDate?startDate=1973-01-01&endDate=2012-01-01&userid=john&password=abc")

(defn parse-date [date-string]
  (parse (formatters :year-month-day) date-string))

(defn get-xml []
  (let [xml-string (read-resource-as-text "fixtures/recalls.xml")
        results-xml (xml/parse-str xml-string)]
    results-xml))

(defn get-recalls []
  (let [base-recalls (map :attrs (:content (first (:content (get-xml)))))
        revised-recalls (map (fn [recall]
                               (let [date-string (:recDate recall)
                                     rec-date (parse-date date-string)]
                               (assoc recall :recDate rec-date)))
                             base-recalls)]
    revised-recalls))

(defn insert-data-to-db []
  (let [recalls (get-recalls)
        response (mc/insert-batch "recalls" recalls)]
    (println (ok? response))
    (println (has-error? response))))

(defn get-first-result []
  (let [recalls (get-recalls)]
    (:attrs (first recalls))))

(defn recalls-by-manufacturer []
  (group-by :manufacturer (db/get-all-recalls)))

(defn recalls-by-time []
  (group-by :manufacturer (db/get-all-recalls)))

