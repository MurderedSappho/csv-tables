(ns csv-tables.events
  (:require [re-frame.core :as re-frame]
            [csv-tables.db :as db]
            [clojure.string :as str]))

(re-frame/reg-event-db
  ::initialize-db
  (fn  [_ _]
    db/default-db))

(defn validate-file [file]
  (let [valid-file-size?  (> (* 1024 1024) (.-size file))
        file-extension (last (str/split (.-name file) #"\."))
        valid-extension? (= "csv" file-extension)
        errors (remove nil? [(when (not valid-file-size?) "file size should be less than 1MB")
                             (when (not valid-extension?) "file extension should be only csv")])]
    {:valid? (and valid-file-size? valid-extension?)
     :errors errors
     :data-loaded? true}))

;; I see no evil to don't use event-fx while we have no unit tests
(re-frame/reg-event-db
  :select-file
  (fn [db [_ file]]
    (let [file-reader (js/FileReader.)
          file-validation-result (validate-file file)]
      (when (:valid? file-validation-result)
        (aset file-reader "onload" #(re-frame/dispatch [:load-csv-file (-> % .-target .-result)]))
        (.readAsText file-reader file))
      (assoc db :validation-result file-validation-result))))

(re-frame/reg-event-db
  :load-csv-file
  (fn [db [_ file-text]]
    (let [split-result (str/split-lines file-text)
          parse-result (map #(str/split % #",") split-result)
          parsed-columns (first parse-result)
          name-column (first parsed-columns)
          value-column (second parsed-columns)
          columns [(if (str/blank? name-column) "Name" name-column)
                   (if (str/blank? value-column) "Value" value-column)]
          rows (vec (map
                      #(hash-map :title (first %) :value (int (second %)))
                      (rest parse-result)))]
      (assoc db :table-content {:columns columns
                                :rows rows}))))

(re-frame/reg-event-db
  :row-title-change
  (fn [db [_ value index]]
    (assoc-in db [:table-content :rows index :title] value)))

(re-frame/reg-event-db
  :row-value-change
  (fn [db [_ value index]]
    (let [number-value (int value)]
      (if (number? number-value)
        (assoc-in db [:table-content :rows index :value] number-value)
        db))))

(re-frame/reg-event-db
  :column-title-change
  (fn [db [_ value]]
    (assoc-in db [:table-content :columns 0] value)))

(re-frame/reg-event-db
  :column-value-change
  (fn [db [_ value]]
    (assoc-in db [:table-content :columns 1] value)))
