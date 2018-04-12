(ns koi.dispatcher
  #?(:clj
     (:require
      [clojure.core.async :as async])
     :cljs
     (:require
      [cljs.core.async :as async])))

(defprotocol IDispatcher
  (message-chan [dispatcher])
  (dispatch! [dispatcher message]))

(defrecord MessageDispatcher [message-ch]
  IDispatcher
  (message-chan [message-dispatcher]
    (:message-ch message-dispatcher))
  (dispatch! [message-dispatcher message]
    (async/put! (:message-ch message-dispatcher) message)))

(defn new-message-dispatcher
  ([message-ch]
   (map->MessageDispatcher {:message-ch message-ch}))
  ([]
   (new-message-dispatcher (async/chan 128))))

(defn stop-dispatcher!
  [dispatcher]
  (async/close! (message-chan dispatcher)))
