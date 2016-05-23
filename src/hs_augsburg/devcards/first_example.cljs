(ns hs_augsburg.devcards.first_example
  (:require
    [reagent.core :as reagent]
    [cljs.core.async :as coreasync :refer [put! chan <! take!]]
    [devcards.core :as dc])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest]]
    [cljs.core.async.macros
     :refer [go go-loop]]))

(enable-console-print!)

(defonce color-counter-state (reagent/atom 0))

; color counter
(defn color-counter [ratom]
  [:div {:style {:color (cond
                          (< @ratom 3) "green"
                          (< @ratom 6) "#D8D100"
                          (< @ratom 200) "red"
                          :else "Grey")}} "Current count: " @ratom
   [:div
    [:button {:on-click #((fn [_] (swap! ratom inc)))} "Increment"]
    [:button {:on-click #((fn [_] (swap! ratom (fn [state] (if (> state 0) (dec state) state)))))} "Decrement"]]])

(defcard-rg :color-counter
            ; function and state
            [color-counter color-counter-state]
            ; state to inspect
            color-counter-state
            {:inspect-data true :history true})

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