(defproject snomed-browser "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [korma "0.4.0"]]
  :main ^:skip-aot snomed-browser.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
