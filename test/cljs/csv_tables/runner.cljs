(ns csv-tables.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [csv-tables.core-test]))

(doo-tests 'csv-tables.core-test)
