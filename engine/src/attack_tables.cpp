#include "attack_tables.h"
#include "enums.h"
#include <stdio.h>

Bitboard pawn_attacks[2][64];

Bitboard generate_pawn_attacks(int side, int square){

    Bitboard bitboard = 0ULL;
    Bitboard attack_table = 0ULL;

    set_bit(bitboard, square);
    
    // white is 0 so !side == 1
    if (!side) {
        if ((bitboard >> 7) & not_a_file) attack_table |= (bitboard >> 7);
        if ((bitboard >> 9) & not_h_file) attack_table |= (bitboard >> 9);
    } else {
        if ((bitboard << 7) & not_h_file) attack_table |= (bitboard << 7);
        if ((bitboard << 9) & not_a_file) attack_table |= (bitboard << 9);
    }

    //print_bitboard(bitboard);

    return attack_table;
}

void pawn_precalc_table(){
    for(int square= 0; square<64 ; square++){
        pawn_attacks[white][square] = generate_pawn_attacks(white, square);
        pawn_attacks[black][square] = generate_pawn_attacks(black, square);
    }

}