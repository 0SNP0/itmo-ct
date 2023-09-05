#pragma once

#include "bpiterator.h"

#include <functional>
#include <sstream>
#include <stack>

template <class T>
std::string toString(T obj);

template <class Key, class Value, class Less = std::less<Key>>
class BPTree
{
    static constexpr std::size_t block_size = 4096;
    using Node = BPNode<Key, Value, Less>;
    using Pair = std::pair<const Key, Value>;

public:
    using key_type = Key;
    using mapped_type = Value;
    using value_type = std::pair<const Key, Value>;
    using reference = value_type &;
    using const_reference = const value_type &;
    using pointer = value_type *;
    using const_pointer = const value_type *;
    using size_type = std::size_t;

    using iterator = BPIterator<Key, Value, Less>;
    using const_iterator = CBPIterator<Key, Value, Less>;

    /*iterator createIterator(Node * node, size_t pos) {
        return iterator(node, pos);
    }*/
    /*void print() {
        tree_root->print();
        std::cout << std::endl;
    }*/

    BPTree()
        : tree_root(new Node(true, block_size))
        , first(tree_root)
        , last(tree_root)
        , tree_size(0)
    {
    }
    BPTree(std::initializer_list<std::pair<const Key, Value>> list)
    {
        BPTree();
        insert(list);
    }
    BPTree(const BPTree & other)
        : tree_root(new Node(*other.tree_root))
        , tree_size(other.tree_size)
        , first(tree_root)
        , last(tree_root)
    {
        if (!tree_root->is_leaf)
            upd_links();
    }
    BPTree(BPTree && other)
        : tree_root(new Node(*other.tree_root))
        , tree_size(other.tree_size)
        , first(tree_root)
        , last(tree_root)
    {
        if (!tree_root->is_leaf)
            upd_links();
    }
    ~BPTree()
    {
        delete tree_root;
    }

    iterator begin()
    {
        return iterator(first, 0);
    }
    const_iterator cbegin() const
    {
        return const_iterator(first, 0);
    }
    const_iterator begin() const
    {
        return const_iterator(first, 0);
    }
    iterator end()
    {
        return iterator(last, last->size);
    }
    const_iterator cend() const
    {
        return const_iterator(last->next, last->size);
    }
    const_iterator end() const
    {
        return const_iterator(last->next, last->size);
    }

    bool empty() const
    {
        return tree_size == 0;
    }
    size_type size() const
    {
        return tree_size;
    }
    void clear()
    {
        delete tree_root;
        BPTree();
    }

    size_type count(const Key & key) const
    {
        return find_key(key).second ? 1 : 0;
    }
    bool contains(const Key & key) const
    {
        return find_key(key).second;
    }
    std::pair<iterator, iterator> equal_range(const Key & key)
    {
        return std::make_pair(lower_bound(key), upper_bound(key));
    }
    std::pair<const_iterator, const_iterator> equal_range(const Key & key) const
    {
        return std::make_pair(lower_bound(key), upper_bound(key));
    }
    iterator lower_bound(const Key & key)
    {
        if (tree_size == 0)
            return end();
        iterator it = iterator(find_leaf(key), 0);
        for (; it != end(); ++it) {
            if (!Less{}(it->first, key))
                break;
        }
        return it;
    }
    const_iterator lower_bound(const Key & key) const
    {
        if (tree_size == 0)
            return cend();
        const_iterator it = const_iterator(find_leaf(key), 0);
        for (; it != cend(); ++it) {
            if (!Less{}(it->first, key))
                break;
        }
        return it;
    }
    iterator upper_bound(const Key & key)
    {
        if (tree_size == 0)
            return end();
        iterator it = iterator(find_leaf(key), 0);
        for (; it != end(); ++it) {
            if (Less{}(key, it->first))
                break;
        }
        return it;
    }
    const_iterator upper_bound(const Key & key) const
    {
        if (tree_size == 0)
            return cend();
        const_iterator it = const_iterator(find_leaf(key), 0);
        for (; it != cend(); ++it) {
            if (Less{}(key, it->first))
                break;
        }
        return it;
    }
    iterator find(const key_type & key)
    {
        auto found = find_key(key);
        //        if (found.first.get_pos().first == nullptr) std::cerr << "nullptr\n";
        //        else std::cerr << "found " << found.first->first << std::endl;
        return found.second ? found.first : end();
    }
    const_iterator find(const key_type & key) const
    {
        auto found = find_key(key);
        return found.second ? found.first.get_const() : cend();
    }

