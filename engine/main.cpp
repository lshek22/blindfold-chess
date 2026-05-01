#include "src/bitboard.h"
#include "src/enums.h"
#include "src/attack_tables.h"
#include <stdio.h>

int main(){
    Bitboard bitboard = 0ULL;
    pawn_precalc_table();
    //print_bitboard( generate_pawn_attacks(black, e4));
    for(int square= 0; square<64 ; square++)
        print_bitboard(pawn_attacks[black][square]);
}