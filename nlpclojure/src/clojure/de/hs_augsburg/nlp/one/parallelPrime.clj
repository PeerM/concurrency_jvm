(ns de.hs_augsburg.nlp.one.parallelPrime
  (:import (de.hs_augsburg.nlp.one PrimeChecker)))

;;(def interval (Math/pow 10 8))
;; long q = (Math.round(Math.sqrt(number) + 1) / 1000000L);
;; return Math.max(q, 500);
(defn longMax [^long a ^long b] (Math/max a b))
(defn calcInterval [^long number]
  (longMax
    10
    (/
      (+ 1
        (Math/round
          (Math/sqrt number)))
      1000)))

(defn nextSeqment [number, inter, [_ prevEnd]]
  (let [nextEnd (+ prevEnd inter)]
    (if
      (> (* nextEnd nextEnd) number)
      [prevEnd, (+ 1 (Math/round (Math/sqrt number)))]
      [prevEnd nextEnd])))

(defn isPrime [number] (cond
                         (< number 2) false
                         (= number 2) true
                         (= (mod number 2) 0) false
                         true
                         (let [interval (calcInterval number)]
                           (not
                             (->>
                               (iterate
                                 (fn [task] (nextSeqment number interval task))
                                 (nextSeqment number interval [0 3]))
                               (take-while (fn [[start _]] (<= (* start start) number)))
                               (pmap
                                 (fn [[start end]] (PrimeChecker/doesRangeContainDivider start end number)))
                               (some
                                 true?))))))
