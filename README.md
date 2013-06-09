# simple-profiler

Simple profiler for Clojure. 

## Usage

Add the following to the dependencies of your `project.clj`:

```clojure
[simple-profiler "0.1.0-SNAPSHOT"]
```

Import the functions/macros:

```clojure
(use '[simple-profiler.core :only [profile get-results clear-results]])
```

Example functions for testing:

```clojure
(defn plus [a b]
  (Thread/sleep 500)
  (+ a b))
  
(defn hello []
  (Thread/sleep 1000)
  (println "Hello world! "
           (plus 1 2)))
```

Profile the code:

```clojure
(profile (hello))
```

`profile` is a macro, you can put any expression in its body.

Get the profile results. Note that the time unit is nano seconds.
The following test is done in a nREPL session, so you can see that
`clojure.tools.nrepl` functions are also profiled.

```clojure
user> (use 'clojure.pprint)
user> (pprint (get-results))
(["#'simple-profiler.sample/hello"
  {:count 1,
   :max-time 1501559440,
   :min-time 1501559440,
   :avg-time 1501559440}]
 ["#'simple-profiler.sample/plus"
  {:count 1,
   :max-time 500096687,
   :min-time 500096687,
   :avg-time 500096687}]
 ["#'clojure.walk/stringify-keys"
  {:count 1, :max-time 351203, :min-time 351203, :avg-time 351203}]
 ["#'clojure.walk/postwalk"
  {:count 10, :max-time 319648, :min-time 10832, :avg-time 62653}]
 ["#'clojure.walk/walk"
  {:count 10, :max-time 300026, :min-time 1089, :avg-time 48858}]
 ["#'clojure.tools.nrepl.bencode/write-netstring*"
  {:count 6, :max-time 20204, :min-time 15144, :avg-time 18015}]
 ["#'clojure.tools.nrepl.misc/response-for"
  {:count 1, :max-time 19177, :min-time 19177, :avg-time 19177}]
 ["#'clojure.tools.nrepl.bencode/string>payload"
  {:count 12, :max-time 4111, :min-time 636, :avg-time 1624}]
 ["#'clojure.tools.nrepl.bencode/lexicographically"
  {:count 4, :max-time 1850, :min-time 798, :avg-time 1105}])
nil
```

Clear the stored profile results:

```clojure
(clear-results)
```

## TODO

- Store the profile results in a log file and then parse the log file
to get the result. Currently all the profile data is stored in-memory,
and this is not good for long-runing profile jobs.

## License

Copyright © 2013 Jerry Peng

Distributed under the Eclipse Public License, the same as Clojure.
