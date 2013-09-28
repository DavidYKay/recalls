(ns recalls.core
  (:require [monger.collection :as mc])
  (:use
    [clojure.data.xml]
    [recalls.env]
    [monger.result :only [ok? has-error?]]
    )
  )

(def DATE-SEARCH-URL "http://www.cpsc.gov/cgibin/CPSCUpcWS/CPSCUpcSvc.asmx/getRecallByDate?startDate=1973-01-01&endDate=2012-01-01&userid=john&password=abc")

(defn get-xml []
  (let [xml-string (read-resource-as-text "fixtures/recalls.xml")
        results-xml (parse-str xml-string)]
    results-xml))

(defn get-recalls []
  (:content (first (:content (get-xml)))))

(defn insert-data-to-db []
  (let [recalls (get-recalls)
        response (mc/insert-batch "recalls" recalls)]
    (println (ok? response))
    (println (has-error? response)))
  )

(defn get-first-result []
  (let [recalls (get-recalls)]
    (:attrs (first recalls))))
