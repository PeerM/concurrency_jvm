(ns de.hs_augsburg.nlp.two.RefBank
  (:import (de.hs_augsburg.nlp.two IBank)
           (java.util List)
           (de.hs_augsburg.nlp.two.BasicMonitor Entry EntryType)))


(defn basic-acc [] {:balance 0 :entires []})

(defn entry [amount type text] {:amount amount :type type :text text})

(defn add-acc [accNo acounts] (assoc acounts accNo (ref (basic-acc))))

(defn gen-accNo! [accNoAtom] (swap! accNoAtom (fn [old] (+ old 1))))

(defn deposit-into [account amount]
  (let [account-with-balance (assoc account :balance (+ amount (:balance account)))]
    (assoc account-with-balance :entires (conj (:entires account-with-balance) (entry amount :deposit "deposit")))))

(defn withdraw-from [account amount]
  (let [account-with-balance (assoc account :balance (- amount (:balance account)))]
    (assoc account-with-balance :entires (conj (:entires account-with-balance) (entry amount :withdraw "withdraw")))))

(defn deposit! [accounts-atom accNo amount]
  (dosync (commute (get @accounts-atom accNo) deposit-into amount)))

(defn withdraw! [accounts-atom accNo amount]
  (dosync (commute (get @accounts-atom accNo) withdraw-from amount)))

(defn transfer! [accounts-atom from to amount]
  (dosync
    (commute (get @accounts-atom from) withdraw-from amount)
    (commute (get @accounts-atom to) deposit-into amount)))

(def type-enum-map {:withdraw EntryType/WITHDRAW, :deposit EntryType/DEPOSIT})

(defn entry-to-java [entry] (new Entry (:text entry) (:amount entry) (type-enum-map (:type entry))))

(defn get-entries-local [accounts accno] (map entry-to-java (:entires @(get accounts accno))))
(defn get-entries-complete [accounts-atom accno] (get-entries-local @accounts-atom accno))
(defn get-a-bunch-of-entries [accounts accnos] (dosync (flatten (map (fn [accno] (get-entries-local accounts accno)) accnos))))

(defn make-Bank []
  (let [accounts (atom []) nextAccNo (atom 0)]
    (reify IBank
      (deposit [this accno amount] (deposit! accounts, accno, amount))
      (withdraw [this accno amount] (withdraw! accounts, accno, amount))
      (transfer [this from to amount] (transfer! accounts from to amount))
      (^List getAccountEntries [this ^long accno] (get-entries-complete accounts accno))
      (^List getAccountEntries [this ^List accnos] (get-a-bunch-of-entries @accounts accnos))
      (getBalance [this accno] (:balance @(get @accounts accno)))
      (createAccount [this] (let [accNo (gen-accNo! nextAccNo)]
                              (swap! accounts (partial add-acc accNo))
                              accNo)))))