(ns simple-profiler.core)

(defonce ^:private ^:dynamic *profile-out* System/err)

(defonce ^:private log-agent (agent []))

(defn- log-profile-event [events name time]
  (conj events [name time]))

(defn wrap-profile [name f]
  (fn [& args]
    (let [begin-time (System/nanoTime)]    
      (try
        (apply f args)
        (finally
          (let [end-time (System/nanoTime)]
            (send log-agent
                  log-profile-event
                  name
                  (- end-time begin-time))))))))

(defmacro profile [& body]
  `(let [ns# (remove #{(the-ns 'clojure.core)
                        (the-ns 'clojure.core.protocols)
                        (the-ns 'simple-profiler.core)}
                     (all-ns))
         funcs# (->> ns#
                     (mapcat ns-interns)
                     (map second)
                     (filter (comp fn? deref)))
         redefs# (->> funcs#
                      (map (fn [var] [var (wrap-profile (str var) @var)]))
                      (into {}))]
     (with-redefs-fn redefs#
       (fn [] ~@body))))


(defn clear-results []
  (send log-agent (fn [_] [])))

(defn get-results []
  (->> @log-agent
       (group-by first)
       (map (fn [[k v]]
              (let [time-spent (map second v)
                    call-count (count time-spent)
                    total-time (apply + time-spent)
                    min-time (apply min time-spent)
                    max-time (apply max time-spent)
                    avg-time (quot total-time call-count)]
                [(str k) {:count call-count
                          :max-time max-time
                          :min-time min-time
                          :avg-time avg-time}])))
       (sort-by (comp - :max-time second))))

