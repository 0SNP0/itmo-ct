module HW2.T3
  ( joinAnnotated
  , joinExcept
  , joinFun
  , joinList
  , joinOption
  ) where

import HW2.T1

joinOption :: Option (Option a) -> Option a
joinOption (Some x) = x
joinOption _        = None

joinExcept :: Except e (Except e a) -> Except e a
joinExcept (Success s) = s
joinExcept (Error e)   = Error e

joinAnnotated :: Semigroup e => Annotated e (Annotated e a) -> Annotated e a
joinAnnotated ((a :# b) :# c) = a :# c <> b

joinList :: List (List a) -> List a
joinList Nil      = Nil
joinList (h :. t) = merge h $ joinList t where
  merge :: List a -> List a -> List a
  merge Nil x        = x
  merge (ha :. ta) b = ha :. merge ta b

joinFun :: Fun i (Fun i a) -> Fun i a
joinFun (F f) = F (\i -> unwrap (f i) i) where
  unwrap (F g) = g
