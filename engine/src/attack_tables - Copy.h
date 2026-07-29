#pragma once

#include "bitboard.h"


const Bitboard not_a_file = 18374403900871474942ULL;

const Bitboard not_h_file = 9187201950435737471ULL;

const Bitboard not_hg_file = 4557430888798830399ULL;

const Bitboard not_ab_file = 18229723555195321596ULL;

const int bishop_relevant_bits[64] = {
    6, 5, 5, 5, 5, 5, 5, 6, 
    5, 5, 5, 5, 5, 5, 5, 5, 
    5, 5, 7, 7, 7, 7, 5, 5, 
    5, 5, 7, 9, 9, 7, 5, 5, 
    5, 5, 7, 9, 9, 7, 5, 5, 
    5, 5, 7, 7, 7, 7, 5, 5, 
    5, 5, 5, 5, 5, 5, 5, 5, 
    6, 5, 5, 5, 5, 5, 5, 6
};

const int rook_relevant_bits[64] = {
    12, 11, 11, 11, 11, 11, 11, 12, 
    11, 10, 10, 10, 10, 10, 10, 11, 
    11, 10, 10, 10, 10, 10, 10, 11, 
    11, 10, 10, 10, 10, 10, 10, 11, 
    11, 10, 10, 10, 10, 10, 10, 11, 
    11, 10, 10, 10, 10, 10, 10, 11, 
    11, 10, 10, 10, 10, 10, 10, 11, 
    12, 11, 11, 11, 11, 11, 11, 12
};


extern Bitboard rook_magic_numbers[64];

extern Bitboard bishop_magic_numbers[64];




extern Bitboard pawn_attacks[2][64];

extern Bitboard knight_attacks[64];

extern Bitboard king_attacks[64];

extern Bitboard bishop_masks[64];

extern Bitboard rook_masks[64];

extern Bitboard bishop_attacks[64][512];

extern Bitboard rook_attacks[64][4096];




Bitboard generate_pawn_attacks(int side, int square);

Bitboard generate_knight_attacks(int side, int square);

Bitboard generate_king_attacks(int side, int square);

Bitboard generate_bishop_attacks(int square);

Bitboard generate_rook_attacks(int square);

Bitboard bishop_attacks_innit(int square, Bitboard blockers);

Bitboard rook_attacks_innit(int square, Bitboard blockers);


void leaper_precalc_table();

Bitboard set_occupancy(int index, int number_of_attacks, Bitboard attack_table);


/*

    using randomness for generating magic numbers for bishop and rook

*/

extern unsigned int state;

unsigned int get_random_U32_number();

Bitboard get_random_U64_number();

Bitboard generate_magic_number();

Bitboard find_magic_number(int square, int relevant_bits, int bishop);

void init_magic_numbers();

void init_sliders_attacks(int bishop);

static inline Bitboard get_bishop_attacks(int square, Bitboard occupancy) {
    occupancy &= bishop_masks[square];
    occupancy *= bishop_magic_numbers[square];
    occupancy >>= 64 - bishop_relevant_bits[square];

    return bishop_attacks[square][occupancy];
}

static inline Bitboard get_rook_attacks(int square, Bitboard occupancy) {
    occupancy &= rook_masks[square];
    occupancy *= rook_magic_numbers[square];
    occupancy >>= 64 - rook_relevant_bits[square];
    
    return rook_attacks[square][occupancy];
}

static inline Bitboard get_queen_attacks(int square, Bitboard occupancy) {
    Bitboard queen_attacks = 0ULL;
    
    Bitboard bishop_occupancy = occupancy;
    
    Bitboard rook_occupancy = occupancy;
    

    bishop_occupancy &= bishop_masks[square];
    bishop_occupancy *= bishop_magic_numbers[square];
    bishop_occupancy >>= 64 - bishop_relevant_bits[square];
    

    queen_attacks = bishop_attacks[square][bishop_occupancy];
    
 
    rook_occupancy &= rook_masks[square];
    rook_occupancy *= rook_magic_numbers[square];
    rook_occupancy >>= 64 - rook_relevant_bits[square];
    

    queen_attacks |= rook_attacks[square][rook_occupancy];
  
    return queen_attacks;
}