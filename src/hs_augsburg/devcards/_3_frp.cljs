(ns hs-augsburg.devcards._3_frp
  (:require
    [reagent.core :as reagent]
    [cljs.core.async :as coreasync :refer [put! chan <! take!]]
    [devcards.core :as dc]
    [cljs-http.client :as http])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]
    [cljs.core.async.macros
     :refer [go go-loop]]))

(enable-console-print!)



(defcard-doc "# Grundidee mit Functional Reactive Programming")

; basic counter
(defonce basic-counter-state (reagent/atom 0))

(defn basic-counter [ratom] [:button
                             {:on-click #((fn [_] (swap! ratom inc)))}
                             (str "Wert = " @ratom)])

(defcard-rg :basic-counter
            ; function and state
            [basic-counter basic-counter-state])


(defcard-doc "
             - Reagent Bibliothek basiert auf React.js
             - Reagent Atom mit Selbem Interface wie das normale Atom"
             (mkdn-pprint-source basic-counter-state)
             "
             - Der Zustand der Anwendung steckt in einem Atom
             - Eine Funktion die das html vom Zustand ableitet"
             (mkdn-pprint-source basic-counter)
             "
             - Bei Änderungen,im Atom, wird die Funktion neu aufgeruffen")

(defcard-doc "[Nächste Folie](#!/hs_augsburg.devcards._4_declarative)")