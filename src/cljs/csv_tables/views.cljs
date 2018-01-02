(ns csv-tables.views
  (:require [re-frame.core :as re-frame]
            [csv-tables.subs :as subs]
            ))

(defn file-input []
  [:input {:type "file"
           :placeholder "Select file"
           :on-change #(re-frame/dispatch
                         [:select-file (first (array-seq (-> % .-target .-files)))])}])

(defn aggregated-results [sum-value avg-value]
  [:div
   [:b "sum"
    [:p sum-value]]
   [:b "avg"
    [:p avg-value]]])

(defn validation-tab [errors]
  [:ul (for [[index error] (map-indexed vector errors)]
    ^{:key index} [:li {:style {"color" "red"}} error])])

(defn csv-table [table-data]
  (let [columns (:columns table-data)
        name-column (first columns)
        value-column (second columns)
        rows (:rows table-data)
        sum-value (:sum-value table-data)
        avg-value (:avg-value table-data)]
    [:div
     [:table
      [:thead
       [:tr
        [:th [:input {:type "text" :value name-column :on-change #(re-frame/dispatch [:column-title-change (-> % .-target .-value)])}]]
        [:th [:input {:type "text" :value value-column :on-change #(re-frame/dispatch [:column-value-change (-> % .-target .-value)])}]]
        ]]
      [:tbody
       (for [[index row] (map-indexed vector rows)]
         ;; there is no any filter or sorting, so index is justified as react and data key
         ^{:key index} [:tr
                        [:td [:input {:type "text"
                                      :value (:title row)
                                      :on-change #(re-frame/dispatch [:row-title-change (-> % .-target .-value) index])}]]
                        [:td [:input {:type "number"
                                      :value (:value row)
                                      :on-change #(re-frame/dispatch [:row-value-change (-> % .-target .-value) index])}]]])
       ]]
     [aggregated-results sum-value avg-value] ]

    ))



(defn main-panel []
  (let [table-data (re-frame/subscribe [::subs/table-data])
        validation-result (re-frame/subscribe [::subs/validation-result])
        errors (:errors @validation-result)]
    [:div
     [file-input]
     (if (and (:valid? @validation-result) (:data-loaded? @validation-result)) [csv-table @table-data])
     (if (not (empty? errors)) [validation-tab errors])]))
