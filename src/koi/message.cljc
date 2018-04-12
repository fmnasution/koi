(ns koi.message)

(defprotocol IMessage
  (handle [message context])
  (handle-error [message context error]))
