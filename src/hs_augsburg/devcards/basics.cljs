(ns hs-augsburg.devcards.basics
  (:require
    [reagent.core :as reagent]
    [cljs.core.async :as coreasync :refer [put! chan <! take!]]
    [devcards.core :as dc])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]))

(defcard :start [:div
                 [:p "Mit einer DSL kann hmtl geschrieben werden"]
                 [:div [:button "Ein Button"]]
                 [:p "Es ist keine Kompilieren nötig wie bei jsx"]])

(defcard-rg :dsl
            [:div
             [:p "Mit einer DSL kann hmtl geschrieben werden"]
             [:div [:button "Ein Button"]]
             [:p "Es ist keine Kompilieren nötig wie bei jsx"]])

(defn list-example [] [:p]
  "Die dsl besteht aus normalen Clojure maps, also kann man dinge wie map verwenden"
  [:ul (map (fn [i] [:li {:key i} i] (range 5 10)))])

(defcard-rg :dsl2 "text" (reagent/as-element [list-example]))

(defcard-doc "source code:" (mkdn-pprint-source list-example))
