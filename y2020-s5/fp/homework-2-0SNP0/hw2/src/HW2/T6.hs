{-# LANGUAGE DerivingStrategies         #-}
{-# LANGUAGE GeneralizedNewtypeDeriving #-}
{-# LANGUAGE InstanceSigs               #-}

module HW2.T6
  ( ParseError (..)
  , Parser (..)
  , pAbbr
  , pChar
  , pEof
  , parseError
  , parseExpr
  , runP
  ) where

import Control.Applicative
import Control.Monad
import Data.Char (digitToInt, isDigit, isSpace, isUpper)
import Data.Functor
import Data.Scientific
import GHC.Natural (Natural)
import HW2.T1 (Annotated ((:#)), Except (Error, Success))
import HW2.T4 (Expr (Op, Val), Prim (Add, Div, Mul, Sub))
import HW2.T5 (ExceptState (ES, runES))

newtype ParseError = ErrorAtPos Natural

newtype Parser a = P (ExceptState ParseError (Natural, String) a) deriving newtype (Functor, Applicative, Monad)

runP :: Parser a -> String -> Except ParseError a
runP (P es) str = case runES es (0, str) of
  Error e          -> Error e
  Success (a :# _) -> Success a

-- ErrorAtPos when the string is empty
-- Creating pair with incremented pos and tail of given string when a character is consumed
pChar :: Parser Char
pChar = P $ ES parse where
  parse (pos, [])     = Error $ ErrorAtPos pos
  parse (pos, c : cs) = Success (c :# (pos + 1, cs))

parseError :: Parser a
parseError = P $ ES $ \(pos, _) -> Error $ ErrorAtPos pos

instance Alternative Parser where
  empty = parseError
  (<|>) :: Parser a -> Parser a -> Parser a
  (P (ES p1)) <|> (P (ES p2)) = P $ ES $ \state -> case p1 state of
    Error _   -> p2 state
    Success x -> Success x

instance MonadPlus Parser   -- No methods.

pEof :: Parser ()
pEof = P $ ES check where
  check (pos, []) = Success (() :# (pos, []))
  check (pos, _)  = Error $ ErrorAtPos pos

pAbbr :: Parser String
pAbbr = do
  abbr <- some (mfilter Data.Char.isUpper pChar)
  pEof
  pure abbr

parseExpr :: String -> Except ParseError Expr
parseExpr = runP $ pExpr <* pEof

pExpr, pAdd, pSub, pMul, pDiv, pUnary, pNumber :: Parser Expr
pExpr = pAdd
pAdd = pBinary '+' Add pSub
pSub = pBinary '-' Sub pMul
pMul = pBinary '*' Mul pDiv
pDiv = pBinary '/' Div pUnary
pUnary = pNumber <|> pBrackets

pNumber = do
  pSpaces
  x <- pDouble <|> pToVal
  pSpaces
  pure x

pBinary :: Char -> (Expr -> Expr -> Prim Expr) -> Parser Expr -> Parser Expr
pBinary ch op low = do
  pSpaces
  left <- low
  pSpaces
  rest <- many $ pExpect ch *> low
  pSpaces
  pure $ foldl1 operator $ left : rest where
    operator x y = Op $ op x y

pSpaces, pInteger :: Parser String
pSpaces = many $ mfilter Data.Char.isSpace pChar
pInteger = some pDigit

pDigit :: Parser Char
pDigit = mfilter Data.Char.isDigit pChar

pDouble, pToVal, pBrackets :: Parser Expr
pDouble = do
  int <- pInteger
  pExpect '.'
  Val . toRealFloat . toScientific int <$> pInteger where
    toScientific num part = scientific (toInt $ num ++ part) $ - length part

pToVal = Val . fromInteger . toInt <$> pInteger

pBrackets = do
  pSpaces
  pExpect '('
  pSpaces
  res <- pAdd
  pSpaces
  pExpect ')'
  pure res

pExpect :: Char -> Parser String
pExpect c = do
  ch <- mfilter (== c) pChar
  pure [ch]

toInt :: String -> Integer
toInt s = foldl step 0 $ map (toInteger . digitToInt) s where
  step prev next = 10 * prev + next
