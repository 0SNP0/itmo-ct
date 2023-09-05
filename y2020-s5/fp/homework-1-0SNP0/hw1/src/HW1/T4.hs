module HW1.T4
  ( tfoldr
  ) where

import HW1.T3 (Tree (Branch, Leaf))

tfoldr :: (a -> b -> b) -> b -> Tree a -> b
tfoldr _ a Leaf                    = a
tfoldr f a (Branch _ left x right) = tfoldr f (f x $ tfoldr f a right) left

treeToList :: Tree a -> [a]    -- output list is sorted
treeToList = tfoldr (:) []
