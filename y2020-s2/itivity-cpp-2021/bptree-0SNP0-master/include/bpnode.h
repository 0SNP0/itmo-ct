#pragma once

#include <functional>
#include <iostream>

template <class Key, class Value, class Less = std::less<Key>>
class BPNode
{
    using Node = BPNode<Key, Value, Less>;
    using Pair = std::pair<Key, Value>;
    using CPair = std::pair<const Key, Value>;
    using ConstPair = const CPair;

private:
    std::vector<std::byte> storage;

public:
    const bool is_leaf;
    size_t key_num;
    Key * key;
    Node ** child;
    Pair * data;
    CPair * cdata;
    ConstPair * const_data;
    size_t size;
    Node * parent;
    Node *prev, *next;
    BPNode(bool leaf, size_t block_size)
        : storage(block_size)
        , is_leaf(leaf)
        , key_num(leaf ? (block_size / sizeof(Pair)) : (block_size / (sizeof(Key) + sizeof(Node *)) - 1))
        , size(0)
        , parent(nullptr)
        , prev(nullptr)
        , next(nullptr)
    {
        if (is_leaf) {
            data = new (&storage[0]) Pair[key_num];
            cdata = new (&storage[0]) CPair[key_num];
            const_data = new (&storage[0]) ConstPair[key_num];
        }
        else {
            key = new (&storage[0]) Key[key_num];
            child = new (&storage[key_num * sizeof(Key)]) Node *[key_num + 1];
        }
    }
    BPNode(const Node & other)
        : storage(other.storage.size())
        , is_leaf(other.is_leaf)
        , key_num(other.key_num)
        , size(other.size)
        , parent(other.parent)
        , prev(other.prev)
        , next(other.next)
    {
        if (is_leaf) {
            data = new (&storage[0]) Pair[key_num];
            cdata = new (&storage[0]) CPair[key_num];
            const_data = new (&storage[0]) ConstPair[key_num];
        }
        else {
            key = new (&storage[0]) Key[key_num];
            child = new (&storage[key_num * sizeof(Key)]) Node *[key_num + 1];
        }
        storage.assign(other.storage.begin(), other.storage.end());
        if (!is_leaf) {
            for (size_t i = 0; i <= size; ++i) {
                child[i] = new Node(*child[i]);
                child[i]->parent = this;
            }
        }
    }
    ~BPNode()
    {
        if (!is_leaf && size > 0) {
            for (size_t i = 0; i <= size; ++i) {
                delete child[i];
            }
        }
    }
    /*void print() {
        std::cout << "[ ";
        if (is_leaf) {
            for (size_t i = 0; i < size; ++i) {
                std::cout << data[i].first << ' ';
            }
        }
        else {
            for (size_t i = 0; i < size; ++i) {
                child[i]->print();
                std::cout << key[i] << ' ';
            }
            child[size]->print();
        }
        std::cout << "] ";
    }*/
};