    // 'at' method throws std::out_of_range if there is no such key
    Value & at(const Key & key)
    {
        auto res = find_key(key);
        if (res.second == false)
            throw std::out_of_range("No such key: " + toString(key));
        return res.first->second;
    }
    const Value & at(const Key & key) const
    {
        auto res = find_key(key);
        if (res.second == false)
            throw std::out_of_range("No such key: " + toString(key));
        return res.first.get_const()->second;
    }

    // '[]' operator inserts a new element if there is no such key
    Value & operator[](const Key & key)
    {
        return add(key, Value()).first->second;
    }
    Value & operator[](Key && key)
    {
        return add(key, Value()).first->second;
    }

    std::pair<iterator, bool> insert(const Key & key, const Value & val) // NB: a digression from std::map
    {
        return add(key, val);
    }
    std::pair<iterator, bool> insert(const Key & key, Value && val)
    {
        return add(key, val);
    }
    std::pair<iterator, bool> insert(Key && key, Value && val)
    {
        return add(key, val);
    }
    void insert(std::initializer_list<value_type> list)
    {
        for (value_type pair : list) {
            add(pair.first, pair.second);
        }
    }
    template <class InputIt>
    void insert(InputIt begin, InputIt end)
    {
        for (; begin != end; ++begin) {
            add(begin->first, begin->second);
        }
    }
    iterator erase(iterator it)
    {
        if (it == end())
            return it;
        return del(it);
    }
    iterator erase(const_iterator it)
    {
        if (it == cend())
            return it;
        return del(it);
    }
    iterator erase_range(const Key l, const Key r)
    {
        for (iterator it = lower_bound(l); it != end(); it = lower_bound(l)) {
            if (!Less{}(it->first, r))
                return it;
            del(it);
        }
        return end();
    }
    void erase_range(const Key l)
    {
        for (iterator it = lower_bound(l); it != end(); it = lower_bound(l)) {
            del(it);
        }
    }
    const_iterator erase(const_iterator l, const_iterator r)
    {
        if (l == cend())
            return cend();
        if (r == cend()) {
            erase_range(l->first);
            return cend();
        }
        else {
            return erase_range(l->first, r->first);
        }
    }
    void erase(iterator l, iterator r)
    {
        if (l == end())
            return;
        if (r == end())
            erase_range(l->first);
        else
            erase_range(l->first, r->first);
    }
    size_type erase(const Key & key)
    {
        iterator found = find(key);
        if (found != end()) {
            del(found);
            return 1;
        }
        else
            return 0;
    }

private:
    Node *tree_root, *first, *last;
    size_t tree_size;

