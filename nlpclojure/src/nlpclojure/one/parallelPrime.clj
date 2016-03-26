(ns nlpclojure.one.parallelPrime
  (:import (de.hs_augsburg.nlp.one Task TaskedPrimeCheck)))

(def interval (Math/pow 10 8))


;public static Task nextTask(Task prev, Long interval) {
;                                                       long nextEnd = prev.end + interval;
;                                                       long endSquared = nextEnd * nextEnd;
;                                                       if (endSquared > prev.number) {
;                                                                                      nextEnd = Math.round(Math.sqrt(prev.number) + 1);
;                                                                                      }
;                                                            return new Task(prev.end, nextEnd, prev.number);
;                                                       ;}

(defn nextSeqment [number, inter, [prevStart prevEnd]]
  (let [nextEnd (+ prevEnd inter)]
    (if
      (> (* nextEnd nextEnd) number)
      [prevEnd, (+ 1 (Math/round (Math/sqrt number)))]
      [prevEnd nextEnd])))

(defn newTask [start end number] (Task/construct start end number))

(defn isPrime [number] (cond
                         (< number 2) false
                         (= number 2) true
                         (= (mod number 2) 0) false
                         true
                         (not
                           (->>
                             (iterate
                               (fn [task] (nextSeqment number interval task))
                               [3 (+ 3 interval)])
                             (take-while (fn [[start _]] (<= (* start start) number)))
                             (map (fn [[start end]] (newTask start end number)))
                             (pmap
                               (fn [t] (TaskedPrimeCheck/dividerInTask t)))
                             (some
                               true?)))))
