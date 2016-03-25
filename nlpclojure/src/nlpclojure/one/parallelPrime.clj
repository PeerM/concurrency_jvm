(ns nlpclojure.one.parallelPrime)
;;(:require [clojure.math.numeric-tower :as math]))



(defn isPrime [number] (cond
                         (< number 2) false
                         (= number 2) true
                         (= (mod number 2) 0) false
                         #(true)
                         (let [checkRange (range 3 (/ (+ (Math/sqrt number) 1) 2) 2)]
                           (not (some
                                  (fn [divider] (= 0 (mod number divider)))
                                  checkRange)))))
