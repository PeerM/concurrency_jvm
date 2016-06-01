(ns hs-augsburg.devcards._1_intro
  (:require
    [devcards.core :as dc])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]))

(defcard-doc "
## Motivation
- GUI haben nebenläufigkeit
- Javascript hat 'nur' Callbacks und bald Futures
- DOM ist shared mutable State
## Grundbausteine aus Clojure
- Atoms mit Watches
- core.async channels:
  - queues
  - green threads oder echte threads
  - async await style
  - Compatible mit Clojure script
- Funktionen mit und ohne Nebenwirkungen")

(defcard-doc "## Ansatz: wir benutzen die werkzeuge von Clojure um Web GUIs zu bauen
=> Clojure wird zu Javascript compiliert")

(defcard-doc "
Die präsentation ist auch auf [www.hs-augsburg.de/~mathiaf/nlp-frp/](http://www.hs-augsburg.de/~mathiaf/nlp-frp/)

Sourcecode [https://r-n-d.informatik.hs-augsburg.de:8080/ferdinandpeer.mathia/nlp_mathia_reichinger/tree/frp-present](https://r-n-d.informatik.hs-augsburg.de:8080/ferdinandpeer.mathia/nlp_mathia_reichinger/tree/frp-present)

[Nächste Folie](#!/hs_augsburg.devcards._2_html)")