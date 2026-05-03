#pragma once

#include "bitboard.h"


const Bitboard not_a_file = 18374403900871474942ULL;

const Bitboard not_h_file = 9187201950435737471ULL;

const Bitboard not_hg_file = 4557430888798830399ULL;

const Bitboard not_ab_file = 18229723555195321596ULL;



extern Bitboard pawn_attacks[2][64];

extern Bitboard knight_attacks[64];

extern Bitboard king_attacks[64];




Bitboard generate_pawn_attacks(int side, int square);

Bitboard generate_knight_attacks(int side, int square);

Bitboard generate_king_attacks(int side, int square);

Bitboard generate_bishop_attacks(int square);

Bitboard generate_rook_attacks(int square);

Bitboard bishop_attacks_innit(int square, Bitboard blockers);

Bitboard rook_attacks_innit(int square, Bitboard blockers);


void leaper_precalc_table();
