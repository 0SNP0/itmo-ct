module HW2.T2
  ( distAnnotated
  , distExcept
  , distFun
  , distList
  , distOption
  , distPair
  , distPrioritised
  , distQuad
  , distStream
  , wrapAnnotated
  , wrapExcept
  , wrapFun
  , wrapList
  , wrapOption
  , wrapPair
  , wrapPrioritised
  , wrapQuad
  , wrapStream
  ) where

import HW2.T1

distOption :: (Option a, Option b) -> Option (a, b)
distOption (Some a, Some b) = Some (a, b)
distOption _                = None

distPair :: (Pair a, Pair b) -> Pair (a, b)
distPair (P a b, P c d) = P (a, c) (b, d)

distQuad :: (Quad a, Quad b) -> Quad (a, b)
distQuad (Q a b c d, Q e f g h) = Q (a, e) (b, f) (c, g) (d, h)

distAnnotated :: Semigroup e => (Annotated e a, Annotated e b) -> Annotated e (a, b)
distAnnotated (a :# e1, b :# e2) = (a, b) :# (e1 <> e2)

distExcept :: (Except e a, Except e b) -> Except e (a, b)
distExcept (Error e, _)           = Error e
distExcept (_, Error e)           = Error e
distExcept (Success a, Success b) = Success (a, b)

distPrioritised :: (Prioritised a, Prioritised b) -> Prioritised (a, b)
distPrioritised (Low a, Low b)       = Low (a, b)
distPrioritised (Low a, Medium b)    = Medium (a, b)
distPrioritised (Low a, High b)      = High (a, b)
distPrioritised (Medium a, Low b)    = Medium (a, b)
distPrioritised (Medium a, Medium b) = Medium (a, b)
distPrioritised (Medium a, High b)   = High (a, b)
distPrioritised (High a, Low b)      = High (a, b)
distPrioritised (High a, Medium b)   = High (a, b)
distPrioritised (High a, High b)     = High (a, b)

distStream :: (Stream a, Stream b) -> Stream (a, b)
distStream (ha :> ta, hb :> tb) = (ha, hb) :> distStream (ta, tb)

distList :: (List a, List b) -> List (a, b)
distList (Nil, _) = Nil
distList (_, Nil) = Nil
distList (ha :. ta, b) = merge (loop ha b) (distList (ta, b)) where
  merge :: List a -> List a -> List a
  merge Nil x        = x
  merge x Nil        = x
  merge (ha :. ta) b = ha :. merge ta b

  loop :: a -> List b -> List (a, b)
  loop _ Nil        = Nil
  loop e (hc :. tc) = (e, hc) :. loop e tc

wrapOption :: a -> Option a
wrapOption = Some

wrapPair :: a -> Pair a
wrapPair x = P x x

wrapQuad :: a -> Quad a
wrapQuad x = Q x x x x

wrapAnnotated :: Monoid e => a -> Annotated e a
wrapAnnotated x = x :# mempty

wrapExcept :: a -> Except e a
wrapExcept = Success

wrapPrioritised :: a -> Prioritised a
wrapPrioritised = Low

wrapStream :: a -> Stream a
wrapStream x = x :> wrapStream x

wrapList :: a -> List a
wrapList x = x :. Nil

distFun :: (Fun i a, Fun i b) -> Fun i (a, b)
distFun (F f, F g) = F $ \x -> (f x, g x)

wrapFun :: a -> Fun i a
wrapFun x = F $ \_ -> x
