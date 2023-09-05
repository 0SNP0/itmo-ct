#pragma once

#include <cstdint>
#include <string>

inline unsigned char * encode(unsigned char * start, const uint8_t value)
{
    *start = value;
    return start + 1;
}

inline unsigned char * encode(unsigned char * start, const uint16_t value)
{
    *start++ = static_cast<unsigned char>((value >> 8) & 0xFF);
    *start++ = static_cast<unsigned char>(value & 0xFF);
    return start;
}

inline unsigned char * encode(unsigned char * start, const uint32_t value)
{
    *start++ = static_cast<unsigned char>((value >> 24) & 0xFF);
    *start++ = static_cast<unsigned char>((value >> 16) & 0xFF);
    *start++ = static_cast<unsigned char>((value >> 8) & 0xFF);
    *start++ = static_cast<unsigned char>(value & 0xFF);
    return start;
}

inline unsigned char * encode(unsigned char * start, const std::string & str, const size_t field_size, const char remainder = '\0')
{
    size_t i = 0;
    while (i < str.size() && i < field_size) {
        *start++ = str[i];
        ++i;
    }
    while (i < field_size) {
        *start++ = remainder;
        ++i;
    }
    return start;
}