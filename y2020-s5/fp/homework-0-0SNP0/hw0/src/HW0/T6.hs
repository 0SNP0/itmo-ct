module HW0.T6 where

import Data.Char
import HW0.T1 (distrib)

a = distrib (Left ("AB" ++ "CD" ++ "EF"))     -- distrib from HW0.T1
b = map isSpace "Hello, World"
c = if 1 > 0 || error "X" then "Y" else "Z"

a_whnf = (Left ("AB" ++ "CD" ++ "EF"), Left ("AB" ++ "CD" ++ "EF"))
b_whnf = (isSpace 'H') : map isSpace "ello, World"
c_whnf = "Y"
