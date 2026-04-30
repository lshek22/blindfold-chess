#pragma once
#include <cstdint>

using U64 = uint64_t;

inline bool get_bit(U64 board, int square) {return board & (1ULL << square);}
inline void set_bit(U64 &board, int square) {board |= (1ULL << square);}
inline void pop_bit(U64 &board, int square) {get_bit(board, square) ? board ^= (1ULL << square): 0;}

void print_bitboard(U64 board);

enum {
   a8, b8, c8, d8, e8, f8, g8, h8,
   a7, b7, c7, d7, e7, f7, g7, h7,
   a6, b6, c6, d6, e6, f6, g6, h6,
   a5, b5, c5, d5, e5, f5, g5, h5,
   a4, b4, c4, d4, e4, f4, g4, h4,
   a3, b3, c3, d3, e3, f3, g3, h3,
   a2, b2, c2, d2, e2, f2, g2, h2,
   a1, b1, c1, d1, e1, f1, g1, h1
};
