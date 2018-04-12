(ns koi.listener
  (:require
   [taoensso.encore :as encore]
   [koi.message :as msg]
   [koi.dispatcher :as dpt]
   #?@(:clj [[clojure.core.async :as async :refer [go-loop]]]
       :cljs [[cljs.core.async :as async]]))
  #?(:cljs
     (:require-macros
      [cljs.core.async.macros :refer [go-loop]])))

(defn- listen!
  [dispatcher context]
  (let [message-ch (dpt/message-chan dispatcher)
        stop-ch (async/chan)
        stopper (fn stop! [] (async/close! stop-ch))]
    (go-loop []
      (let [[message ch] (async/alts! [message-ch stop-ch] :priority true)
            stop? (or (= stop-ch ch) (nil? message))]
        (when-not stop?
          (encore/catching
           (msg/handle message context)
           error
           (msg/handle-error message context error))
          (recur))))
    stopper))

(defprotocol IListener
  (stopper [listener]))

(defrecord MessageListener [message-dispatcher context stopper]
  IListener
  (stopper [message-listener]
    (:stopper message-listener)))

(defn start-message-listener!
  ([message-dispatcher context]
   (let [stopper (listen! message-dispatcher context)]
     (MessageListener. message-dispatcher context stopper)))
  ([message-dispatcher]
   (start-message-listener! message-dispatcher {})))

(defn stop-message-listener!
  [message-listener]
  ((stopper message-listener)))
