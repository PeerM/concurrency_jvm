(ns hs-augsburg.devcards._6_git_api
  (:require
    [reagent.core :as reagent]
    [cljs.core.async :as coreasync :refer [put! chan <! take!]]
    [devcards.core :as dc]
    [cljs-http.client :as http])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]
    [cljs.core.async.macros
     :refer [go go-loop]]))

;request

(defonce git-state (reagent/atom {:search-term "" :state :inital :response {}}))
(defonce search-term-changes (chan))
(defonce send-actions (chan))

(go (loop [i 0]
      (let [username (<! search-term-changes)]
        (swap! git-state (fn [old] (assoc old :search-term username))))
      (recur (+ 1 i))))

(defn git-statemachine! []
  (go (loop [i 0]
        ; Auf Button warten
        (<! send-actions)
        ; Request starten
        (let [response-chan (http/get "https://api.github.com/search/repositories"
                                      {:with-credentials? false,
                                       :query-params      {"q" (:search-term @git-state)}})]
          ; Zustand auf :loading stellen
          (swap! git-state (fn [old] (assoc old :state :loading)))
          ; Auf antwort warten
          (let [response (<! response-chan)]
            ; Zustand auf :finished stellen und antwort speichern
            (swap! git-state (fn [old] (assoc old, :response response, :state :finished)))))
        (recur (+ 1 i)))))
(git-statemachine!)

(defn git-view [atom] [:div
                       [:input {
                                :type      "text", :placeholder "github search term", :value (:search-term @atom),
                                :on-change (fn [ev] (put! search-term-changes (-> ev .-target .-value)))}]
                       [:button {:on-click #(put! send-actions :send)} "send"]
                       [:div
                        (let [state (:state @git-state)]
                          (cond
                            (= state :inital) "no data avalible"),
                            (= state :loading) "loading",
                            (= state :finished)
                            [:ul
                             (map
                               (fn [i] [:li {:key i} i])
                               (map
                                 #(:html_url %)
                                 (take 5 (:items (:body (:response @git-state))))))],)]])


(defcard-doc "
             ## Channels für Automaten
             - Viele Quellen für Ereignisse
             - Modellierung von Prozeduren mit Endlichen Automaten
             - Beispiel durchsuchen von GitHub
               - Zustand :inital
               - 'Send' Button löst suche aus
               - Request wird gemacht
               - Zustand :loading
               - Request ist fertig
               - Zustand :finished und daten von api sind vorhanden
               - Button kann die nächste suche auslösen")

(defcard-rg :git-card
            [git-view git-state]
            git-state {:inspect-data false :history true})

(defcard-doc
  (mkdn-pprint-source git-state)
  (mkdn-pprint-source git-statemachine!)
  (mkdn-pprint-source git-view))

(defcard-doc "[Optionale letzte Folie](#!/hs_augsburg.devcards._7_calc)")