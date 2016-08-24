(ns hs-augsburg.devcards._1_intro
  (:require
    [devcards.core :as dc])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]))

(defcard-doc "
# \"Functional Reactive Programming\" mit Clojure
## Motivation
- GUIs haben nebenläufigkeit
- Javascript hat 'nur' Callbacks und bald Futures
- DOM ist shared mutable State
## Clojure
- ist ein Lisp
- läuft auf der JVM, aber auch JavaScript
- simple made easy
- functional wenn mans will
## Grundbausteine aus Clojure
- Atoms mit Watchers
- core.async channels
- Funktionen mit und ohne Nebenwirkungen
- Immutable Datenstrukturen")

(defcard-doc "## Clojure zu Javascript")

(defcard-doc "
Die Präsentation ist auch auf [www.hs-augsburg.de/~mathiaf/nlp-frp/](http://www.hs-augsburg.de/~mathiaf/nlp-frp/)

Sourcecode [https://r-n-d.informatik.hs-augsburg.de:8080/ferdinandpeer.mathia/nlp_mathia_reichinger/tree/frp-present](https://r-n-d.informatik.hs-augsburg.de:8080/ferdinandpeer.mathia/nlp_mathia_reichinger/tree/frp-present)

[Nächste Folie](#!/hs_augsburg.devcards._2_html)")