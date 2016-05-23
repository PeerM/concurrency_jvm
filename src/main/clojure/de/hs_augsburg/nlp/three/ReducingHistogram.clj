(ns de.hs-augsburg.nlp.three.ReducingHistogram
  (:gen-class
    :implements [de.hs_augsburg.nlp.three.histogram.IHistogram])
  (:import (de.hs_augsburg.nlp.three.histogram ColorMask ClojureHelpers)))

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

(defn color-hist [mask ^ints pixels cocurcencie]
  (->> (segments pixels cocurcencie)
       (pmap (fn [segment]
               (ClojureHelpers/partialHistogram
                 pixels mask
                 (:start segment) (:end segment))))
       (reduce (fn [prev next] (ClojureHelpers/arrayElementBasedAdd prev next)))))

(defn -histogram [this ^ints pixels]
  (into {}
        (pmap
          (fn [mask] [mask (color-hist mask pixels (.. Runtime getRuntime availableProcessors))])
          [ColorMask/BLUE ColorMask/RED ColorMask/GREEN])))


(defn run [] (-histogram nil (ClojureHelpers/pathToPixels "flickr1.jpg")))
