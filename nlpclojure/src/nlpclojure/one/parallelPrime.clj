(ns nlpclojure.one.parallelPrime)
;;(:require [clojure.math.numeric-tower :as math]))

(def interval (Math/pow 10 8))

(defn isPrime [number] (cond
                         (< number 2) false
                         (= number 2) true
                         (= (mod number 2) 0) false
                         true
                         (not
                           (->>
                             (iterate
                               (fn [[_ end]] [end (+ end interval)])
                               [3 (+ 3 interval)])
                             (take-while (fn [[start end]] (<= (* start start) number)))
                             (pmap
                               (fn [[start end]] (->>
                                                   (range start end 2)
                                                   (take-while
                                                     (fn [i] (<= (* i i) number)))
                                                   (some
                                                     (fn [divider] (= 0 (mod number divider)))))))
                             (some
                               true?)))))
