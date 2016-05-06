(ns de.hs-augsburg.nlp.three.ReducingHistogram
  (:gen-class
    :implements [de.hs_augsburg.nlp.three.IHistogram])
  (:import (de.hs_augsburg.nlp.three ColorMask ClojureHelpers)))


(def pixels (int-array [0x000001 0x000002 0x000003 0x000004 0x000005 0x000006 0x000007]))

(defn segment-size [^ints pixels parts] (int (/ (alength pixels) parts)))

(defn first-segment [segment-length] {:start 0 :end segment-length})

(defn next-segment [prev segment-length absolute-length] {:start (+ segment-length (:start prev))
                                                          :end   (min (+ segment-length (:end prev)) absolute-length)})

(defn make-segmenter [segment-lenth absolute-length] (fn [prev] (next-segment prev segment-lenth absolute-length)))

(defn segments [^ints pixels num-segments]
  (->> (iterate
         (make-segmenter (segment-size pixels num-segments) (alength pixels))
         (first-segment (segment-size pixels num-segments)))
       (take-while (fn [segment] (< (:start segment) (alength pixels))))))

(defn color-hist [mask ^ints pixels] (->> (segments pixels 32)
                                          (map (fn [segment]
                                                 (ClojureHelpers/partialHistogram
                                                   pixels mask
                                                   (:start segment) (:end segment))))
                                          (reduce (fn [prev next] (ClojureHelpers/arrayElementBasedAdd prev next)))))

(defn -histogram [this ^ints pixels]
  (into {}
        (pmap
          (fn [mask] [mask (color-hist mask pixels)])
          [ColorMask/BLUE ColorMask/RED ColorMask/GREEN])))

(defn run [] (-histogram nil (ClojureHelpers/pathToPixels "flickr1.jpg")))


;(reduce
;  (fn [prev, next] (assoc prev (get next 0) (get next 1)))
;  {:a 1}
;  (-histogram pixels))