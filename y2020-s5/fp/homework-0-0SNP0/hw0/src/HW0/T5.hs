module HW0.T5
  ( Nat
  , nFromNatural
  , nToNum
  , nmult
  , nplus
  , ns
  , nz
  ) where

import Numeric.Natural (Natural)

type Nat a = (a -> a) -> a -> a


nz :: Nat a
nz _ x = x

ns :: Nat a -> Nat a
ns n x = x . (n x)

nplus, nmult :: Nat a -> Nat a -> Nat a
nplus x y f = y f . (x f)
nmult x y f = x $ y f

nFromNatural :: Natural -> Nat a
nFromNatural x = case x of
  0 -> nz
  x -> ns $ nFromNatural (x - 1)

nToNum :: Num a => Nat a -> a
nToNum c = c (+1) 0
