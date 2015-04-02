(ns snomed-browser.core
  (:require [korma.db :refer [defdb] :as db]
            [korma.core :refer :all]
            [snomed-browser.config :refer [settings]])
  (:gen-class))

(def ^:const ^:private core-moduleid "900000000000207008")
(def ^:const ^:private vet-moduleid "332351000009108")
(def ^:const ^:private is-a "116680003")
(def ^:const ^:private fully-specified-name "900000000000003001")
(def ^:const ^:private synonym "900000000000013009")


(def conn (db/mysql {:db "snomedct"
                     :host (:db-host @settings)
                     :user (:db-user @settings)
                     :password (:db-password @settings)}))

(defdb snomed-db conn)

(defentity concept
  (table :curr_concept_f)
  (entity-fields :id :effectiveTime :active))

(defentity relationship
  (table :curr_relationship_f)
  (entity-fields :sourceid :destinationid :typeid :moduleid :active))

(defentity description
  (table :curr_description_f)
  (entity-fields :active :moduleid :conceptid :languagecode :typeid :term))

(defn concept-active?
  "Returns true if the concept is active or false if it is retired."
  [sctid]
  (let [records (exec-raw [(str "SELECT cc1.* FROM curr_concept_f cc1"
                                " LEFT OUTER JOIN curr_concept_f cc2"
                                " ON (cc1.id=cc2.id AND cc1.effectivetime < cc2.effectivetime)"
                                " WHERE cc2.id is NULL AND cc1.id=?")
                           [sctid]] :results)]
    (= "1" (:active (first records)))))

(defn descriptions
  "Returns the descriptions for the concept."
  [sctid]
  (let [record (select description
                 (where {:conceptid sctid
                         :active 1
                         :languagecode "en"}))]
    (set (map :term record))))

(defn fsn-description
  "Returns the fully specified name for the concept."
  [sctid]
  (let [record (select description
                 (where {:conceptid sctid
                         :active 1
                         :languagecode "en"
                         :typeid fully-specified-name}))]
    (:term (first record))))

(defn parents
  "Returns all the active parent SCTID's for the concept."
  [sctid]
  (let [records (select relationship
                 (where {:sourceid sctid
                         :active 1
                         :typeid is-a}))]
    (->> records
         (filter #(concept-active? (:destinationid %)))
         (map :destinationid))))

(defn children
  "Returns all the active children SCTID's for the concept."
  [sctid]
  (let [records (select relationship
                 (where {:destinationid sctid
                         :active 1
                         :typeid is-a}))]
    (->> records
         (filter #(concept-active? (:sourceid %)))
         (map :sourceid))))

(defn descendents
  "Returns all the descendent SCTID's for the concept."
  [sctid]
  ; TODO: implement descendents
  )

(defn antecedents
  "Returns all the antecedent SCTID's for the concept."
  [sctid]
  ; TODO: implement antecedents
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
