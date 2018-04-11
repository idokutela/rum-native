(ns my-app.core
  (:require [rum.core :refer [defc] :as rum]
            [react-native.core :as rn]))

(def state (atom "Hello, world!"))
(defc App < rum/reactive [state]
  (rn/view {:style {:align-items :center
                    :justify-content :center
                    :flex 1}}
           (rn/text {:style {:font-size 30}} (rum/react state))))

(defn init []
  (rn/mount-and-register "MyApp" (App state)))


