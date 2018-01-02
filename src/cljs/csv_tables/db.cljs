(ns csv-tables.db)

(def default-db
  {:table-content nil
   :validation-result {:valid? true
                       :errors []
                       :data-loaded? false}})
