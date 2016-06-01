(ns hs-augsburg.devcards._5_channels
  (:require
    [reagent.core :as reagent]
    [cljs.core.async :as coreasync :refer [put! chan <! take!]]
    [devcards.core :as dc]
    [cljs-http.client :as http])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]
    [cljs.core.async.macros
     :refer [go go-loop]]))


(defcard-doc "
## Nebenläufigkeit
- core.async channels:
  - queues
  - green threads oder echte threads
  - async await style
- Channels verwenden um Zustands änderungen zu managen
  - sequentielle abarbeitung wie bei blocken
  - übersetztung zu javascript nebenläufigkeit
  - Channel input kann von überall kommen, auch von callbacks")

(defonce seconds-past (reagent/atom 0))

(defonce timer-channel
         ; channel erstellen
         (let [channel (chan 4)]
           ; js callback das jede Sekunde aufgerufen wird registrieren
           (js/setInterval #(put! channel :tick) 1000)
           channel))

(defn listen-to-channel! []
  ; go block
  (go
    ; endloss schleife
    (loop [i 0]
      ; auf das näschste :tick warten
      (<! timer-channel)
      ; die zahl um eins erhöhen
      (swap! seconds-past inc)
      ; wieder von loop anfangen
      (recur (+ i 1)))))

(listen-to-channel!)

(defn timer-view [atom] [:p "Vergangen Zeit " [:em @atom]])

(defcard-rg :timer
            [timer-view seconds-past])

(defcard-doc (mkdn-pprint-source timer-channel))
(defcard-doc (mkdn-pprint-source listen-to-channel!))

(defcard-doc "[Nächste Folie](#!/hs_augsburg.devcards._6_git_api)")