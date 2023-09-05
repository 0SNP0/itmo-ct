#include "board.h"

#include <algorithm>
#include <iostream>

static std::default_random_engine rndgen(std::random_device{}());

Board Board::create_goal(const unsigned size)
{
    std::vector<std::vector<unsigned>> res(size);
    unsigned cnt = 1;
    for (auto & line : res) {
        line.resize(size);
        for (auto & e : line) {
            e = cnt++;
        }
    }
    res[size - 1][size - 1] = 0;
    return Board(res);
}

Board Board::create_random(const unsigned size)
{
    std::vector<unsigned> initial(size * size);
    std::iota(initial.begin(), initial.end(), 0);
    std::shuffle(initial.begin(), initial.end(), rndgen);

    std::vector<std::vector<unsigned>> res(size);
    auto it = initial.begin();
    for (unsigned i = 0; i < size; ++i) {
        res[i] = std::vector<unsigned>(it, it + size);
        it += size;
    }
    return Board(res);
}

Board::Board(const std::vector<std::vector<unsigned>> & data)
    : board_table(data)
{
    upd_empty_cell();
}

Board::Board(const std::vector<std::vector<unsigned int>> & data, const std::pair<unsigned int, unsigned int> & zero)
    : board_table(data)
    , empty_cell(zero)
{
}

std::size_t Board::size() const
{
    return board_table.size();
}

bool Board::is_goal() const
{
    unsigned cnt = 0;
    const unsigned m = size() * size() - 1;
    for (size_t i = 0; i < size(); ++i)
        for (size_t j = 0; j < size(); ++j) {
            cnt = cnt < m ? cnt + 1 : 0;
            if (board_table[i][j] != cnt)
                return false;
        }
    return true;
}

unsigned Board::hamming() const
{
    unsigned sum = 0, cnt = 0;
    const unsigned m = size() * size() - 1;
    for (size_t i = 0; i < size(); ++i)
        for (size_t j = 0; j < size(); ++j) {
            cnt = cnt < m ? cnt + 1 : 0;
            if (board_table[i][j] != cnt)
                ++sum;
        }
    return sum;
}

unsigned Board::manhattan() const
{
    unsigned sum = 0;
    for (size_t i = 0; i < size(); ++i)
        for (size_t j = 0; j < size(); ++j) {
            if (board_table[i][j] != 0)
                sum += abs(static_cast<int>((board_table[i][j] - 1) / size() - i)) +
                        abs(static_cast<int>((board_table[i][j] - 1) % size() - j));
        }
    return sum;
}

std::string Board::to_string() const
{
    std::string res;
    for (size_t i = 0; i < size(); ++i)
        for (size_t j = 0; j < size(); ++j) {
            res.append(std::to_string(board_table[i][j])).append((j + 1 == size()) ? "\n" : " ");
        }
    return res;
}

bool Board::is_solvable() const
{
    if (is_goal())
        return true;
    bool res = true;
    for (size_t i = 0; i < size(); ++i)
        for (size_t j = 0; j < size(); ++j) {
            if (board_table[i][j] != 0) {
                for (size_t k = 0; k < i; ++k)
                    for (size_t l = 0; l < size(); ++l)
                        res = res != (board_table[k][l] > board_table[i][j]);
                for (size_t k = 0; k < j; ++k)
                    res = res != (board_table[i][k] > board_table[i][j]);
            }
            else if (size() % 2 == 0)
                res = res == (i % 2 == 1);
        }
    return res;
}

void Board::upd_empty_cell()
{
    for (size_t i = 0; i < size(); ++i)
        for (size_t j = 0; j < size(); ++j)
            if (board_table[i][j] == 0) {
                empty_cell = std::make_pair(i, j);
                return;
            }
}
std::vector<Board> Board::possible_moves() const
{
    auto &x = empty_cell.first, &y = empty_cell.second;
    std::vector<Board> res;
    res.reserve(4);
    std::vector<std::vector<unsigned>> copy(board_table);
    for (auto & i : {-1, 1}) {
        if (x + i < size()) {
            std::swap(copy[x][y], copy[x + i][y]);
            res.push_back(Board(copy, {x + i, y}));
            std::swap(copy[x][y], copy[x + i][y]);
        }
        if (y + i < size()) {
            std::swap(copy[x][y], copy[x][y + i]);
            res.push_back(Board(copy, {x, y + i}));
            std::swap(copy[x][y], copy[x][y + i]);
        }
    }
    return res;
}
