(ns hs-augsburg.devcards._4_declarative
  (:require
    [reagent.core :as reagent]
    [devcards.core :as dc])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]
    [cljs.core.async.macros
     :refer [go go-loop]]))

(enable-console-print!)


; color counter
(defonce color-counter-state (reagent/atom 0))

(defn color-value-render [number]
  [:span
   {:style {:color
            ; Die richtige Farbe auswählen
            (cond
              (< number 3) "green"
              (< number 6) "#D8D100"
              (< number 200) "red"
              :else "Grey")}}
   "Current count: " number])

(defn color-counter [ratom]
  [:div
   [color-value-render @ratom]
   [:div
    [:button {:on-click #((fn [_] (swap! ratom inc)))} "Increment"]
    [:button {:on-click #((fn [_] (swap! ratom (fn [state] (if (> state 0) (dec state) state)))))} "Decrement"]]])

(defcard-doc "
             ## Declarativer Stile
             - Es wird nicht beschrieben was gemacht werden soll, sonder was seien soll
             - Bei verschienden Werten soll andere Farbe gewält werden
             - Der code ändert nicht die Farbe er beschreibt das element nur abhängig vom Zustand")

(defcard-rg :color-counter-card
            ; function and state
            [color-counter color-counter-state]
            ; state to inspect
            color-counter-state
            {:inspect-data true :history true})

(defcard-doc (mkdn-pprint-source color-value-render))

(defcard-doc "[Nächste Folie](#!/hs_augsburg.devcards._5_channels)")