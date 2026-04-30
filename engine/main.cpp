#include "src/bitboard.h"
#include <stdio.h>

int main(){
    U64 bitboard = 16ULL;
    set_bit(bitboard, e1);
    print_bitboard(bitboard);
}