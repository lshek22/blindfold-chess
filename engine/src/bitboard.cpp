#include "bitboard.h"
#include "enums.h"
#include <stdio.h>

void print_bitboard(Bitboard board) {
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            int square = row * 8 + col;
            printf(" %d ", get_bit(board, square) ? 1 : 0);
        }
        printf("\n");
    }
    printf("\n");
}

int count_bits(Bitboard bitboard) {
    return __builtin_popcountll(bitboard);
}