#include "engine.h"
#include "bitboard.h"
#include "search.h"
#include "uci.h"
#include "movegen.h"


Move best_move;

void init_engine() {
    innit_all();
}

void set_position(const std::string& fen) {
    parse_fen(const_cast<char*>(fen.c_str()));
}

std::string get_board() {
    return get_board_string();
}

std::string get_best_move(int depth) {

    search_position(depth);

    // assuming search_position stores best move somewhere
    return get_move_as_string(pv_table[0][0]);
}

bool make_move_string(const std::string& moveStr) {
    int move = parse_move(moveStr);

    if (move == 0)
        return false;

    return make_move(move, all_moves);
}

bool is_checkmate() {

    MoveList moves;
    generate_moves(moves);

    // Any legal move?
    for (int i = 0; i < moves.get_count(); i++) {

        copy_board();

        if (make_move(moves.get_move(i), all_moves)) {

            take_back();
            return false;
        }

        take_back();
    }

    int king_square =
            side == white ?
            get_lsb_index(bitboards[K]) :
            get_lsb_index(bitboards[k]);

    return is_square_attacked(
            king_square,
            side ^ 1
    );
}

bool insufficient_material() {

    int pieces = 0;

    for (int p = P; p <= k; p++) {

        if (p == K || p == k)
            continue;

        pieces += count_bits(bitboards[p]);
    }

    if (pieces == 0)
        return true;

    if (pieces == 1 &&
        (
                count_bits(bitboards[B]) +
                count_bits(bitboards[b]) +
                count_bits(bitboards[N]) +
                count_bits(bitboards[n])
        ) == 1)
        return true;

    return false;
}

bool is_draw() {

    if (insufficient_material()) {
        return true;
    }


    MoveList moves;
    generate_moves(moves);

    bool has_legal_move = false;

    for (int i = 0; i < moves.get_count(); i++) {

        copy_board();

        if (make_move(moves.get_move(i), all_moves)) {
            has_legal_move = true;
            take_back();
            break;
        }

        take_back();
    }

    if (has_legal_move) {
        return false;
    }

    int king_square =
            side == white
            ? get_lsb_index(bitboards[K])
            : get_lsb_index(bitboards[k]);

    bool in_check = is_square_attacked(
            king_square,
            side ^ 1
    );

    return !in_check;
}