(ns csv-tables.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
  ::table-data
  (fn [db]
    (let [{{rows :rows columns :columns} :table-content} db
            row-values (map #(get % :value) rows)
            sum-value (reduce + row-values)
            rows-count (count row-values)
            avg-value (/ sum-value (if (= 0 rows-count) 1 rows-count))]
      {:columns columns
       :rows rows
       :sum-value sum-value
       :avg-value avg-value})))

(re-frame/reg-sub
  ::validation-result
  (fn [db]
    (let [{validation-result :validation-result} db]
      validation-result)))
