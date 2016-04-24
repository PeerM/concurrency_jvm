(ns de.hs_augsburg.nlp.two.RefBankFactory
  (:require
    [de.hs_augsburg.nlp.two.RefBank :as RefBank])
  (:import (de.hs_augsburg.nlp.two.Functional CasBank)
           (de.hs_augsburg.nlp.two IBank))
  (:gen-class
    :name de.hs_augsburg.nlp.two.cl.RefBankFactory
    :methods [^:static [makeRefBank [] "de.hs_augsburg.nlp.two.IBank"]]))


(defn ^IBank -makeRefBank [] (new CasBank))
