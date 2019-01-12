(ns distilled.core)

(list 1 2 3 4)

(defn square [x]
  (* x x))

(defn bmi [height weight]
  (println "height:" height)
  (println "weight:" weight)
  (/ weight (square height)))

(bmi 1.8 75)
; => 23.14...

(map #(* % %) (range 1 6))
; => (1 4 9 16 25)

(loop [[n & numbers] [1 2 3 4 5]
       result []]
  (let [result (conj result (* n n))]
     (if numbers
       (recur numbers result)
       result)))
; => (1 4 9 16 25)


(filter even? (range 1 6))
; => (2 4)


(filter even?
        (map #(* 3 %) (range 1 6)))
; => (6 12)

; = Clojures ============================

(defn greeting [greeting-string]
  (fn [quest]
    (println greeting-string quest)))

(let [greet (greeting "Welcome to the wonderful world of clojure")]
  (greet "Jane")
  (greet "John"))

; = Threading =============================

(reduce + (interpose 5 (map inc (range 10))))

(->> (range 10) (map inc) (interpose 5) (reduce +))

; source: http://www.clearwhitelight.org/hitch/hhgttg.txt
(def text (slurp "data/hhgttg.txt"))

(->> text
     (re-seq #"\w+")
     (frequencies)
     (sort-by val)
     (reverse)
     (take 10))
; => Top 10 most common words and their frequencies
; (["the" 2224] ["of" 1254] ["to" 1176] ... )

; source: https://www.textfixer.com/tutorials/common-english-words.txt
(def common-words (-> (slurp "data/common-english-words.txt")
                      (clojure.string/split #",")
                      set))

(->> text
     (re-seq #"[\w|']+")
     (map #(clojure.string/lower-case %))
     (remove common-words)
     (frequencies)
     (sort-by val)
     (reverse)
     (take 10))

; = Code structure =========================

(println
  (filter #(= (mod % 2) 0)
          (map #(* % %) (range 1 6))))
; => (4 16)


(->> (range 1 6)
     (map #(* % %))
     (filter #(= (mod % 2) 0))
     (println))
; => (4 16)

; = Destructuring ==========================

(def my-list (list "Bronn" "Hound" "Mountain"))

(let [[_ name-2 _ :as sellswords] my-list]
  [name-2 sellswords])
; => ["Hound" ("Bronn" "Hound" "Mountain")]


(let [[smaller bigger] (split-with #(< % 5) (range 10))]
  (println smaller bigger))

(defn print-user [[name address phone]]
  (println name "-" address phone))

(print-user ["John" "45 street" "416-936-3218"])

(defn print-args [x y & remaining]
  (println "x: " x)
  (println "y: " y)
  (if remaining
    (println (count remaining) " remaining args: " remaining)
    (println "no extra args :-(")))

(print-args 1 :second)
(print-args 1 :second "hello" "World" 42)

(def my-map-ds {:foo "foo" :bar "bar"})
(let [{foo :foo bar :bar} my-map-ds]
  (println foo bar))

(def my-map-ds2 {:id "foo" :item [1 2 3]})
(let [{[a b c] :items id :id} my-map-ds2]
  (println id " -> " a b c))

; syntactic sugar for extracting keys from maps

(defn login [{:keys [user pass] :or {pass "default-password"} :as credentials}]
  (println "credentials:" credentials)
  (println "pass: " pass)
  (and (= user "bob") (= pass "secret")))

(login {:user "bob" :pass "secret"})
; => credentials: {:user bob, :pass secret}
;    pass: secret

(login {:user "bob"})
; => credentials: {:user bob}
;    pass: default-password

(def my-point {:z 50})
(let [{:keys [x y z] :or {x 100 y 200}} my-point]
  (+ x y z))
; => 350

; = Multimethods ==========================

(defmulti area :shape)
; => dispatch using keyword, in this case ":shape"
; NOTE: this works, because keywords can acts as functions, like in
;       (:x {:x 10 :y 20})

(defmethod area :circle [{:keys [r]}]
  (* Math/PI r r))

(defmethod area :rectangle [{:keys [l w]}]
  (* l w))

(defmethod area :default [shape]
  (throw (Exception. (str "unrecognized shape: " shape))))

(area {:shape :circle :r 10})
; => dispatch to circle implementation

(area {:w 10 :l 100 :shape :rectangle})
; => dispatch to rectangle implementation


(defmulti encounter2 (fn [x y] [(:Species x) (:Species y)]))
; => sophisticated dispatch method

(defmethod encounter2 [:Bunny :Lion] [b l] :run-away)
(defmethod encounter2 [:Lion :Bunny] [l b] :eat)
(defmethod encounter2 [:Lion :Lion] [l1 l2] :fight)
(defmethod encounter2 [:Bunny :Bunny] [b1 b2] :mate)

(def b1 {:Species :Bunny :other :stuff})
(def b2 {:Species :Bunny :other :stuff})
(def l1 {:Species :Lion :other :stuff})
(def l2 {:Species :Lion :other :stuff})

(encounter2 b1 b2)
;-> :mate
(encounter2 b1 l1)
;-> :run-away
(encounter2 l1 b1)
;-> :eat
(encounter2 l1 l2)
;-> :fight

; = Dealing with global state =======================


