(ns thingtest.data
  (:require [reagent.core :as reagent]))

(defonce app (reagent/atom {:selected 0}))
