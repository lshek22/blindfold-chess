#include "bitboard.h"
#include <stdio.h>

void print_bitboard(U64 board) {
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            int square = row * 8 + col;
            printf(" %d ", get_bit(board, square) ? 1 : 0);
        }
        printf("\n");
    }
}
