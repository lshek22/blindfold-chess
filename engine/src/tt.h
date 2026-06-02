#pragma once
#include "bitboard.h"
#include "movegen.h"

#define hash_size 0x400000
#define no_hash_entry 100000


#define hash_flag_exact 0
#define hash_flag_alpha 1
#define hash_flag_beta 2


struct TT_Entry {
    Bitboard hash_key;
    int depth;
    int flag;
    int score;
    // Move best_move;
};

extern TT_Entry hash_table[hash_size]; 

void clear_hash_table();

static inline int read_hash_entry(int alpha, int beta, int depth) {

    TT_Entry *hash_entry = &hash_table[hash_key % hash_size];

    if (hash_entry->hash_key == hash_key) {
        if (hash_entry->depth >= depth) {
            if (hash_entry->flag == hash_flag_exact) {
                return hash_entry->score;
            }

            if (hash_entry->flag == hash_flag_alpha && hash_entry->score <= alpha) {
                return alpha;
            }

            if (hash_entry->flag == hash_flag_beta && hash_entry->score >= beta) {
                return beta;
            }
        }
    }

    return no_hash_entry;
}

static inline void record_hash(int score, int hashf, int depth) {
    TT_Entry *hash_entry = &hash_table[hash_key % hash_size];

    hash_entry->hash_key = hash_key;
    hash_entry->score = score;
    hash_entry->flag = hashf;
    hash_entry->depth = depth;
}