(ns simple-profiler.core-test
  (:use clojure.test
        simple-profiler.core))

(defn- plus [a b]
  (Thread/sleep 500)
  (+ a b))

(defn- hello []
  (Thread/sleep 1000)
  (println "Hello world! "
           (plus 1 2)))

(defn- millis-to-nanos [millis]
  (* millis 1000000))

(deftest profile-basic-test
  (testing "the functions should be profiled"
    (println "running profiler basic test...")
    (let [output (profile (with-out-str (hello)))
          the-agent @(ns-resolve 'simple-profiler.core 'log-agent)
          _ (await the-agent)
          results (get-results)
          hello-name (str #'hello)
          plus-name (str #'plus)
          hello-result (first results)
          plus-result (second results)
          hello-metrics (second hello-result)
          plus-metrics (second plus-result)]

      ;; Assert the results
      (is (= "Hello world! 3")
          output)
      
      ;; Results for `hello` func
      (is (= hello-name
             (first hello-result)))
      (is (= 1 (:count hello-metrics)))
      (is (< (millis-to-nanos 1500)
             (:min-time hello-metrics)))
      (is (< (millis-to-nanos 1500)
             (:max-time hello-metrics)))
      (is (<= (:min-time hello-metrics)
              (:max-time hello-metrics)))

      ;; Results for `plus` func
      (is (= plus-name
             (first plus-result)))
      (is (= 1 (:count plus-metrics)))
      (is (< (millis-to-nanos 500)
             (:min-time plus-metrics)))
      (is (< (millis-to-nanos 500)
             (:max-time plus-metrics)))
      (is (<= (:min-time plus-metrics)
              (:max-time plus-metrics)))

      )))

