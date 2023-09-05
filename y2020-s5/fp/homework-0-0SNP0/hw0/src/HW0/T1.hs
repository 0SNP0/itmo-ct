{-# LANGUAGE TypeOperators #-}

module HW0.T1
  ( assocEither
  , assocPair
  , distrib
  , flipIso
  , runIso
  , type (<->) (Iso)
  ) where

data a <-> b = Iso (a -> b) (b -> a)

flipIso :: (a <-> b) -> (b <-> a)
flipIso (Iso f g) = Iso g f

runIso :: (a <-> b) -> (a -> b)
runIso (Iso f _) = f


distrib :: Either a (b, c) -> (Either a b, Either a c)
distrib (Left a)       = (Left a, Left a)
distrib (Right (b, c)) = (Right b, Right c)

assocPair :: (a, (b, c)) <-> ((a, b), c)
assocPair = Iso fwd bwd where
  fwd :: (a, (b, c)) -> ((a, b), c)
  fwd (a, (b, c)) = ((a, b), c)
  bwd :: ((a, b), c) -> (a, (b, c))
  bwd ((a, b), c) = (a, (b, c))

assocEither :: Either a (Either b c) <-> Either (Either a b) c
assocEither = Iso fwd bwd where
  fwd :: Either a (Either b c) -> Either (Either a b) c
  fwd (Left a)          = Left (Left a)
  fwd (Right (Left b))  = Left (Right b)
  fwd (Right (Right c)) = Right c
  bwd :: Either (Either a b) c -> Either a (Either b c)
  bwd (Left (Left a))  = Left a
  bwd (Left (Right b)) = Right $ Left b
  bwd (Right c)        = Right $ Right c
