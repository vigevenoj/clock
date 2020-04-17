(defproject gui "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :plugins [[io.aviso/pretty "0.1.37"]]
  :middleware [io.aviso.lein-pretty/inject]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [cljfx "1.6.3"]
                 [slingshot "0.12.2"]
                 ;; readability things
                 [io.aviso/pretty "0.1.37"]
                 [expound "0.7.2"]
                 ;; end rt
                 [clojure.java-time "0.3.2"]
                 [tea-time "1.0.1"]
                 [org.clojure/core.cache "1.0.207"]
                 ]
  :main ^:skip-aot gui.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :injections [(javafx.application.Platform/exit)]
                       }})
