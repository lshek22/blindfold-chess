#pragma once
#include "bitboard.h"

namespace HashKeys {
    extern Bitboard piece_keys[12][64];
    extern Bitboard enpassant_keys[64];
    extern Bitboard castle_keys[16];
    extern Bitboard side_key;

    void init_random_keys();
    Bitboard generate_hash_key();
}