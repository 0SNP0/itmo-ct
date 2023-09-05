module HW0.T4
  ( fac
  , fib
  , map'
  , repeat'
  ) where

import Data.Function (fix)
import Numeric.Natural (Natural)

repeat' :: a -> [a]
repeat' x = fix (x:)

map' :: (a -> b) -> [a] -> [b]
map' mapper = fix $ \f list ->
  case list of
    (x:xs) -> mapper x : f xs
    []     -> []

fib :: Natural -> Natural
fib = fix (\f x y n ->
  if n == 0
    then x
    else f y (x + y) (n - 1)
  ) 0 1

fac :: Natural -> Natural
fac = fix $ \f n ->
  if n == 0
    then 1
    else n * f (n - 1)
