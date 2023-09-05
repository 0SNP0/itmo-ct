#pragma once

#include "board.h"

class Solver
{
    class Solution
    {
    public:
        std::size_t moves() const { return m_moves.empty() ? 0 : m_moves.size() - 1; }

        using const_iterator = std::vector<Board>::const_iterator;

        const_iterator begin() const { return m_moves.begin(); }

        const_iterator end() const { return m_moves.end(); }

        Solution(const std::vector<Board> & moves)
            : m_moves(moves)
        {
        }

    private:
        std::vector<Board> m_moves;
    };

public:
    static Solution solve(const Board & initial);
};
