in_sieve(I, MAX_N, S) :-
    I =< MAX_N, assert(composite(I)), NI is I + S,
    in_sieve(NI, MAX_N, S).
sieve(I, MAX_N) :- I > MAX_N, !.
sieve(I, MAX_N) :-
    not(composite(I)), I2 is I * I, I2 =< MAX_N,
    in_sieve(I2, MAX_N, I).
sieve(I, MAX_N) :-
    II is I + 1, sieve(II, MAX_N).
init(MAX_N) :- sieve(2, MAX_N).

composite(1).
prime(2).
prime(N) :- N > 2, not(composite(N)).

divisors(1, _, []).
divisors(N, P, [H | T]) :-
    prime(H), P =< H,
    divisors(R, H, T), N is R * H.

find_prime(N, D, [N]) :- D * D > N, !.
find_prime(N, D, [H | T]) :-
    0 is mod(N, D), !, H is D, NN is div(N, D),
    find_prime(NN, D, T).
find_prime(N, D, [H | T]) :-
    mod(N, D) > 0, !, DD is D + 1,
    find_prime(N, DD, [H | T]).

prime_divisors(1, []) :- !.
prime_divisors(N, Divisors) :-
    integer(N), find_prime(N, 2, Divisors).
prime_divisors(N, Divisors) :-
    not(integer(N)), divisors(N, 2, Divisors).

common_elems(E, X, Y) :- member(E, X), member(E, Y).
gcd(A, B, 1) :-
    prime_divisors(A, X), prime_divisors(B, Y),
    not(common_elems(E, X, Y)), !.
gcd(A, B, GCD) :-
    prime_divisors(A, L1), prime_divisors(B, L2),
    common_elems(E, L1, L2),
    X is div(A, E), Y is div(B, E),
    gcd(X, Y, D), GCD is D * E, !.
