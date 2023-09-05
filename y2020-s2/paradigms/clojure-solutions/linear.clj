; --HW8--linear--Simplex--hard---

;checks
(defn sameSizes? [args] (every? (fn [operand] (== (count (first args)) (count operand))) args))
(defn vectors? [args] (every? (fn [operand] (and (vector? operand) (every? number? operand))) args))
(defn scalars? [args] (every? number? args))
(defn matrices? [args] (every? (fn [operand] (and (vector? operand) (and (vectors? operand) (sameSizes? operand)))) args))
(defn depth [arg] (if (vector? arg) (inc (depth (first arg))) 0))
(defn sameDepth? [arg] (if (vector? arg) (every? (fn [x] (and (sameDepth? x) (== (depth x) (depth (first arg))))) arg) (number? arg)))
(defn checkSimplex [arg, size]
  (if (vector? arg) (and (== (count arg) size) (every? (fn [i] (checkSimplex (nth arg i) (- size i))) (range size))) true))
(defn simplices? [args] (every? (fn [operand] (and (sameDepth? operand) (checkSimplex operand (count operand)))) args))
(defn sameSizeAndDimensionality? [args] (and (every? (fn [x] (== (depth (first args)) (depth x))) args) (sameSizes? args)))
;operations
; vectors
(defn vectorOp [op] (fn [& vectors]
    {:pre [(and (vectors? vectors) (sameSizes? vectors))]}
    (apply mapv op vectors)))
(def v+ (vectorOp +))
(def v- (vectorOp -))
(def v* (vectorOp *))
(def vd (vectorOp /))
(defn v*s [vector & scalars]
  {:pre [(and (vectors? (list vector)) (scalars? scalars))]}
  (let [s (apply * scalars)] (mapv (fn [v] (* v s)) vector)))
(defn scalar [& vectors]
  {:pre [(and (vectors? vectors) (sameSizes? vectors))]}
  (apply + (apply v* vectors)))
(defn combCoordinates [a b c1 c2]
  (- (* (nth a c1) (nth b c2)) (* (nth b c1) (nth a c2))))
(defn vect [& vectors]
  {:pre [(and (vectors? vectors) (sameSizes? vectors))]}
  (reduce (fn [a b] (vector (combCoordinates a b 1 2) (combCoordinates a b 2 0) (combCoordinates a b 0 1))) vectors))
; matrices
(defn matrixOp [op] (fn [& matrices]
    {:pre [(and (matrices? matrices) (and (sameSizes? matrices) (sameSizes? (mapv first matrices))))]}
    (apply mapv op matrices)))
(def m+ (matrixOp v+))
(def m- (matrixOp v-))
(def m* (matrixOp v*))
(def md (matrixOp vd))
(defn m*s [matrix & scalars]
  {:pre [(and (matrices? (list matrix)) (scalars? scalars))]}
  (let [s (apply * scalars)] (mapv (fn [m] (v*s m s)) matrix)))
(defn m*v [matrix & vectors]
  {:pre [(and (matrices? (list matrix)) (vectors? vectors))]}
  (mapv (fn [m] (apply scalar m vectors)) matrix))
(defn transpose [matrix]
  {:pre [(matrices? (list matrix))]}
  (apply mapv vector matrix))
(defn m*m [& matrices]
  {:pre [(matrices? matrices)]}
  (reduce (fn [a b] (mapv (fn [v] (m*v (transpose b) v)) a)) matrices))
; simplices
(defn simplexOp [op]
  (fn [& simplices]
    {:pre [(and (simplices? simplices) (sameSizeAndDimensionality? simplices))]}
    (apply mapv (if (vectors? simplices) op (simplexOp op)) simplices)))
(def x+ (simplexOp +))
(def x- (simplexOp -))
(def x* (simplexOp *))
(def xd (simplexOp /))
