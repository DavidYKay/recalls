(ns recalls.db
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:use [monger.conversion :only [from-db-object]])
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types ObjectId]))

(defn connect! []
  (mg/connect!)
  (mg/set-db! (mg/get-db "recalls"))
  )

(defn disconnect! []
  (mg/disconnect!)
  )

(defn run-mongo []
  (mg/connect!)

  ;; localhost, default port
  (mg/set-db! (mg/get-db "recalls"))

  ;; with explicit document id (recommended)

  ;; multiple documents at once
  (mc/insert-batch "documents" [{:first_name "John" :last_name "Lennon"}
                               {:first_name "Paul" :last_name "McCartney"}])

  (println "Documents: " (mc/find-maps "documents"))

  (mg/disconnect!)
  )

(defn get-all-recalls []
  (mc/find-maps "recalls"))

(defn save-recall! [recall]
  (if (nil? recall)
    nil
    (mc/insert "recalls" recall)))

(defn get-recall [recall-name]
  (mc/find-one-as-map "recalls"
                      { :name recall-name }))

(defn has-recall? [recall-name]
  (not (nil? (get-recall recall-name))))
