(ns hs_augsburg.devcards._8_further_reading
  (:require
    [devcards.core :as dc])
  (:require-macros
    [devcards.core :refer [defcard defcard-rg defcard-doc deftest mkdn-pprint-source]]))

(defcard-doc "
# Elm und die Elm Architecture
- für die strukturirung von \"Steuerelementen\"
 - alternativ Demand Driven architecture und Functional Lenses
- weniger lisp und mehr haskell
- strukturelle typen
- pure functionale programirung
 - keine nebeneffekte
 - erschwärt javascript interop)
- nicht stabil
- [Counter](https://github.com/evancz/elm-architecture-tutorial/blob/master/nesting/Counter.elm)
- [Counter-Pair](https://github.com/evancz/elm-architecture-tutorial/blob/master/nesting/1-counter-pair.elm)
")

(defcard-doc "
# Quellen
- [react](https://facebook.github.io/react/)
- [reagent](https://github.com/reagent-project/reagent)
- [re-frame](https://github.com/Day8/re-frame)
- [cljs-http](https://github.com/r0man/cljs-http)
- [the elm architecture](http://guide.elm-lang.org/architecture/index.html)
- [figwheel](https://github.com/bhauman/lein-figwheel) und [devcards](https://github.com/bhauman/devcards)
- [om next](https://github.com/omcljs/om/wiki/Quick-Start-(om.next)#client-server-architecture) (demand driven architecture)
")