(ns gui.core
  (:require
    [cljfx.api :as fx]
    [java-time :as jt]
    [tea-time :as tt]
    )
  (:import
    [javafx.application Platform]
    [javafx.scene.input KeyCode KeyEvent]
    [javafx.scene.paint Color]
    [javafx.scene.canvas Canvas]
  )
  (:gen-class))

(defn clock-tick [] (swap! *state assoc :clock (jt/local-date-time)))

(def *state
  (atom {:title "App Title"
         :clock (jt/local-date-time)}))

(defmulti event-handler :event/type)

(defmethod event-handler ::update-clock []
           (swap! *state assoc :clock (jt/local-date-time)))

(def default-style
  {:-fx-font [ 32 :sans-serif]})

(defn date-pane [clock]
  {:fx/type :label
   :style default-style
   :text (jt/format "MMM dd yyyy" clock)})

(defn day-of-week-pane [clock]
  {:fx/type :label
   :style default-style
   :text (jt/format "EEEE" clock)}
  )

(defn clock-pane [clock]
  {:fx/type :label
   :style default-style
   :text (jt/format "hh:mm:ss a" clock)})

(defn top-row [clock]
  {:fx/type :h-box
   :v-box/vgrow :always
   :fill-height true
   :style {:-fx-alignment :center}
   :spacing 10
   :children [(day-of-week-pane clock)
              (date-pane clock)]})

(defn bottom-row [clock]
  {:fx/type :h-box
   :v-box/vgrow :always
   :fill-height true
   :style {:-fx-alignment :center}
   :spacing 10
   :children [(clock-pane clock)]})

(defn root-view [{{:keys [clock]} :state}]
  {:fx/type :stage
   :width 800
   :height 480
   :showing true
   :scene {:fx/type :scene
           :root {:fx/type :v-box
                  :children [(top-row clock)
                             (bottom-row clock)]}}})

;(def renderer
;  (fx/create-renderer
;   :middleware (fx/wrap-map-desc assoc :fx/type root-view)))
(def renderer
  (fx/create-renderer
   :middleware (fx/wrap-map-desc (fn [state]
                                   {:fx/type root-view
                                    :state state}))
   :opts {:fx.opt/map-event-handler event-handler}))

(defn -main []
  (Platform/setImplicitExit(true))
  ; if you're in the repl, you have to call these three functions
  (fx/mount-renderer *state renderer)
  (tt/start!) ; start the tea-time thread pool
  (tt/every! 1 (bound-fn [] (clock-tick))) ; update the clock every second
  (println "Hello, World!"))
