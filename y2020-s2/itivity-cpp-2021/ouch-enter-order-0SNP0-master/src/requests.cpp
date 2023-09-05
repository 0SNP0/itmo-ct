#include "requests.h"

#include <string>

uint8_t encode_request_type(const RequestType type)
{
    switch (type) {
    case RequestType::EnterOrder:
        return 'O';
    }
    return 0;
}

unsigned char * add_request_header(unsigned char * start, const RequestType type)
{
    return encode(start, encode_request_type(type));
}

void encode_enter_order_opt_fields(unsigned char * bitfield_start, const char time_in_force, const char capacity)
{
    auto * p = bitfield_start + enter_order_bitfield_num();
#define FIELD(name, bitfield_num, bit)                    \
    set_opt_field_bit(bitfield_start, bitfield_num, bit); \
    p = encode_field_##name(p, name);
#include "enter_order_opt_fields.inl"
}

char convert(const Side side)
{
    switch (side) {
    case Side::Buy: return 'B';
    case Side::Sell: return 'S';
    }
    return 0;
}

char convert(const TimeInForce time_in_force)
{
    switch (time_in_force) {
    case TimeInForce::Day: return '0';
    case TimeInForce::IOC: return '3';
    }
    return 0;
}

char convert(const Capacity capacity)
{
    switch (capacity) {
    case Capacity::Agency: return '1';
    case Capacity::Principal: return '2';
    case Capacity::RisklessPrincipal: return '7';
    }
    return 0;
}

unsigned char * choose_price(unsigned char * p, const double price, const OrdType ord_type)
{
    switch (ord_type) {
    case OrdType::Limit:
        p = encode_price(p, price);
        break;
    case OrdType::Market:
        p = encode_binary4(p, 0x7FFFFFFF);
        break;
    }
    return p;
}

std::vector<unsigned char> create_enter_order_request(
        const std::string & cl_ord_id,
        const Side side,
        const double volume,
        const double price,
        const std::string & symbol,
        const OrdType ord_type,
        const TimeInForce time_in_force,
        const Capacity capacity,
        const std::string & firm,
        const std::string & user)
{
    static_assert(calculate_size(RequestType::EnterOrder) == 44, "Wrong Enter Order message size");

    std::vector<unsigned char> msg(calculate_size(RequestType::EnterOrder));
    auto * p = add_request_header(msg.begin().base(), RequestType::EnterOrder);
    p = encode_text(p, cl_ord_id, 14, ' ');
    p = encode_char(p, convert(side));
    p = encode_integer(p, volume);
    p = encode_binary4(p, std::stoul(symbol));
    p = choose_price(p, price, ord_type);
    p = encode_text(p, firm, 4, ' ');
    p = encode_text(p, user, 6, ' ');
    encode_enter_order_opt_fields(p, convert(time_in_force), convert(capacity));
    return msg;
}
