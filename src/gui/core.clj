(ns gui.core
  (:require
    [cljfx.api :as fx]
    [clojure.tools.logging :as log]
    [gui.config :refer [env]]
    [java-time :as jt]
    [mount.core :as mount]
    [tea-time.core :as tt])
  (:import
    [javafx.application Platform]
    [javafx.scene.input KeyCode KeyEvent]
    [javafx.scene.paint Color]
    [javafx.scene.canvas Canvas])
  (:gen-class))

(def *state
  (atom {:title "Quarantine Clock"
         :clock (jt/local-date-time)}))

(defn clock-tick [] (swap! *state assoc :clock (jt/local-date-time)))

(defn days-in-quarantine []
  (jt/time-between (:quarantine-start env) (jt/local-date) :days))

(defmulti event-handler :event/type)

(defmethod event-handler ::update-clock []
           (swap! *state assoc :clock (jt/local-date-time)))

(def default-style
  {:-fx-font [ 64 :sans-serif]})

(defn date-pane
  "Display the current date "
  [clock]
  {:fx/type :label
   :style default-style
   :text (jt/format "MMMM dd yyyy" clock)})

(defn day-of-week-pane
  "Display the current day of week"
  [clock]
  {:fx/type :label
   :style default-style
   :text (jt/format "EEEE" clock)}
  )

(defn clock-pane
  "Display a clock with AM/PM"
  [clock]
  {:fx/type :label
   :style default-style
   :text (jt/format "hh:mm:ss" clock)})

(defn am-pm-pane
  "Display if the current time is AM or PM"
  [clock]
  {:fx/type :label
   :style default-style
   :text (jt/format "a" clock)})

(defn top-row
  "Configuration for top row of display. This includes the day of week and date"
  [clock]
  {:fx/type :h-box
   :v-box/vgrow :always
   :fill-height true
   :style {:-fx-alignment :center}
   :spacing 10
   :children [(day-of-week-pane clock)
              (date-pane clock)]})

(defn middle-row
  "Configuration for middle row of display, how long since we sheltered in place."
  []
  {:fx/type :h-box
   :v-box/vgrow :always
   :fill-height true
   :style {:-fx-alignment :center}
   :spacing 10
   :children [{:fx/type :label
               :text (str (days-in-quarantine) " days in quarantine") }]})

(defn bottom-row
  "Configuration for bottom row of display. This includes the clock."
  [clock]
  {:fx/type :h-box
   :v-box/vgrow :always
   :fill-height true
   :style {:-fx-alignment :center}
   :spacing 10
   :children [(clock-pane clock)
              (am-pm-pane clock)]})

(defn semicircle [centerx centery radius inner-radius bgcolor stroke-color]
  (let [move-to {:fx/type :move-to
                 :x (+ centerx inner-radius)
                 :y centery}
        inner-arc {:fx/type :arc-to
                   :x (- centerx inner-radius)
                   :y centery
                   :radius-x inner-radius
                   :radius-y inner-radius}
        move-to-2 {:fx/type :move-to
                   :x (- centerx inner-radius)
                   :y centery}
        hline-to-right-leg {:fx/type :h-line-to
                            :x (+ centerx radius)}
        outer-arc {:fx/type :arc-to
                   :x (- centerx radius)
                   :y centery
                   :radius-x radius
                   :radius-y radius}
        hline-to-left-leg {:fx/type :h-line-to
                           :x (- centerx inner-radius)}
        path {:fx/type   :path
              :fill      bgcolor
              :stroke    stroke-color
              :fill-rule :even-odd
              :elements [move-to inner-arc move-to-2 hline-to-right-leg outer-arc hline-to-right-leg]}
        ]
    path ))

(defn root-view
  "This defines the root view"
  [{{:keys [clock]} :state}]
  {:fx/type :stage
   :width 800
   :height 480
   :showing true
   :style (:decorated env) ; default is :decorated, :undecorated removes the window chrome
   :scene {:fx/type :scene
           :root {:fx/type :v-box
                  :children [
                              (semicircle 120 120 100 50 :lightgreen :transparent)
;                              (top-row clock)
;                             (middle-row)
;                             (bottom-row clock)
                              ]}}})

(def renderer
  (fx/create-renderer
   :middleware (fx/wrap-map-desc (fn [state]
                                   {:fx/type root-view
                                    :state state}))
   :opts {:fx.opt/map-event-handler event-handler}))

(defn startup
  "Utility method to start things up"
  ; if you are in the repl, run this manually to create the window
  []
  (do
    (fx/mount-renderer *state renderer)
    (tt/start!) ; start the tea-time thread pool
    (tt/every! 1 (bound-fn [] (clock-tick))) )); update the clock every second

(defn -main []
  (Platform/setImplicitExit(true))
  (mount/start #'gui.config/env)
  (startup)
  (println "Hello, World!"))
