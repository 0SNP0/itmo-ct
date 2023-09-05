#pragma once

#include <random>
#include <string>
#include <vector>

class Board
{
public:
    static Board create_goal(unsigned size);

    static Board create_random(unsigned size);

    Board() = default;

    explicit Board(const std::vector<std::vector<unsigned>> & data);

    Board(const Board & board) = default;

    std::size_t size() const;

    bool is_goal() const;

    unsigned hamming() const;

    unsigned manhattan() const;

    std::string to_string() const;

    bool is_solvable() const;

    std::vector<Board> possible_moves() const;

    const std::vector<unsigned> & operator[](size_t i) const
    {
        return board_table[i];
    }

    friend bool operator==(const Board & lhs, const Board & rhs)
    {
        return lhs.board_table == rhs.board_table;
    }

    friend bool operator!=(const Board & lhs, const Board & rhs)
    {
        return lhs.board_table != rhs.board_table;
    }

    friend std::ostream & operator<<(std::ostream & out, const Board & board)
    {
        return out << board.to_string();
    }

    friend bool operator<(const Board & lhs, const Board & rhs)
    {
        return lhs.hamming() + lhs.manhattan() > rhs.manhattan() + rhs.hamming();
    }

    using const_iterator = std::vector<std::vector<unsigned>>::const_iterator;
    const_iterator begin() const { return board_table.begin(); }
    const_iterator end() const { return board_table.end(); }

private:
    std::vector<std::vector<unsigned>> board_table;
    std::pair<unsigned, unsigned> empty_cell;

    explicit Board(const std::vector<std::vector<unsigned>> & data, const std::pair<unsigned, unsigned> & zero);
    void upd_empty_cell();
};

template <>
struct std::hash<Board> : public std::unary_function<Board, size_t>
{
    result_type operator()(const argument_type & board) const
    {
        size_t hash = 5381;
        for (const auto & line : board) {
            for (const auto & e : line) {
                hash = (hash << 5) + hash + e;
            }
        }
        return hash;
    }
};
