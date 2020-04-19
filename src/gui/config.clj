(ns gui.config
    (:require [cprop.core :refer [load-config]]
              [cprop.source :as source]
              [java-time :as jt]
              [mount.core :refer [args defstate]]))

(defstate env
  :start
  (load-config
    :merge
   [{:shelter-start-date (jt/local-date 2020 03 12)}
     (args)
    (source/from-system-props)
    (source/from-env)]))