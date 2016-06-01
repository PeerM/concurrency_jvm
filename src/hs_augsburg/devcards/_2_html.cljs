(ns hs-augsburg.devcards._2_html
  (:require
    [reagent.core :as reagent]
    [cljs.core.async :as coreasync :refer [put! chan <! take!]]
    [devcards.core :as dc])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]))

(defn first_card [] [:div
                     [:p "DSL für hmtl"]
                     [:div [:button
                            {:on-click (fn [ev] (js/alert "Der Button wurde gedrückt"))}
                            "Ein Button"]]
                     [:p "Clojurescript wird javascript kompiliert (serperater dialekt wie jsx wird vermiden)"]
                     [:p {:style {:text-decoration "underline"}} "Inline CSS und andere attribute"]])

(defcard-rg :dsl [first_card])
(defcard-doc "source code:" (mkdn-pprint-source first_card))

(defn list-example [start end] [:ul (map (fn [i] [:li {:key i} i]) (filter even? (range start end)))])

(defcard-rg :dsl2 "Die dsl besteht aus normalen Clojure maps, also kann man dinge wie map verwenden" [list-example 5 15])

(defcard-doc "source code:" (mkdn-pprint-source list-example))

(defn compose-example [] [:div "componenten wie functionen kombinieren" [list-example 2 9]])

(defcard-rg :dsl3 "" [compose-example])

(defcard-doc "source code:" (mkdn-pprint-source compose-example))

(defcard-doc "[Nächste Folie](#!/hs_augsburg.devcards.functional_reacitve)")