(ns nlpclojure.one.primePlayground
  (:require
            [nlpclojure.one.parallelPrime :refer :all]))

(time (isPrime 1000000000000000003))