#pragma once

#include <cstdio>
#include "bitboard.h"
#include "enums.h"
#include "attack_tables.h"

static inline bool is_square_attacked(int square, int side) {

    if ((side == white) && (pawn_attacks[black][square] & bitboards[P])) return true;

    if ((side == black) && (pawn_attacks[white][square] & bitboards[p])) return true;

    if (knight_attacks[square] & ((side == white) ? bitboards[N] : bitboards[n])) return true;
    
    if (get_bishop_attacks(square, occupancies[both]) & ((side == white) ? bitboards[B] : bitboards[b])) return true;

    if (get_rook_attacks(square, occupancies[both]) & ((side == white) ? bitboards[R] : bitboards[r])) return true;    

    if (get_queen_attacks(square, occupancies[both]) & ((side == white) ? bitboards[Q] : bitboards[q])) return true;
    
    if (king_attacks[square] & ((side == white) ? bitboards[K] : bitboards[k])) return true;


    return false;
}

void print_attacked_squares(int side);
