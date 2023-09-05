#pragma once

#include "bpnode.h"

#include <iterator>

template <class Key, class Value, class Less>
class BPIterator;
template <class Key, class Value, class Less>
class CBPIterator;

template <class Key, class Value, class Less = std::less<Key>>
class BPIterator : Less
{
    using Leaf = BPNode<Key, Value, Less>;
    using Pair = std::pair<const Key, Value>;
    using It = typename std::vector<Pair>::const_iterator;
    using Iterator = BPIterator<Key, Value, Less>;
    using CIterator = CBPIterator<Key, Value, Less>;

public:
    using difference_type = std::ptrdiff_t;
    using iterator_category = std::bidirectional_iterator_tag;
    using value_type = Pair;
    using pointer = Pair *;
    using reference = Pair &;

    BPIterator()
        : node(nullptr)
        , index(0)
    {
    }
    BPIterator(Leaf * leaf, size_t i)
        : node(leaf)
        , index(i)
    {
    }
    BPIterator(const CBPIterator<Key, Value, Less> & other)
        : node(other.node)
        , index(other.index)
    {
    }
    reference operator*() const
    {
        return node->cdata[index];
    }
    pointer operator->() const
    {
        return &(node->cdata[index]);
    }
    Iterator & operator++()
    {
        if (index + 1 < node->size || node->next == nullptr)
            ++index;
        else {
            node = node->next;
            index = 0;
        }
        return *this;
    }
    Iterator operator++(int)
    {
        auto tmp = *this;
        operator++();
        return tmp;
    }
    Iterator & operator--()
    {
        if (index > 0)
            --index;
        else if (node != nullptr) {
            node = node->prev;
            if (node == nullptr)
                index = 0;
            else
                index = node->size - 1;
        }
        return *this;
    }
    Iterator operator--(int)
    {
        auto tmp = *this;
        operator--();
        return tmp;
    }
    friend bool operator==(const Iterator & a, const Iterator & b)
    {
        return a.node == b.node && a.index == b.index;
    }
    friend bool operator!=(const Iterator & a, const Iterator & b)
    {
        return !operator==(a, b);
    }
    friend bool operator==(const Iterator & a, const CIterator & b)
    {
        return a.node == b.node && a.index == b.index;
    }
    friend bool operator!=(const Iterator & a, const CIterator & b)
    {
        return !operator==(a, b);
    }
    std::pair<Leaf *, size_t> get_pos()
    {
        return std::make_pair(node, index);
    }
    CBPIterator<Key, Value, Less> get_const() const
    {
        return CBPIterator<Key, Value, Less>(node, index);
    }

    Leaf * node;
    size_t index;
};

template <class Key, class Value, class Less = std::less<Key>>
class CBPIterator : Less
{
    using Leaf = BPNode<Key, Value, Less>;
    using CPair = const std::pair<const Key, Value>;
    using It = typename std::vector<std::pair<const Key, Value>>::const_iterator;
    using Iterator = BPIterator<Key, Value, Less>;
    using CIterator = CBPIterator<Key, Value, Less>;

public:
    using difference_type = std::ptrdiff_t;
    using iterator_category = std::bidirectional_iterator_tag;
    using value_type = CPair;
    using pointer = CPair *;
    using reference = CPair &;

    CBPIterator()
        : node(nullptr)
        , index(0)
    {
    }
    CBPIterator(Leaf * leaf, size_t i)
        : node(leaf)
        , index(i)
    {
    }
    CBPIterator(const BPIterator<Key, Value, Less> & other)
        : node(other.node)
        , index(other.index)
    {
    }
    reference operator*() const
    {
        return node->const_data[index];
    }
    pointer operator->() const
    {
        return &(node->const_data[index]);
    }
    CIterator & operator++()
    {
        if (index + 1 < node->size || node->next == nullptr)
            ++index;
        else {
            node = node->next;
            index = 0;
        }
        return *this;
    }
    CIterator operator++(int)
    {
        auto tmp = *this;
        operator++();
        return tmp;
    }
    CIterator & operator--()
    {
        if (index > 0)
            --index;
        else if (node != nullptr) {
            node = node->prev;
            if (node == nullptr)
                index = 0;
            else
                index = node->size - 1;
        }
        return *this;
    }
    CIterator operator--(int)
    {
        auto tmp = *this;
        operator--();
        return tmp;
    }
    friend bool operator==(const CIterator & a, const CIterator & b)
    {
        return a.node == b.node && a.index == b.index;
    }
    friend bool operator!=(const CIterator & a, const CIterator & b)
    {
        return !operator==(a, b);
    }
    friend bool operator==(const CIterator & a, const Iterator & b)
    {
        return a.node == b.node && a.index == b.index;
    }
    friend bool operator!=(const CIterator & a, const Iterator & b)
    {
        return !operator==(a, b);
    }
    /*std::pair<Leaf *, size_t> get_pos()
    {
        return std::make_pair(node, index);
    }*/

    Leaf * node;
    size_t index;
};