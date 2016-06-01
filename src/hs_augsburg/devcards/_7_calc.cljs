(ns hs_augsburg.devcards._7_calc
  (:require
    [reagent.core :as reagent]
    [cljs.core.async :as coreasync :refer [put! chan <! take!]]
    [devcards.core :as dc]
    [cljs-http.client :as http])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]
    [cljs.core.async.macros
     :refer [go go-loop]]))


; robust calculator
(defonce robust-calculator-state (reagent/atom {:text "1+1" :result "2" :error ""}))
(defonce calc-chan (chan))
(go (while true
      (let [new_input (<! calc-chan) res (try [true (js/eval (str new_input ";"))] (catch js/Error e [false (str e)]))]
        (if (res 0)
          (reset! robust-calculator-state {:text new_input :result (res 1) :error ""})
          (swap! robust-calculator-state (fn [old] (assoc old :error (res 1) :text new_input)))))))


(defn robust-calculator [ratom]
  [:div {:class "full-width robust-calculator"}
   [:div
    [:input {:type      "text"
             :value     (:text @ratom)
             :on-change (fn [ev] (put! calc-chan (-> ev .-target .-value)))}]]
   [:div [:input {:type "text" :read-only true :value (str "=" (:result @ratom))}]]
   [:div {:class "error" :hidden (empty? (:error @ratom))} [:span (:error @ratom)]]])

(defcard-rg :robust-calculator-card
            [robust-calculator robust-calculator-state]
            robust-calculator-state {:inspect-data true :history true})