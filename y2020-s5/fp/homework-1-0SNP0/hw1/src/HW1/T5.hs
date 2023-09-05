module HW1.T5
  ( joinWith
  , splitOn
  ) where

import Data.List.NonEmpty as NonEmpty

splitOn :: Eq a => a -> [a] -> NonEmpty [a]
splitOn sep s = NonEmpty.fromList $ if null s
  then [[]]
  else cur' : acc' where
    splitOn' x (cur, acc)
      | x == sep  = ([], cur:acc)
      | otherwise = (x:cur, acc)
    (cur', acc') = foldr splitOn' ([], []) s

joinWith :: a -> NonEmpty [a] -> [a]
joinWith sep list = joinWith' sep (NonEmpty.toList list) where
  joinWith' :: a -> [[a]] -> [a]
  joinWith' sep []     = []
  joinWith' sep [w]    = w
  joinWith' sep (w:ws) = w ++ (sep : joinWith' sep ws)
