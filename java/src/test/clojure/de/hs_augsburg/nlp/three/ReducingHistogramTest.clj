(ns de.hs-augsburg.nlp.three.ReducingHistogramTest
  (:use de.hs-augsburg.nlp.three.ReducingHistogram clojure.test))

(def test-pixels (int-array [0x000001 0x000002 0x000003 0x000004 0x000005 0x000006 0x000007]))


(deftest segment-size-test (is (= (segment-size test-pixels 3) 2)))
(deftest segments-test (testing "when a pixels do not defide evenly into segments make a last segment with the rest"
                         (is (= (segments test-pixels 3) [{:start 0, :end 2} {:start 2, :end 4} {:start 4, :end 6} {:start 6, :end 7}])))
                       (is (= (segments (int-array [0x000001 0x000002 0x000003 0x000004 0x000005 0x000006]) 3)
                              [{:start 0, :end 2} {:start 2, :end 4} {:start 4, :end 6}])))