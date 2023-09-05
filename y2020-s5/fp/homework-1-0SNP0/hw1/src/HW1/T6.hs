module HW1.T6
  ( epart
  , mcat
  ) where

mcat :: Monoid a => [Maybe a] -> a
mcat  = foldr mcat' mempty where
  mcat' :: Monoid a => Maybe a -> a -> a
  mcat' Nothing acc  = acc
  mcat' (Just x) acc = x <> acc

epart :: (Monoid a, Monoid b) => [Either a b] -> (a, b)
epart = foldr epart' (mempty, mempty) where
  epart' x (a, b) = case x of
    Left l  -> (l <> a, b)
    Right r -> (a, r <> b)
