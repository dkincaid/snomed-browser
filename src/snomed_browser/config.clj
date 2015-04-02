(ns snomed-browser.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def ^:const ^:private default-environment "local")

(def default-config
  (str (System/getProperty "user.home") "/.snomed-browser"))

(defn load-config [filename]
  (with-open [r (io/reader filename)]
    (edn/read (java.io.PushbackReader. r))))

(defn config-from-file
  "Returns the configuration read from the config file."
  ([config-file]
     (load-config config-file))
  ([] (config-from-file default-config)))

(def settings
  "Default and individual profiles settings set from the env.config.name system property."
  (delay
   (let [default {}
         config {:local {:environment "local"
                         :db-host "localhost"}}
         file-config (config-from-file)
         environment (keyword (or (System/getProperty "env.config.name") default-environment))]
     (merge default (config environment) file-config))))
