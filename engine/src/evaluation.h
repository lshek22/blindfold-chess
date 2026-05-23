#include "bitboard.h"
#include "enums.h"
#include "attack_tables.h"
#include "movegen.h"

#define max_ply 64

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

static int mvv_lva[12][12] = {
 	105, 205, 305, 405, 505, 605,  105, 205, 305, 405, 505, 605,
	104, 204, 304, 404, 504, 604,  104, 204, 304, 404, 504, 604,
	103, 203, 303, 403, 503, 603,  103, 203, 303, 403, 503, 603,
	102, 202, 302, 402, 502, 602,  102, 202, 302, 402, 502, 602,
	101, 201, 301, 401, 501, 601,  101, 201, 301, 401, 501, 601,
	100, 200, 300, 400, 500, 600,  100, 200, 300, 400, 500, 600,

	105, 205, 305, 405, 505, 605,  105, 205, 305, 405, 505, 605,
	104, 204, 304, 404, 504, 604,  104, 204, 304, 404, 504, 604,
	103, 203, 303, 403, 503, 603,  103, 203, 303, 403, 503, 603,
	102, 202, 302, 402, 502, 602,  102, 202, 302, 402, 502, 602,
	101, 201, 301, 401, 501, 601,  101, 201, 301, 401, 501, 601,
	100, 200, 300, 400, 500, 600,  100, 200, 300, 400, 500, 600
};

extern int killer_moves[2][max_ply];

extern int history_moves[12][64];

extern int pv_length[max_ply];

extern int pv_table[max_ply][max_ply];

extern int follow_pv, score_pv;

const int full_depth_moves = 4;
const int reduction_limit = 3;


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

            square = get_lsb_index(bitboard);

            pop_bit(bitboard, square);
        }
    }

    return (side == white) ? score : -score;
}