(ns de.hs_augsburg.nlp.one.primePlayground
  (:require
            [de.hs_augsburg.nlp.one.parallelPrime :refer :all]))

;;(time (isPrime 1000000000000000003))
;;(print (str "1117 should be a prime and it is :" (isPrime 1117)))

(time (doall(map isPrime (range 1000000000000000000 1000000000000000005))))