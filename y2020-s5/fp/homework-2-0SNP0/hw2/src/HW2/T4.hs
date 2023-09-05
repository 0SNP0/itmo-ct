module HW2.T4
  ( Expr (..)
  , Prim (..)
  , State (..)
  , eval
  , joinState
  , mapState
  , modifyState
  , wrapState
  ) where

import Control.Monad
import HW2.T1

newtype State s a = S { runS :: s -> Annotated s a }

mapState :: (a -> b) -> State s a -> State s b
mapState f state = S $ mapAnnotated f . runS state

wrapState :: a -> State s a
wrapState a = S $ \state -> a :# state

joinState :: State s (State s a) -> State s a
joinState state = S $ join where
  join s = extract $ runS state s where
    extract (a :# s') = runS a s'

modifyState :: (s -> s) -> State s ()
modifyState f = S $ \s -> () :# f s

instance Functor (State s) where
  fmap = mapState

instance Applicative (State s) where
  pure = wrapState
  p <*> q = Control.Monad.ap p q

instance Monad (State s) where
  m >>= f = joinState $ fmap f m

data Prim a =
    Add a a      -- (+)
  | Sub a a      -- (-)
  | Mul a a      -- (*)
  | Div a a      -- (/)
  | Abs a        -- abs
  | Sgn a        -- signum

data Expr = Val Double | Op (Prim Expr)

instance Num Expr where
  x + y = Op $ Add x y
  x - y = Op $ Sub x y
  x * y = Op $ Mul x y
  abs x = Op $ Abs x
  signum x = Op $ Sgn x
  fromInteger x = Val $ fromInteger x

instance Fractional Expr where
  x / y = Op $ Div x y
  fromRational x = Val $ fromRational x

eval :: Expr -> State [Prim Double] Double
eval (Val x) = wrapState x
eval (Op op) = opEval op

binary :: (Double -> Double -> Double) ->
  (Double -> Double -> Prim Double) ->
  Expr -> Expr -> State [Prim Double] Double
binary f prim a b = do
  x <- eval a
  y <- eval b
  modifyState (prim x y :)
  wrapState $ f x y

unary :: (Double -> Double) -> (Double -> Prim Double) -> Expr -> State [Prim Double] Double
unary f prim a = do
  x <- eval a
  modifyState (prim x :)
  wrapState $ f x

opEval :: Prim Expr -> State [Prim Double] Double
opEval (Add a b) = binary (+) Add a b
opEval (Sub a b) = binary (-) Sub a b
opEval (Mul a b) = binary (*) Mul a b
opEval (Div a b) = binary (/) Div a b
opEval (Abs a)   = unary abs Abs a
opEval (Sgn a)   = unary signum Sgn a
