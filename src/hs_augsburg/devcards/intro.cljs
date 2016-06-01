(ns hs-augsburg.devcards.intro
  (:require)
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]))

(defcard-doc "
# Motivation
- GUI haben nebenläufigkeit
- Javascript hat 'nur' Callbacks und bald Futures
- DOM ist shared mutable State")

(defcard-doc "
# Grundbausteine aus Clojure
- Atoms mit Watchers
- core.async channels:
  - queuse
  - green threads oder echte threads
  - async await style
- Pure functions")

(defcard-doc "## Ansatz wir benutzen die werkzeuge von Clojure um Web GUIs zu bauen")

(defcard-doc "[Nächste Folie](#!/hs_augsburg.devcards.basics)")