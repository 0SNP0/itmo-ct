node(Pair, t(Pair, Rand, nil, nil)) :- rand_int(1000, Rand).

merge(T, nil, T) :- T \= nil.
merge(nil, T, T).
merge(t((AKey, AValue), AW, ALeft, ARight),t((BKey, BValue), BW, BLeft, BRight), Result) :-
    (AW < BW -> merge(t((AKey, AValue), AW, ALeft, ARight), BLeft, Tmp), Result = t((BKey, BValue), BW, Tmp, BRight);
    merge(ARight, t((BKey, BValue), BW, BLeft, BRight), Tmp), Result = t((AKey, AValue), AW, ALeft, Tmp)).

split(nil, Key, nil, nil).
split(t((K, V), Weight, LeftRef, RightRef), Key, Left, Right) :-
    (K < Key -> split(RightRef, Key, Tmp0, Right), Left = t((K, V), Weight, LeftRef, Tmp0);
    split(LeftRef, Key, Left, Tmp1), Right = t((K, V), Weight, Tmp1, RightRef)).

map_remove(Map, Key, Result) :-
    split(Map, Key, Left, Right), split(Right, Key + 1, _, Removed),
    merge(Left, Removed, Result).

insert((Key, Value), nil, Result) :- node((Key, Value), Result).
insert((Key, Value), Tree, Result) :-
    Tree \= nil,
    split(Tree, Key, Left, Right), node((Key, Value), Node),
    merge(Left, Node, Inserted), merge(Inserted, Right, Result).
map_put(Map, Key, Value, Result) :-
    map_remove(Map, Key, Removed), insert((Key, Value), Removed, Result).

buildTree([], Result, Result).
buildTree([(K, V) | R], T, Result) :-
    map_put(T, K, V, NewTree), buildTree(R, NewTree, Result).
map_build(List, Map) :- buildTree(List, nil, Map).

map_get(t((Key, Value), _, _, _), Key, Value).
map_get(t((NodeKey, NodeValue), _, LeftRef, RightRef), Key, Value) :-
    Key \= NodeKey,
    (NodeKey < Key -> map_get(RightRef, Key, Value);
    map_get(LeftRef, Key, Value)).

map_getLast(t((Key, Value), _, _, nil), (Key, Value)).
map_getLast(t((K, V), _, _, RightRef), (Key, Value)) :- map_getLast(RightRef, (Key, Value)).

map_removeLast(nil, nil).
map_removeLast(Map, Result) :- map_getLast(Map, (Key, Value)), map_remove(Map, Key, Result).
%map_removeLast(t(Pair, _, LeftRef, nil), LeftRef);
%map_removeLast(t(Pair, _, LeftRef, RightRef), t(Pair, _, LeftRef, NewRight)) :- map_removeLast(RightRef, NewRight).