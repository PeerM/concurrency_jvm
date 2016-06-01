(defproject nlp_frp "1.0.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [reagent "0.5.1"]
                 [com.cemerick/piggieback "0.2.1"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [org.clojure/clojurescript "1.8.51"]
                 [devcards "0.2.1-6"]
                 [hiccup "1.0.5"]
                 [cljsjs/react "0.14.3-0"]
                 [cljsjs/react-dom "0.14.3-1"]
                 [cljsjs/react-dom-server "0.14.3-0"]
                 [org.clojure/core.async "0.2.374"]
                 [cljs-http "0.1.40"]]

  :plugins [[lein-figwheel "0.5.2"]
            [lein-cljsbuild "1.1.3" :exclusions [org.clojure/clojure]]]

  ;:aot  [dvap-gruppe11.seesaw.counter]
  ;:main dvap-gruppe11.seesaw.counter

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :cljsbuild {
              :builds [{:id           "devcards"
                        :source-paths ["src"]
                        :figwheel     {:devcards true}      ;; <- note this
                        :compiler     {:main                 "hs_augsburg.devcards.cards-root"
                                       :asset-path           "js/compiled/devcards_out"
                                       :output-to            "resources/public/js/compiled/devcards_trail_devcards.js"
                                       :output-dir           "resources/public/js/compiled/devcards_out"
                                       :source-map-timestamp true}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:main                 "hs_augsburg.devcards.stand_alone"
                                       :devcards             true
                                       :asset-path           "js/compiled/devcards_out"
                                       :output-to            "resources/public/js/compiled/devcards_trail_devcards.js"
                                       :optimizations        :advanced
                                       :source-map-timestamp true}}]}
  ;{:id "dev"
  ; :source-paths ["src"]
  ; :figwheel true
  ; :compiler {:main       "devcards_trail.core"
  ;            :asset-path "js/compiled/out"
  ;            :output-to  "resources/public/js/compiled/devcards_trail.js"
  ;            :output-dir "resources/public/js/compiled/out"
  ;            :source-map-timestamp true}}]}
  ;{:id "prod"
  ; :source-paths ["src"]
  ; :compiler {:main       "devcards_trail.core"
  ;            :asset-path "js/compiled/out"
  ;            :output-to  "resources/public/js/compiled/devcards_trail.js"
  ;            :optimizations :advanced}}]}

  :figwheel {:css-dirs         ["resources/public/css"]
             :nrepl-port       7002
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]})