(ns de.hs-augsburg.nlp.three.ReduceHistogram
  (:gen-class
    :implements [de.hs_augsburg.nlp.three.IHistogram])
  (:import (de.hs_augsburg.nlp.three ColorMask ClojureHelpers)))

(defn hist [pixels ^ColorMask mask] (->> pixels
                                         (map (fn [x] (.apply mask x)))
                                         (frequencies)))

;(def pixels (int-array [0xFF0001 0x01FF00]))

(defn map-to-array [the-map] (let [^ints values (int-array 256)]
                               (doseq
                                 [pair (seq the-map)]
                                 (aset ^ints values ^int (get pair 0) ^int (get pair 1)))
                               values))

(defn -histogram [this ^ints pixels]
  (into {}
        (map
          (fn [mask] [mask (map-to-array (hist pixels mask))])
          [ColorMask/BLUE ColorMask/RED ColorMask/GREEN])))

(defn run [] (-histogram nil (ClojureHelpers/pathToPixels "flickr1.jpg")))


;(reduce
;  (fn [prev, next] (assoc prev (get next 0) (get next 1)))
;  {:a 1}
;  (-histogram pixels))