    Node * find_leaf(const Key & key) const
    {
        Node * cur = tree_root;
        while (!cur->is_leaf) {
            size_t i;
            for (i = 0; i < cur->size && !Less{}(key, cur->key[i]); ++i)
                ;
            cur = cur->child[i];
        }
        return cur;
    }
    std::pair<iterator, bool> find_key(const Key & key) const
    {
        Node * leaf = find_leaf(key);
        for (size_t i = 0; i < leaf->size; ++i) {
            if (leaf->data[i].first == key) {
                return std::make_pair(iterator(leaf, i), true);
            }
        }
        return std::make_pair(end(), false);
    }
    std::pair<iterator, bool> add(const Key & key, const Value & val)
    {
        Node * leaf = find_leaf(key);
        size_t pos;
        for (pos = 0; pos < leaf->size && Less{}(leaf->data[pos].first, key); ++pos)
            ;
        if (pos < leaf->size && leaf->data[pos].first == key) {
            return std::make_pair(iterator(leaf, pos), false);
        }
        for (size_t i = leaf->size; i > pos; --i) {
            leaf->data[i] = leaf->data[i - 1];
        }
        leaf->data[pos] = std::make_pair(key, val);
        ++leaf->size;
        if (leaf->size >= leaf->key_num) {
            split(leaf);
            if (pos >= leaf->size) {
                pos -= leaf->size;
                leaf = leaf->next;
            }
        }
        if (last->next != nullptr) {
            last = last->next;
        }
        ++tree_size;
        return std::make_pair(iterator(leaf, pos), true);
    }
    Node * split(Node * node)
    {
        const bool is_leafs = node->is_leaf;
        Node * new_node = new Node(is_leafs, block_size);
        if (is_leafs) {
            new_node->next = node->next;
            if (node->next != nullptr)
                node->next->prev = new_node;
            node->next = new_node;
            new_node->prev = node;
        }
        Key mid_key;
        if (is_leafs) {
            mid_key = node->data[node->size / 2].first;
            new_node->size = node->size / 2 + node->size % 2;
            node->size -= new_node->size;
            for (size_t i = 0; i < new_node->size; ++i) {
                new_node->data[i] = node->data[i + node->size];
            }
        }
        else {
            mid_key = node->key[node->size / 2];
            auto old_size = node->size;
            node->size /= 2;
            new_node->size = old_size - node->size - 1;
            for (size_t i = 0; i < new_node->size; ++i) {
                new_node->key[i] = node->key[i + node->size + 1];
                new_node->child[i] = node->child[i + node->size + 1];
                new_node->child[i]->parent = new_node;
            }
            new_node->child[new_node->size] = node->child[old_size];
            new_node->child[new_node->size]->parent = new_node;
        }
        if (node == tree_root) {
            tree_root = new Node(false, block_size);
            tree_root->key[0] = mid_key;
            tree_root->child[0] = node;
            tree_root->child[1] = new_node;
            tree_root->size = 1;
            node->parent = new_node->parent = tree_root;
        }
        else {
            Node * parent = new_node->parent = node->parent;
            size_t pos;
            for (pos = 0; pos < parent->size && Less{}(parent->key[pos], mid_key); ++pos)
                ;
            for (auto i = parent->size; i > pos; --i) {
                parent->key[i] = parent->key[i - 1];
                parent->child[i + 1] = parent->child[i];
            }
            parent->key[pos] = mid_key;
            parent->child[pos + 1] = new_node;
            ++parent->size;
            if (parent->size >= parent->key_num) {
                Node * splitted = split(parent);
                int in = 0;
                for (size_t i = 0; i < parent->size; ++i) {
                    if (parent->key[i] == mid_key) {
                        in = -1;
                        break;
                    }
                }
                for (size_t i = 0; i < splitted->size; ++i) {
                    if (splitted->key[i] == mid_key) {
                        in = 1;
                        break;
                    }
                }
                if (in >= 0) {
                    new_node->parent = splitted;
                    if (in > 0) {
                        node->parent = splitted;
                    }
                }
            }
        }
        return new_node;
    }
    iterator del(iterator it)
    {
        const Key key = it->first;
        std::pair<Node *, size_t> position = it.get_pos();
        Node * leaf = position.first;
        size_t pos = position.second;
        --leaf->size;
        --tree_size;
        for (size_t i = pos; i < leaf->size; ++i) {
            leaf->data[i] = leaf->data[i + 1];
        }
        if (leaf->size < leaf->key_num / 2 && leaf != tree_root) {
            node_upd(leaf->parent);
        }
        return lower_bound(key);
    }
    void node_upd(Node * node)
    {
        for (size_t pos = 0; pos < node->size; ++pos) {
            Node *left = node->child[pos], *right = node->child[pos + 1];
            if (left->is_leaf != right->is_leaf)
                continue;
            if (left->size >= left->key_num / 2 && right->size >= right->key_num / 2)
                continue;
            const bool is_leafs = left->is_leaf;
            if (left->size < left->key_num / 2 && right->size > right->key_num / 2) {
                if (is_leafs) {
                    left->data[left->size] = right->data[0];
                    ++left->size;
                    --right->size;
                    for (size_t i = 0; i < right->size; ++i) {
                        right->data[i] = right->data[i + 1];
                    }
                    node->key[pos] = right->data[0].first;
                }
                else {
                    left->key[left->size] = node->key[pos];
                    ++left->size;
                    left->child[left->size] = right->child[0];
                    left->child[left->size]->parent = left;
                    node->key[pos] = right->key[0];
                    --right->size;
                    for (size_t i = 0; i < right->size; ++i) {
                        right->key[i] = right->key[i + 1];
                        right->child[i] = right->child[i + 1];
                    }
                    right->child[right->size] = right->child[right->size + 1];
                }
                break;
            }
            else if (left->size > left->key_num / 2 && right->size < right->key_num / 2) {
                if (is_leafs) {
                    for (size_t i = 0; i < right->size; ++i) {
                        right->data[i + 1] = right->data[i];
                    }
                    ++right->size;
                    --left->size;
                    right->data[0] = left->data[left->size];
                    node->key[pos] = right->data[0].first;
                }
                else {
                    right->child[right->size + 1] = right->child[right->size];
                    for (size_t i = 0; i < right->size; ++i) {
                        right->key[i + 1] = right->key[i];
                        right->child[i + 1] = right->child[i];
                    }
                    ++right->size;
                    right->key[0] = node->key[pos];
                    right->child[0] = left->child[left->size];
                    right->child[0]->parent = right;
                    --left->size;
                    node->key[pos] = left->key[left->size];
                }
                break;
            }
            else if (is_leafs && left->size + right->size < left->key_num) {
                for (size_t i = 0; i < right->size; ++i) {
                    left->data[i + left->size] = right->data[i];
                }
                left->size += right->size;
                right->size = 0;
                if (last == right) {
                    last = left;
                }
                if (right->next != nullptr) {
                    right->next->prev = left;
                }
                left->next = right->next;
                delete right;
            }
            else if (!is_leafs && left->size < left->key_num / 2 && right->size < right->key_num / 2) {
                left->key[left->size] = node->key[pos];
                ++left->size;
                for (size_t i = 0; i < right->size; ++i) {
                    left->key[i + left->size] = right->key[i];
                    left->child[i + left->size] = right->child[i];
                    left->child[i + left->size]->parent = left;
                }
                left->size += right->size;
                left->child[left->size] = right->child[right->size];
                left->child[left->size]->parent = left;
                right->size = 0;
                delete right;
            }
            --node->size;
            for (auto i = pos + 1; i < node->size; ++i) {
                node->key[i] = node->key[i + 1];
                node->child[i] = node->child[i + 1];
            }
            if (node->size > 0)
                node->child[node->size] = node->child[node->size + 1];
            if (node != tree_root)
                node_upd(node->parent);
            else if (node == tree_root && node->size == 0) {
                tree_root = node->child[0];
                delete node;
            }
            break;
        }
    }

    void upd_links()
    {
        Node * node = tree_root;
        Node * prev = nullptr;
        std::stack<size_t> stack;
        size_t index = 0;
        for (node = node->child[0]; node != tree_root || index <= node->size; ++index) {
            if (node->is_leaf) {
                last = node;
                node->prev = prev;
                if (node->prev == nullptr)
                    first = node;
                else
                    node->prev->next = node;
                prev = node;
                node = node->parent;
                index = stack.top();
                stack.pop();
            }
            else if (index > node->size) {
                node = node->parent;
                index = stack.top();
                stack.pop();
            }
            else {
                stack.push(index);
                node = node->key[index];
                index = 0;
            }
        }
    }
};

template <class T>
std::string toString(T obj)
{
    std::ostringstream s;
    s << obj;
    return s.str();
}