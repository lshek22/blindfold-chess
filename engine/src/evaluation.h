#include "bitboard.h"
#include "enums.h"
#include "attack_tables.h"
#include "movegen.h"

/*
    ♙ =   100   = ♙
    ♘ =   300   = ♙ * 3
    ♗ =   350   = ♙ * 3 + ♙ * 0.5
    ♖ =   500   = ♙ * 5
    ♕ =   1000  = ♙ * 10
    ♔ =   10000 = ♙ * 100
    
*/

extern int material_score[12];

// pawn positional score
extern const int pawn_score[64];

// knight positional score
extern const int knight_score[64];

// bishop positional score
extern const int bishop_score[64];

// rook positional score
extern const int rook_score[64];

// king positional score
extern const int king_score[64];

// mirror positional score tables for opposite side
extern const int mirror_score[128];


static inline int evaluate() {
    int score = 0;

    Bitboard bitboard;

    int piece, square;

    for (int bb_piece = P; bb_piece <= k; bb_piece++) {
        
        bitboard = bitboards[bb_piece];

        while (bitboard) {
            piece = bb_piece;
            square = get_lsb_index(bitboard);
            
            score += material_score[piece];

            switch (piece)
            {
                case P: score += pawn_score[square]; break;
                case N: score += knight_score[square]; break;
                case B: score += bishop_score[square]; break;
                case R: score += rook_score[square]; break;
                case K: score += king_score[square]; break;

                case p: score -= pawn_score[mirror_score[square]]; break;
                case n: score -= knight_score[mirror_score[square]]; break;
                case b: score -= bishop_score[mirror_score[square]]; break;
                case r: score -= rook_score[mirror_score[square]]; break;
                case k: score -= king_score[mirror_score[square]]; break;
            }

            pop_bit(bitboard, get_lsb_index(bitboard));
        }
    }

    return (side == white) ? score : -score;
}