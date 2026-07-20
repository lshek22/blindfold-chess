#pragma once

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

// king positional score for middlegame
extern const int king_mg_score[64];

// king positional score for endgame
extern const int king_eg_score[64];

// mirror positional score tables for opposite side
extern const int mirror_score[128];

extern int mvv_lva[12][12];

extern int killer_moves[2][max_ply];

extern int history_moves[12][64];

extern int pv_length[max_ply];

extern int pv_table[max_ply][max_ply];

extern int follow_pv, score_pv;

extern const int full_depth_moves;
extern const int reduction_limit;

extern Bitboard file_masks[64];

extern Bitboard rank_masks[64];

extern Bitboard isolated_masks[64];

extern Bitboard white_passed_masks[64];

extern Bitboard black_passed_masks[64];

extern const int get_rank[64];

extern const int double_pawn_penalty;

extern const int isolated_pawn_penalty;

extern const int passed_pawn_bonus[8]; 

extern const int semi_open_file_score;

extern const int open_file_score;

extern const int king_shield_bonus;


int evaluate();

void init_evaluation_masks();

Bitboard set_file_rank_mask(int file_number, int rank_number);

static inline bool is_endgame() {

    int white_material = 0;
    int black_material = 0;

    for (int piece = N; piece <= Q; piece++) {
        white_material += count_bits(bitboards[piece]);
    }

    for (int piece = n; piece <= q; piece++) {
        black_material += count_bits(bitboards[piece]);
    }

    return white_material <= 2 &&
           black_material <= 2;
}