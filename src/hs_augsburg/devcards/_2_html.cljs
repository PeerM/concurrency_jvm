(ns hs-augsburg.devcards._2_html
  (:require
    [devcards.core :as dc])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]))

(defn first_card [] [:div
                     [:ul
                      [:li "Domain Specific Language für hmtl"]
                      [:li [:button
                             {:on-click (fn [ev] (js/alert "Der Button wurde gedrückt"))}
                             "Ein Button"]]
                      [:li "Separater Dialekt wie jsx oder cshtml wird vermieden"]
                      [:li {:style {:text-decoration "underline"}} "Inline CSS und andere attribute möglich"]]])

(defcard-rg :dsl [first_card])
(defcard-doc "source code:" (mkdn-pprint-source first_card))

(defn list-example [start end] [:ul
                                (map
                                  (fn [zahl] [:li {:key zahl} zahl])
                                  (range start end))])

(defcard-rg :dsl2 "DSL besteht aus normalen Clojure Maps => map reduce" [list-example 5 10])

(defcard-doc "source code:" (mkdn-pprint-source list-example))

(defn compose-example [] [:div "Componenten wie Funktionen kombinierbar" [list-example 2 6]])

(defcard-rg :dsl3 "" [compose-example])

(defcard-doc "source code:" (mkdn-pprint-source compose-example))

(defcard-doc "[Nächste Folie](#!/hs_augsburg.devcards._3_frp)")