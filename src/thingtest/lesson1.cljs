(ns thingtest.lesson1
  (:require [reagent.core :as reagent]
            [thi.ng.geom.gl.core :as gl]
            [thi.ng.geom.gl.shaders :as shd]
            [thi.ng.geom.gl.webgl.constants :as glc]
            [thi.ng.typedarrays.core :as ta]
            [thingtest.data :refer [app]]
            [thingtest.gl :refer [gl-component]]))

;; Lesson 1
;;---------------------------------------------------------------------------

(def shader-spec
  {:vs
   "
    void main(void) {
        gl_Position = vec4(position, 1.0);
    }
    "
   :fs
   "
    void main(void) {
        gl_FragColor = vec4(color, 1);
    }
   "
   :uniforms {:color [:vec3 [1 1 0]]}
   :attribs {:position :vec3}})

(def props
  {:width 640
   :height 640
   :init
   (fn
     [this]
     (let [ctx (gl/gl-context (reagent/dom-node this))
           shader (shd/make-shader-from-spec ctx shader-spec)
           spec
           {:attribs {:position {:data (ta/float32 [0,1,0
                                                    -1,-1,0
                                                    1,-1,0])
                                 :size 3}}
            :mode glc/triangles
            :num-vertices 3}
           spec-prepared (assoc (gl/make-buffers-in-spec spec ctx glc/static-draw)
                                :shader shader)]
         (swap! app assoc
                :ctx ctx
                :spec spec-prepared)))

   :update
   (fn
     [this]
     (fn [t frame]
       (let [{:keys [ctx shader spec]} @app]
         (when ctx
           (gl/clear-color-buffer ctx 0 1 0 1)
           (gl/draw-with-shader ctx spec)))
       (:active (reagent/state this))))
   })

(defn lesson1 []
  [gl-component props])
