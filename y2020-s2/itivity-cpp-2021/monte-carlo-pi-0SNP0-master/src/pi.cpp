#include "pi.h"

#include "random_gen.h"

double calculate_pi(unsigned long runs) {
    if (runs == 0) {
        return 0;
    }
    unsigned long inside = 0;
    for (unsigned long i = 0; i < runs; ++i) {
        double x = get_random_number(), y = get_random_number();
        if (x * x + y * y <= 1) {
            ++inside;
        }
    }
    return 4.0 * inside / runs;
}
