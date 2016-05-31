(ns hs_augsburg.devcards.first_example
  (:require
    [reagent.core :as reagent]
    [cljs.core.async :as coreasync :refer [put! chan <! take!]]
    [devcards.core :as dc]
    [cljs-http.client :as http])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest]]
    [cljs.core.async.macros
     :refer [go go-loop]]))

(enable-console-print!)





; basic counter
(defonce basic-counter-state (reagent/atom 0))

(defn basic-counter [ratom] [:button {:on-click #((fn [_] (swap! ratom inc)))} (str "Wert = " @ratom)])

(defcard-rg :basic-counter
            ; function and state
            [basic-counter basic-counter-state])





; color counter
(defonce color-counter-state (reagent/atom 0))

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





(defonce seconds-past (reagent/atom 0))

(defonce timer-channel (let [channel (chan 4)]
                         (js/setInterval #(put! channel :tick) 1000)
                         channel))
(go (loop [i 0]
      (<! timer-channel)
      (swap! seconds-past inc)
      ;(reset! seconds-past i)
      (recur (+ i 1))))

(defn timer-view [atom] [:p "Vergangen Zeit " [:em @atom]])

(defcard-rg :timer
            [timer-view seconds-past])

;request

(defonce git-state (reagent/atom {:search-term "" :state :inital :response {}}))
(defonce search-term-changes (chan))
(defonce send-actions (chan))

(go (loop [i 0]
      (let [username (<! search-term-changes)]
        (swap! git-state (fn [old] (assoc old :search-term username))))
      (recur (+ 1 i))))

(go (loop [i 0]
      (<! send-actions)
      (let [response-chan (http/get "https://api.github.com/search/repositories"
                                    {:with-credentials? false,
                                     :query-params      {"q" (:search-term @git-state)}})]
        (swap! git-state (fn [old] (assoc old :state :loading)))
        (let [response (<! response-chan)]
          (swap! git-state (fn [old] (assoc old :response response :state :finished)))))
      (recur (+ 1 i))))


(defn git-view [atom] [:div [:input {
                                     :type      "text", :placeholder "github search term", :value (:search-term @atom),
                                     :on-change (fn [ev] (put! search-term-changes (-> ev .-target .-value)))}]
                       [:button {:on-click #(put! send-actions :send)} "send"]
                       [:div
                        (let [state (:state @git-state)]
                          (cond
                            (= state :inital) "no data avalible"
                            (= state :loading) "loading"
                            (= state :finished) [:ul
                                                 (map
                                                   (fn [i] [:li {:key i} i])
                                                   (map
                                                     #(:html_url %)
                                                     (take 5 (:items (:body (:response @git-state))))))]))]])


(defcard-rg :git-card
            [git-view git-state]
            git-state {:history true})


; robust calculator
(defonce robust-calculator-state (reagent/atom {:text "1+1" :result "2" :error "" :live_update true}))
(defonce calc-chan (chan))

(defn evaluate [new_input] (try [true (js/eval (str new_input ";"))] (catch js/Error e [false (str e)])))
(go (while true
      (let [message (<! calc-chan) new_input (:value message)]
        (print message)
        (if (or (:live_update @robust-calculator-state) (:submited message))
          (let [res (evaluate new_input)]
            (if (get res 0)
              (swap! robust-calculator-state (fn [old] (assoc old :text new_input :result (res 1) :error "")))
              (swap! robust-calculator-state (fn [old] (assoc old :error (res 1) :text new_input)))))
          (swap! robust-calculator-state (fn [old] (assoc old :text new_input)))))))

(defn put-change-message [ev submited] (put! calc-chan {:value (-> ev .-target .-value) :submited submited}))
(defn robust-calculator [ratom]
  [:div {:class "full-width robust-calculator"}
   [:div
    [:form
     [:label {:for "live_update"} "Live update?"]
     [:input {:name      "live_update"
              :type      "checkbox"
              :checked   (:live_update @robust-calculator-state)
              :on-change (fn [ev] (swap! robust-calculator-state (fn [state] (assoc state :live_update (-> ev .-target .-checked)))))}]] ;(fn [ev] (swap! robust-calculator-state ((fn [state] (assoc state :live_update false)))))}]]
    [:form
     [:input {:type      "text"
              :class     "text_input"
              :value     (:text @ratom)
              :on-change (fn [ev] (put-change-message ev false))}]]]
   ;[:a {:on-click (put! calc-chan {:value (:text @ratom) :submited true})} "Update"]]
   [:div [:input {:type "text" :class "text_input" :read-only true :value (str "=" (:result @ratom))}]]
   [:div {:class "error" :hidden (empty? (:error @ratom))} [:span (:error @ratom)]]])

(defcard-rg :robust-calculator-card
            [robust-calculator robust-calculator-state]
            robust-calculator-state {:inspect-data true :history true})