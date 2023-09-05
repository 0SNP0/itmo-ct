module HW1.T2
  ( N (..)
  , nFromNatural
  , nToNum
  , ncmp
  , nmult
  , nplus
  , nsub
    -- * Advanced
  , nEven
  , nOdd
  , ndiv
  , nmod
  ) where

import GHC.Natural (Natural)

data N = Z | S N

nplus :: N -> N -> N        -- addition
nplus a Z     = a
nplus a (S b) = S $ nplus a b

nmult :: N -> N -> N        -- multiplication
nmult _ Z     = Z
nmult a (S b) = nplus (nmult a b) a

nsub :: N -> N -> Maybe N   -- subtraction     (Nothing if result is negative)
nsub a Z         = Just a
nsub Z _         = Nothing
nsub (S a) (S b) = nsub a b

ncmp :: N -> N -> Ordering  -- comparison      (Do not derive Ord)
ncmp a b = case nsub a b of
  Nothing -> LT
  Just Z  -> EQ
  Just _  -> GT

nFromNatural :: Natural -> N
nFromNatural x = case x of
  0 -> Z
  _ -> S $ nFromNatural $ x - 1

nToNum :: Num a => N -> a
nToNum x = case x of
  Z     -> 0
  (S n) -> nToNum n + 1

-- | Advanced

nEven, nOdd :: N -> Bool    -- parity checking
nEven x = case x of
  Z -> True
  (S n) -> nOdd n
nOdd x = case x of
  Z -> False
  (S n) -> nEven n

ndiv :: N -> N -> N         -- integer division
ndiv a b = case nsub a b of
  Nothing  -> Z
  Just x -> S $ ndiv x b

nmod :: N -> N -> N         -- modulo operation
nmod a b = case nsub a b of
  Nothing -> a
  Just x  -> nmod x b
