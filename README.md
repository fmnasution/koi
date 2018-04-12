# koi

Simple message signaling

## Usage

Require the namespaces
```clojure
(ns my-project.core
  (:require
   [koi.message :as msg]
   [koi.dispatcher :as dpt]
   [koi.listener :as lst]))
```

Define the message
```clojure
(defrecord PrintlnMessage [message]
  msg/IMessage
  (handle [message context]
    (println "Incoming message: " message))
  (handle-error [message context error]
    (println "Error:" error "when processing message:" message)))
    
(defn new-println-message
  [message]
  (PrintlnMessage. message))
```

Create the dispatcher
```clojure 
(def my-dispatcher (dpt/new-message-dispatcher))
(def my-dispatcher-listener (lst/start-message-listener! my-dispatcher))
```

Starts dispatching 
```clojure
(dpt/dispatch! my-dispatcher (new-println-message "foobar"))

;; printed
;; "Incoming message: foobar"
```

## License

Copyright © 2018 fmnasution

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
