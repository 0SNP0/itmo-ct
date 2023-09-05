module HW1.T3
  ( Tree (..)
  , mkBranch
  , tFromList
  , tdepth
  , tinsert
  , tmember
  , tsize
  ) where

import GHC.Generics

data Tree a = Leaf | Branch (Int, Int) (Tree a) a (Tree a)

-- | Size of the tree, O(1).
tsize :: Tree a -> Int
tsize Leaf                     = 0
tsize (Branch (size, _) _ _ _) = size

-- | Depth of the tree.
tdepth :: Tree a -> Int
tdepth Leaf                      = 0
tdepth (Branch (_, depth) _ _ _) = depth

-- | Check if the element is in the tree, O(log n)
tmember :: Ord a => a -> Tree a -> Bool
tmember _ Leaf = False
tmember elem (Branch _ left x right)
    | elem == x = True
    | elem < x  = tmember elem left
    | otherwise = tmember elem right

-- | Insert an element into the tree, O(log n)
tinsert :: Ord a => a -> Tree a -> Tree a
tinsert elem t = if tmember elem t then t else tinsert' elem t where
  tinsert' elem Leaf = mkBranch Leaf elem Leaf
  tinsert' elem t@(Branch _ left x right) =
    balance . recalc $ if elem < x
      then updLeft t $ tinsert elem left
      else updRight t $ tinsert elem right

-- | Build a tree from a list, O(n log n)
tFromList :: Ord a => [a] -> Tree a
tFromList = (foldl . flip) tinsert Leaf

mkBranch :: Tree a -> a -> Tree a -> Tree a
mkBranch left x right = Branch (1 + tsize left + tsize right, 1 + max (tdepth left) (tdepth right)) left x right

recalc :: Tree a -> Tree a
recalc (Branch _ left x right) = mkBranch left x right

getRight, getLeft :: Tree a -> Tree a
getRight (Branch _ _ _ right) = right
getLeft (Branch _ left _ _) = left

updRight :: Tree a -> Tree a -> Tree a
updRight (Branch p left x _) right = Branch p left x right

updLeft :: Tree a -> Tree a -> Tree a
updLeft (Branch p _ x right) left = Branch p left x right

getBalance :: Tree a -> Int
getBalance (Branch _ left _ right) = tdepth right - tdepth left

rotateRight :: Tree a -> Tree a
rotateRight t@(Branch _ l _ r) = recalc $ updRight l $ recalc $ updLeft t $ getRight l

rotateLeft :: Tree a -> Tree a
rotateLeft t@(Branch _ l _ r) = recalc $ updLeft r $ recalc $ updRight t $ getLeft r

balance :: Tree a -> Tree a
balance t@(Branch _ l _ r) = case getBalance t of
  -2 -> rotateRight $ if getBalance l > 0 then updLeft t $ rotateLeft l else t
  2  -> rotateLeft  $ if getBalance r < 0 then updRight t $ rotateRight r else t
  _  -> t
