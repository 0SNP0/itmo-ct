#include "solver.h"

#include <algorithm>
#include <iostream>
#include <queue>
#include <unordered_map>
#include <unordered_set>

Solver::Solution Solver::solve(const Board & board)
{
    if (!board.is_solvable())
        return std::vector<Board>{};
    if (board.is_goal())
        return std::vector<Board>{board};

    std::unordered_set<Board> used;
    std::unordered_map<Board, Board> parent;
    std::unordered_map<Board, unsigned> distance;
    std::priority_queue<Board> open;

    Board goal = Board::create_goal(board.size());
    open.push(board);
    distance[board] = 0;

    while (!open.empty()) {
        Board cur = open.top();
        if (cur == goal) {
            std::vector<Board> res;
            for (; cur != board; cur = parent[cur]) {
                res.push_back(cur);
            }
            res.push_back(cur);
            std::reverse(res.begin(), res.end());
            return res;
        }
        used.insert(cur);
        open.pop();

        for (const Board & move : cur.possible_moves()) {
            auto move_dist = move.hamming() + move.manhattan() + distance.at(cur);
            if (!used.count(move) || move_dist < distance.at(move)) {
                open.push(move);
                parent[move] = cur;
                distance[move] = move_dist;
            }
        }
    }
    return std::vector<Board>{};
}
