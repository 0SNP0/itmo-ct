; --HW9--parseFunction--SumAvg--hard--
(def constant constantly)
(defn variable [name] #(%1 name))

(defn operation [act]
  (fn [& operands] (fn [vars] (apply act (mapv #(%1 vars) operands)))))

(def add (operation +))
(def subtract (operation -))
(def multiply (operation *))
(def divide (operation (fn ([arg] (/ 1 (double arg)))
                         ; :NOTE: reduce
                         ([x & args] (reduce #(/ %1 (double %2)) x args)))))
(def negate (operation -))
(def sum (operation +))
(def avg (operation (fn [& args] (/ (apply + args) (count args)))))

(def OPS {'+ add, '- subtract, '* multiply, '/ divide, 'negate negate, 'sum sum, 'avg avg})
(defn parseSeq [expr]
  (cond
    (number? expr) (constant expr)
    (symbol? expr) (variable (str expr))
    (seq? expr) (apply (get OPS (first expr)) (map parseSeq (rest expr)))))
(def parseFunction (comp parseSeq read-string))

; --HW10--parseObject--SumAvg--hard--
(load-file "proto.clj")

(defn remove-once [pred coll]
  ((fn in [c]
     (lazy-seq (when-let [[x & xs] (seq c)] (if (pred x) xs (cons x (in xs)))))
     ) coll))

(def toString (method :toString))
(def evaluate (method :evaluate))
(def diff (method :diff))
(def toStringInfix (method :toStringInfix))

(defn Primitive [this x] (assoc this :value x))

(declare Constant ZERO ONE)
(def ConstantPrototype
  (let [val (field :value)]
    {
     :toString      (fn [this] (str (val this)))
     :evaluate      (fn [this _] (val this))
     :diff          (fn [_ _] ZERO)
     :toStringInfix toString
     }))
(def Constant (constructor Primitive ConstantPrototype))
(def ZERO (Constant 0))
(def ONE (Constant 1))

(declare Variable)
(def VariablePrototype
  (let [name (field :value)]
    {
     :toString      (fn [this] (name this))
     :evaluate      (fn [this variables] (variables (clojure.string/lower-case (subs (name this) 0 1))))
     :diff          (fn [this var] (if (= var (name this)) ONE ZERO))
     :toStringInfix toString
     }))
(def Variable (constructor Primitive VariablePrototype))

(def operands (field :operands))
(def OperationPrototype
  (let [char (field :symbol) op (field :action) ruleDiff (method :ruleDiff)]
    {
     :toString      (fn [this] (str "(" (char this) " " (clojure.string/join " " (mapv toString (operands this))) ")"))
     :evaluate      (fn [this variables] (apply (op this) (mapv #(evaluate %1 variables) (operands this))))
     :diff          (fn [this var] (ruleDiff this var))
     :toStringInfix (fn [this] (if (= (count (operands this)) 1)
                                 (str (char this) "(" (toStringInfix (first (operands this))) ")")
                                 (str "(" (clojure.string/join (str " " (char this) " ") (mapv toStringInfix (operands this))) ")")))
     }))

(defn createOperation
  ([char op ruleDiff]
   (let [Operation (fn [this & args] (assoc this :symbol char :action op :ruleDiff ruleDiff :operands args))]
     (constructor Operation OperationPrototype)))
  ([char op]
   (let [Operation (fn [this & args] (assoc this :symbol char :action op :operands args))]
     (constructor Operation OperationPrototype))))
(declare Add, Subtract, Multiply, Divide, Negate, Sum, Avg)

(defn operandsDiff [this var] (mapv #(diff %1 var) (operands this)))
(defn mulDiff [op dop] (mapv #(apply Multiply (cons (nth dop %1) (remove-once #{(nth op %1)} op))) (range (count op))))
(defn divDiff [op var] (cond
                         (== (count op) 1) (let [x (first op)] (Negate (Divide (diff x var) x x)))
                         :else (apply Divide (apply Subtract (mulDiff op (mapv #(diff %1 var) op))) (concat (rest op) (rest op)))))

(def Add (createOperation '+ + #(apply Add (operandsDiff %1 %2))))
(def Subtract (createOperation '- - #(apply Subtract (operandsDiff %1 %2))))
(def Multiply (createOperation '* * (fn [this var] (apply Add (mulDiff (operands this) (mapv #(diff %1 var) (operands this)))))))
(def Divide (createOperation '/
                             (fn ([arg] (/ 1 (double arg)))
                               ([x & args] (reduce #(/ %1 (double %2)) x args)))
                             #(divDiff (operands %1) %2)))

(def Negate (createOperation 'negate - #(apply Negate (operandsDiff %1 %2))))
(def Sum (createOperation 'sum + #(apply Sum (operandsDiff %1 %2))))
(def Avg (createOperation 'avg (fn [& args] (/ (apply + args) (count args)))
                          #(apply Avg (operandsDiff %1 %2))))

(def OP_OBJ {'+ Add, '- Subtract, '* Multiply, '/ Divide, 'negate Negate, 'sum Sum, 'avg Avg})
(defn parseSeqObject [expr]
  (cond
    (number? expr) (Constant expr)
    (symbol? expr) (Variable (str expr))
    (seq? expr) (apply (get OP_OBJ (first expr)) (map parseSeqObject (rest expr)))
    ))
(def parseObject (comp parseSeqObject read-string))

; --HW11--parseObjectInfix--PowLog--hard--

(def IPow (createOperation '**
                           (fn [& args] (reduce #(Math/pow %2 %1) (reverse args)))))
(def ILog (createOperation (symbol "//")
                           (fn [& args] (reduce #(/ (Math/log (Math/abs %1)) (double (Math/log (Math/abs %2)))) (reverse args)))))
(def OPS_CMB {'+ Add, '- Subtract, '* Multiply, '/ Divide, 'negate Negate, '** IPow, (symbol "//") ILog})

(load-file "parser.clj")
(defn +strf [f x] (mapv #(f (str %)) x))
(def *str #(apply +seqf str (+strf +char %)))
(def *str-or #(apply +or (+strf *str %)))
(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))
(def *ign-ws #(+seqn 0 *ws % *ws))
(def *digit (+char "0123456789"))

(def *number
  (+map read-string (+seqf
    #(str %1 (apply str %2) %3 (apply str %4))
    (+opt (+char "+-"))
    (+plus *digit)
; :NOTE: Не аккуратно
    (+opt (+char "."))
    (+opt (+plus *digit)))))

(def *variable
  (+str (+plus (+char "xyzXYZ"))))
(def *unary (*str-or ['negate]))

(defn createOp [char & args] (apply (get OPS_CMB (symbol char)) args))

(defn left-assoc [x & [args]]
  (reduce (fn [a [o b]] (createOp o a b)) x args))
(defn right-assoc [x & [args]]
  (if (empty? args) x (createOp (first (first args)) x (apply right-assoc (vector (second (first args)) (rest args))))))

(declare *low, *mid, *high)
(def *val (*ign-ws (+or
                     (+map Constant *number)
                     (+map Variable *variable)
                     (+map #(createOp (first %) (second %)) (+seq *unary *ws (delay *val)))
                     (+seqn 1 (+char "(") (delay *low) (+char ")")))))

(defn *op [build next_priority & chars]
  (+map (partial apply build) (+seq next_priority (+star (+seq (*ign-ws (*str-or chars)) next_priority)))))

(def *high (*op right-assoc *val '** (symbol "//")))
(def *mid (*op left-assoc *high '* '/))
(def *low (*op left-assoc *mid '+ '-))

(def parseObjectInfix (+parser *low))
