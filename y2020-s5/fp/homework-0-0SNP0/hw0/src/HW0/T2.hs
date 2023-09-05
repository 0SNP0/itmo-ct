module HW0.T2
  ( Not
  , doubleNeg
  , reduceTripleNeg
  ) where

import Data.Void

type Not a = a -> Void

doubleNeg :: a -> Not (Not a)
doubleNeg x neg = neg x

reduceTripleNeg :: Not (Not (Not a)) -> Not a
reduceTripleNeg neg = neg . doubleNeg
