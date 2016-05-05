(ns de.hs-augsburg.nlp.three.ReduceHistogram
  (:gen-class
    :implements [de.hs_augsburg.nlp.three.IHistogram])
  (:import (de.hs_augsburg.nlp.three ColorMask ClojureHelpers)))


(def pixels (int-array [0xFF0001 0x01FF00]))

(defn -histogram [this ^ints pixels]
  (into {}
        (pmap
          (fn [mask] [mask (ClojureHelpers/partialHistogram pixels mask 0 (alength pixels))])
          [ColorMask/BLUE ColorMask/RED ColorMask/GREEN])))

(defn run [] (-histogram nil (ClojureHelpers/pathToPixels "flickr1.jpg")))


;(reduce
;  (fn [prev, next] (assoc prev (get next 0) (get next 1)))
;  {:a 1}
;  (-histogram pixels))