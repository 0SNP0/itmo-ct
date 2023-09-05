#include <array>
#include <cstring>
#include <fstream>
#include <iostream>
#include <iterator>
#include <memory>
#include <string>
#include <vector>

const std::string std_input = "-";
static constexpr uint8_t files_count = 2;

template <class C>
void print_out(std::ostream & strm, const C & c)
{
    std::ostream_iterator<typename C::value_type> out(strm, "\n");
    std::copy(c.begin(), c.end(), out);
}

void compare_streams(std::pair<std::istream &, std::istream &> file, const bool & only_file_1, const bool & only_file_2, const bool & both_files)
{
    std::pair<std::vector<std::string>, std::vector<std::string>> lines;
    std::string line;
    while (std::getline(file.first, line)) {
        lines.first.push_back(line);
    }
    while (std::getline(file.second, line)) {
        lines.second.push_back(line);
    }
    std::vector<std::string> result;
    auto current = std::pair(lines.first.begin(), lines.second.begin());
    while (current.first < lines.first.end() && current.second < lines.second.end()) {
        auto rem = std::pair(lines.first.end() - current.first, lines.second.end() - current.second);
        auto next = std::pair(current.first, current.second);
        [&] {
            for (auto len = 0; len < rem.first + rem.second - 1; ++len) {
                for (auto i = len / 2; i >= 0; --i) {
                    auto first = current.first + i, second = current.second + (len - i);
                    if (first < lines.first.end() && second < lines.second.end() && *first == *second) {
                        next.first = first;
                        next.second = second;
                        return;
                    }
                    first = current.first + (len - i), second = current.second + i;
                    if (first < lines.first.end() && second < lines.second.end() && *first == *second) {
                        next.first = first;
                        next.second = second;
                        return;
                    }
                }
            }
        }();
        if (*next.first != *next.second) {
            break;
        }
        if (only_file_1) {
            while (current.first != next.first) {
                result.push_back(*current.first++);
            }
        }
        if (only_file_2) {
            while (current.second != next.second) {
                result.push_back((only_file_1 ? "\t" : "") + *current.second++);
            }
        }
        if (both_files) {
            result.push_back((only_file_1 && only_file_2 ? "\t\t" : (only_file_1 ^ only_file_2 ? "\t" : "")) +
                             *next.first);
        }
        current = std::pair(next.first + 1, next.second + 1);
    }
    if (only_file_1) {
        while (current.first != lines.first.end()) {
            result.push_back(*current.first++);
        }
    }
    if (only_file_2) {
        while (current.second != lines.second.end()) {
            result.push_back((only_file_1 ? "\t" : "") + *current.second++);
        }
    }
    print_out(std::cout, result);
}

int main(int argc, char ** argv)
{
    std::array<bool, files_count + 1> only_file;
    only_file.fill(true);
    bool & both_files = only_file[files_count];
    std::array<std::string, files_count> file_name;
    file_name.fill(std_input);
    uint8_t cnt = 0;
    for (auto i = 1; i < argc; ++i) {
        if (argv[i][0] == '-' && std::strlen(argv[i]) > 1)
            for (size_t j = 1; j < std::strlen(argv[i]); ++j) {
                only_file.at(argv[i][j] - '0' - 1) = false;
            }
        else if (cnt < files_count)
            file_name[cnt++] = argv[i];
    }
    std::array<std::shared_ptr<std::istream>, files_count> input;
    for (auto i = 0; i < files_count; ++i) {
        input[i] = std::shared_ptr<std::istream>(file_name[i] == std_input ? &std::cin : new std::ifstream(file_name[i]));
    }
    compare_streams({*input[0].get(), *input[1].get()}, only_file[0], only_file[1], both_files);
    return 0;
}
