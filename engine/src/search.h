#pragma once
#include "bitboard.h"
#include "enums.h"
#include "attack_tables.h"
#include "movegen.h"
#include "evaluation.h"

extern int ply;

void search_position(int depth);

void print_move_scores(MoveList move_list);

static inline void enable_pv_scoring(MoveList& move_list) {
    follow_pv = 0;

    for (int count = 0; count < move_list.get_count(); count++) {
        if (pv_table[0][ply] == move_list.get_move(count)) {
            score_pv = 1;
            follow_pv = 1;
        }
    }
}

static inline int score_move(Move move) {

    if (score_pv) {
        if (pv_table[0][ply] == move) {
            
            score_pv = 0;

            return 20000;
        }
    }


    if (MoveBuilder::get_capture(move)) {

        int target_piece = P;
        int start_piece, end_piece;

        if (side == white) {
            start_piece = p;
            end_piece = k;
        } else {
            start_piece = P;
            end_piece = K;   
        }

        for (int bb_piece = start_piece; bb_piece <= end_piece; bb_piece++) {
            if (get_bit(bitboards[bb_piece], MoveBuilder::get_target(move))) {
                target_piece = bb_piece;
                break;
            }
        }

        
        return mvv_lva[MoveBuilder::get_piece(move)][target_piece] + 10000;
    } else {
        if (killer_moves[0][ply] == move) {
            return 9000;
        }

        else if (killer_moves[1][ply] == move) {
            return 8000;
        }

        else {
            return history_moves[MoveBuilder::get_piece(move)][MoveBuilder::get_target(move)];
        }
    }
    return 0;
}


static inline void sort_moves(MoveList& move_list) {
    int move_scores[move_list.get_count()];

    for (int move = 0; move < move_list.get_count(); move++) {
        move_scores[move] = score_move(move_list.get_move(move));
    }

    for (int current_move = 0; current_move < move_list.get_count(); current_move++) {

        for (int next_move = current_move + 1; next_move < move_list.get_count(); next_move++) {

            if (move_scores[current_move] < move_scores[next_move]) {

                int temp_score = move_scores[current_move];
                move_scores[current_move] = move_scores[next_move];
                move_scores[next_move] = temp_score;
                
                move_list.swap_moves(current_move, next_move);

            }

        }

    }

}



static inline int quiescence(int alpha, int beta) {

    nodes++;

    int evaluation = evaluate();

    
    if (evaluation >= beta) {
        return beta;
    }

    if (evaluation > alpha) {
        alpha = evaluation;
    }



    MoveList move_list;
    move_list.clear();

    generate_moves(move_list);

    sort_moves(move_list);


    for (int count = 0; count < move_list.get_count(); count++) {
        //BoardCopy backup;
        copy_board();

        ply++;

        if (make_move(move_list.get_move(count), only_captures) == 0) {
            ply--;

            continue;
        }

        int score = -quiescence(-beta, -alpha);


        ply--;

        //backup.restore();
        take_back();

        if (score >= beta) {
            return beta;
        }

        if (score > alpha) {
            alpha = score;

        }
    }

    return alpha;

}

static inline int negamax (int alpha, int beta, int depth) {


    pv_length[ply] = ply;


    if (depth == 0) {
        //return evaluate();
        return quiescence(alpha, beta);
    }

    if (ply > max_ply - 1) {
        return evaluate();
    }

    nodes++;

    int in_check = is_square_attacked((side == white) ? get_lsb_index(bitboards[K]) : get_lsb_index(bitboards[k]), side ^ 1);

    if (in_check) depth++;

    int legal_moves = 0;

    if (depth >= 3 && in_check == 0 && ply) {
        copy_board();

        side ^= 1;
        enpassant = no_sq;

        int score = -negamax(-beta, -beta+1, depth - 1 - 2);
        take_back();

        if (score >= beta) {
            return beta;
        }
    }

    MoveList move_list;
    move_list.clear();

    generate_moves(move_list);

    if (follow_pv) {
        enable_pv_scoring(move_list);
    }

    sort_moves(move_list);

    int moves_searched = 0;


    for (int count = 0; count < move_list.get_count(); count++) {

        copy_board();
        //BoardCopy backup;

        ply++;

        if (make_move(move_list.get_move(count), all_moves) == 0) {
            ply--;

            continue;
        }

        legal_moves++;

        int score = 0;

        
            if (moves_searched == 0) {
                score = -negamax(-beta, -alpha, depth - 1);
            } else {
                if(
                    moves_searched >= full_depth_moves &&
                    depth >= reduction_limit &&
                    in_check == 0 && 
                    MoveBuilder::get_capture(move_list.get_move(count)) == 0 &&
                    MoveBuilder::get_promoted(move_list.get_move(count)) == 0
                ) {
                    score = -negamax(-alpha - 1, -alpha, depth - 2);
                }

                else score = alpha + 1;

                if (score > alpha) {
                    score = -negamax(-alpha - 1, -alpha, depth-1);
                    
                    if((score > alpha) && (score < beta)) {
                        score = -negamax(-beta, -alpha, depth-1);
                    }
                }

            }

        


        // int score = -negamax(-beta, -alpha, depth - 1);


        ply--;

        //backup.restore();
        take_back();

        moves_searched++;

        if (score >= beta) {

            if (MoveBuilder::get_capture(move_list.get_move(count)) == 0) {

                killer_moves[1][ply] = killer_moves[0][ply];
                killer_moves[0][ply] = move_list.get_move(count);
            }

            return beta;
        }

        if (score > alpha) {

            if (MoveBuilder::get_capture(move_list.get_move(count)) == 0) {

                history_moves[MoveBuilder::get_piece(move_list.get_move(count))][MoveBuilder::get_target(move_list.get_move(count))] += depth;
            }

            alpha = score;


            pv_table[ply][ply] = move_list.get_move(count);

            for (int next_ply = ply + 1; next_ply < pv_length[ply + 1]; next_ply++) {
                pv_table[ply][next_ply] = pv_table[ply + 1][next_ply];
            }
            pv_length[ply] = pv_length[ply + 1]; 

            
        }
    }

    if (legal_moves == 0) {
        if (in_check) {
            return -49000 + ply;
        }

        else {
            return 0;
        }
    }

    

    return alpha;

}

