#pragma once
#include <cstdint>

using Bitboard = uint64_t;

inline bool get_bit(Bitboard board, int square) {return board & (1ULL << square);}
inline void set_bit(Bitboard &board, int square) {board |= (1ULL << square);}
inline void pop_bit(Bitboard &board, int square) {get_bit(board, square) ? board ^= (1ULL << square): 0;}

void print_bitboard(Bitboard board);

