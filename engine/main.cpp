#include "src/bitboard.h"
#include "src/enums.h"
#include "src/attack_tables.h"
#include <stdio.h>

int main(){
    Bitboard bitboard = 0ULL;
    leaper_precalc_table();
    //print_bitboard( generate_pawn_attacks(black, e4));
    //for(int square= 0; square<64 ; square++)
    //    print_bitboard(generate_rook_attacks(square));
   
    Bitboard blockers = 0ULL;
    set_bit(blockers, g4);
    set_bit(blockers, e3);
    set_bit(blockers, c4);
    set_bit(blockers, g1);
    set_bit(blockers, e1);
    set_bit(blockers, c1);
    print_bitboard(blockers);
    printf("bit count: %d\n", count_bits(blockers));
}