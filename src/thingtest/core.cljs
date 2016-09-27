(ns thingtest.core
  (:require-macros
   [reagent.ratom :refer [reaction]])
  (:require
   [reagent.core :as reagent]
   [thi.ng.math.core :as m :refer [PI HALF_PI TWO_PI]]
   [thi.ng.geom.core :as g]
   [thi.ng.geom.cuboid :as c]
   [thi.ng.geom.gl.core :as gl]
   [thi.ng.geom.gl.webgl.constants :as glc]
   [thi.ng.geom.gl.buffers :as buf]
   [thi.ng.geom.gl.webgl.animator :as anim]
   [thi.ng.geom.gl.shaders.phong :as phong]
   [thi.ng.geom.gl.shaders :as shd]))

(enable-console-print!)

(defonce app (reagent/atom {:selected 1}))

;; Lesson 1
;;---------------------------------------------------------------------------

(def shader-0-spec
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

(defn init-0
  [this]
  (let [ctx (gl/gl-context (reagent/dom-node this))]
    (let [shader (shd/make-shader-from-spec ctx shader-0-spec)
          spec
          {:attribs {:position {:data (js/Float32Array. [0,1,0
                                                         -1,-1,0
                                                         1,-1,0])
                                :size 3}}
           :mode glc/triangles
           :num-vertices 3}
          ]
      (swap! app assoc
             :ctx ctx
             :spec
             (assoc (gl/make-buffers-in-spec spec ctx glc/static-draw)
                    :shader shader)))))

(defn update-0
  [this]
  (fn [t frame]

    (let [{:keys [ctx shader spec]} @app]

      (when ctx
        (gl/clear-color-buffer ctx 0 1 0 1)
        (gl/draw-with-shader ctx spec)))
    (:active (reagent/state this))))


;; Lesson 2
;;---------------------------------------------------------------------------


(def shader-1-spec
  {:vs
   "
    void main(void) {
        gl_Position = vec4(position, 1.0);
        vColor = vec4(color, 1.0);
    }
    "
   :fs
   "
    void main(void) {
        gl_FragColor = vColor;
    }
   "

   :varying {
             :vColor :vec4
             }
   :attribs {
             :position :vec3
             :color :vec3
             }})

(defn init-1
  [this]
  (let [ctx (gl/gl-context (reagent/dom-node this))]
    (let [shader (shd/make-shader-from-spec ctx shader-1-spec)
          spec
          {:attribs {:position {:data (js/Float32Array. [0, 1,0
                                                        -1,-1,0
                                                         1,-1,0])
                                :size 3}
                     :color {:data (js/Float32Array.
                                    [1, 0, 0
                                     0, 1, 0
                                     0, 0, 1
                                     ])
                             :size 3}}
           :mode glc/triangles
           :num-vertices 3}]
      (swap! app assoc
             :ctx ctx
             :spec
             (assoc (gl/make-buffers-in-spec spec ctx glc/static-draw)
                    :shader shader)))))

(defn update-1
  [this]
  (fn [t frame]
    (let [{:keys [ctx shader spec]} @app]
      (when ctx
        (gl/clear-color-buffer ctx 0 0 0 1)
        (gl/draw-with-shader ctx spec)))
    (:active (reagent/state this))))

;; Common components
;;---------------------------------------------------------------------------

(defn gl-component
  [props]
  (reagent/create-class
   {:component-did-mount
    (fn [this]
      (reagent/set-state this {:active true})
      ((:init props) this)
      (anim/animate ((:loop props) this)))
    :component-will-unmount
    (fn [this]
      (reagent/set-state this {:active false}))
    :reagent-render
    (fn [props]
      [:canvas
       (merge
        {:width (:width props)
         :height (:height props)}
        props)])}))

(defn lesson0 []
  [gl-component {:init init-0 :loop update-0 :width 640 :height 640 :selected (:selected @app)}]
  )

(defn lesson1 []
  [gl-component {:init init-1 :loop update-1 :width 640 :height 640 :selected (:selected @app)}]
  )

(defn app-component
  []
  [:div
   [:h2 "controls"]
   (into [:ul.menu]
         (for [n (range 2)]
           [:li {:class (when (= n (:selected @app)) "active")}
            [:a {:href "#"
                 :on-click (fn [e]
                             (.preventDefault e)
                             (reset! app {:selected n}))}
             (str "Lesson " n)]]))
   [:h4 "Selected: " (:selected @app)]
   [:div (case (:selected @app)
           0 [lesson0]
           1 [lesson1]
           :else
           [:h3 "Unknown."])]])

(defn main
  []
  (reagent/render-component
   [app-component]
   (.getElementById js/document "app")))

(main)

(defn on-js-reload [])
