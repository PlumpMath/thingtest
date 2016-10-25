(ns thingtest.gl


  (:require [reagent.core :as reagent]
            [thi.ng.geom.gl.webgl.animator :as anim]))


(defn gl-component [props]
  (reagent/create-class
   {:component-did-mount
    (fn [this]
      (reagent/set-state this {:active true})
      ((:init props) this)
      (anim/animate ((:update props) this)))
    :component-will-unmount
    (fn [this]
      (reagent/set-state this {:active false}))
    :reagent-render
    (fn [props]
      [:canvas
        {:width (:width props)
         :height (:height props)}
       ])}))
