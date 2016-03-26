(ns de.hs_augsburg.nlp.one.parallelPrime
  (:import (de.hs_augsburg.nlp.one PrimeChecker)))

(def interval (Math/pow 10 8))


(defn nextSeqment [number, inter, [prevStart prevEnd]]
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
                         (not
                           (->>
                             (iterate
                               (fn [task] (nextSeqment number interval task))
                               (nextSeqment number interval [0 3]))
                             (take-while (fn [[start _]] (<= (* start start) number)))
                             (pmap
                               (fn [[start end]] (PrimeChecker/doesRangeContainDivider start end number)))
                             (some
                               true?)))))
