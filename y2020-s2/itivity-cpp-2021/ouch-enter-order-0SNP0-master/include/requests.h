#pragma once

#include "fields.h"

#include <algorithm>
#include <array>
#include <cstddef>
#include <string>
#include <vector>

/*
 * Enter Order
 *  time_in_force(1, 32)
 *  capacity(1, 1)
 */

constexpr size_t enter_order_bitfield_num()
{
    return 4;
    //    return std::max({0
    //#define FIELD(_, n, __) , n
    //#include "enter_order_opt_fields.inl"
    //                    });
}

constexpr size_t enter_order_opt_fields_size()
{
    return 0
#define FIELD(name, _, __) +name##_field_size
#include "enter_order_opt_fields.inl"
            ;
}

enum class RequestType
{
    EnterOrder
};

constexpr size_t calculate_size(const RequestType type)
{
    switch (type) {
    case RequestType::EnterOrder:
        return 38 + enter_order_bitfield_num() + enter_order_opt_fields_size();
    }
}

enum class Side
{
    Buy,
    Sell
};

enum class OrdType
{
    Market,
    Limit
};

enum class TimeInForce
{
    Day,
    IOC
};

enum class Capacity
{
    Agency,
    Principal,
    RisklessPrincipal
};

std::vector<unsigned char> create_enter_order_request(
        const std::string & cl_ord_id,
        Side side,
        double volume,
        double price,
        const std::string & symbol,
        OrdType ord_type,
        TimeInForce time_in_force,
        Capacity capacity,
        const std::string & firm,
        const std::string & user);