(ns nlpclojure.one.parallelPrime_test
  (:require [clojure.test :refer :all]
            [nlpclojure.core :refer :all]
            [nlpclojure.one.parallelPrime :refer :all]))


(deftest isPrime-2
  (testing "2, is a prime."
    (is (= true (isPrime 2)))))

(deftest isPrime-3
  (testing "3, is a prime."
    (is (= true (isPrime 3)))))

(deftest isNoPrime-4
  (testing "4, is not a prime."
    (is (= false (isPrime 4)))))

(deftest isPrime-5
  (testing "5, is a prime."
    (is (= true (isPrime 5)))))

(deftest isNoPrime-16
  (testing "16, is not a prime."
    (is (= false (isPrime 16)))))

(deftest isNoPrime-1119
  (testing "89, is not a prime."
    (is (= false (isPrime 1119)))))

(deftest isNoPrime-002
  (testing "1000000000000000002, is a prime."
    (is (= false (isPrime 1000000000000000002)))))

(deftest isPrime-003
  (testing "1000000000000000003, is a prime."
    (is (= true (isPrime 1000000000000000003)))))
