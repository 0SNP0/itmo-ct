module HW2.T5
  ( EvaluationError (..)
  , ExceptState (..)
  , eval
  , joinExceptState
  , mapExceptState
  , modifyExceptState
  , throwExceptState
  , wrapExceptState
  ) where

import Control.Monad
import HW2.T1
import HW2.T2
import HW2.T4 hiding (binary, eval, opEval, unary)

newtype ExceptState e s a = ES { runES :: s -> Except e (Annotated s a) }

mapExceptState :: (a -> b) -> ExceptState e s a -> ExceptState e s b
mapExceptState f es = ES $ mapExcept (mapAnnotated f) . runES es

wrapExceptState :: a -> ExceptState e s a
wrapExceptState a = ES $ \s -> Success (a :# s)

joinExceptState :: ExceptState e s (ExceptState e s a) -> ExceptState e s a
joinExceptState state = ES $ \s -> case runES state s of
  Error e           -> Error e
  Success (a :# s') -> runES a s'

modifyExceptState :: (s -> s) -> ExceptState e s ()
modifyExceptState f = ES $ \s -> Success (() :# f s)

throwExceptState :: e -> ExceptState e s a
throwExceptState e = ES $ \_ -> Error e

instance Functor (ExceptState e s) where
  fmap = mapExceptState

instance Applicative (ExceptState e s) where
  pure = wrapExceptState
  p <*> q = Control.Monad.ap p q

instance Monad (ExceptState e s) where
  m >>= f = joinExceptState $ fmap f m

data EvaluationError = DivideByZero

eval :: Expr -> ExceptState EvaluationError [Prim Double] Double
eval (Val x) = pure x
eval (Op op) = opEval op

opEval :: Prim Expr -> ExceptState EvaluationError [Prim Double] Double
opEval (Add x y) = binary Add (binWrap (+)) x y
opEval (Sub x y) = binary Sub (binWrap (-)) x y
opEval (Mul x y) = binary Mul (binWrap (*)) x y
opEval (Div x y) = binary Div divCheck x y
opEval (Abs x)   = unary abs Abs x
opEval (Sgn x)   = unary signum Sgn x

binary :: (Double -> Double -> Prim Double) ->
  (Double -> Double -> ExceptState EvaluationError [Prim Double] Double) ->
  Expr -> Expr -> ExceptState EvaluationError [Prim Double] Double
binary f prim a b = do
  x <- eval a
  y <- eval b
  _ <- modifyExceptState (f x y :)
  prim x y

unary :: (Double -> Double) -> (Double -> Prim Double) -> Expr -> ExceptState EvaluationError [Prim Double] Double
unary f prim a = do
  x <- eval a
  modifyExceptState (prim x :)
  pure $ f x

binWrap :: (Double -> Double -> Double) -> Double -> Double -> ExceptState EvaluationError [Prim Double] Double
binWrap op x y = pure (op x y)

divCheck :: Double -> Double -> ExceptState EvaluationError [Prim Double] Double
divCheck _ 0 = throwExceptState DivideByZero
divCheck a b = pure (a / b)
