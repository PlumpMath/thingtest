(ns thingtest.core
  (:require [reagent.core :as reagent]
            [thingtest.data :refer [app]]
            [thingtest.lesson1 :refer [lesson1]]
            [thingtest.lesson2 :refer [lesson2]]
            [thingtest.lesson3 :refer [lesson3]]))

(enable-console-print!)

(defn app-component []
  [:div
   [:h2 "controls"]
   (into [:ul.menu]
         (for [n (range 3)]
           [:li {:class (when (= n (:selected @app)) "active")}
            [:a {:href "#"
                 :on-click (fn [e]
                             (.preventDefault e)
                             (when (not= (:selected @app) n)
                               (reset! app {:selected n})))}
             (str "Lesson " n)]]))
   [:h4 "Selected: " (:selected @app)]
   [:div (case (:selected @app)
           0 [lesson1]
           1 [lesson2]
           2 [lesson3]
           :else
           [:h3 "Unknown."])]])

(defn main []
  (reagent/render-component
   [app-component]
   (.getElementById js/document "app")))

(main)

(defn on-js-reload [])
