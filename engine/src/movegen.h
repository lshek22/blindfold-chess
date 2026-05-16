#pragma once

#include <cstdio>
#include "bitboard.h"
#include "enums.h"
#include "attack_tables.h"
#include <cstring>

char get_promoted_char(int piece);

extern const int castling_rights[64];


/*
          binary move bits                               hexidecimal constants
    
    0000 0000 0000 0000 0011 1111    source square       0x3f
    0000 0000 0000 1111 1100 0000    target square       0xfc0
    0000 0000 1111 0000 0000 0000    piece               0xf000
    0000 1111 0000 0000 0000 0000    promoted piece      0xf0000
    0001 0000 0000 0000 0000 0000    capture flag        0x100000
    0010 0000 0000 0000 0000 0000    double push flag    0x200000
    0100 0000 0000 0000 0000 0000    enpassant flag      0x400000
    1000 0000 0000 0000 0000 0000    castling flag       0x800000
*/


using Move = uint32_t;

namespace MoveBuilder {

    inline constexpr Move encode(int src, int tgt, int pc, int prom, int cap, int dbl, int ep, int cas) {
        return (src) | (tgt << 6) | (pc << 12) | (prom << 16) | 
               (cap << 20) | (dbl << 21) | (ep << 22) | (cas << 23);
    }

    inline constexpr int get_source(Move move) { return move & 0x3f; }
    inline constexpr int get_target(Move move) { return (move >> 6) & 0x3f; }
    inline constexpr int get_piece(Move move) { return (move >> 12) & 0xf; }
    inline constexpr int get_promoted(Move move) { return (move >> 16) & 0xf; }
    inline constexpr int get_capture(Move move) { return (move) & 0x100000; }
    inline constexpr int get_double(Move move) { return (move) & 0x200000; }
    inline constexpr int get_enpassant(Move move) { return (move) & 0x400000; }
    inline constexpr int get_castling(Move move) { return (move) & 0x800000; }
    
}



static inline bool is_square_attacked(int square, int side) {

    if ((side == white) && (pawn_attacks[black][square] & bitboards[P])) return true;

    if ((side == black) && (pawn_attacks[white][square] & bitboards[p])) return true;

    if (knight_attacks[square] & ((side == white) ? bitboards[N] : bitboards[n])) return true;
    
    if (get_bishop_attacks(square, occupancies[both]) & ((side == white) ? bitboards[B] : bitboards[b])) return true;

    if (get_rook_attacks(square, occupancies[both]) & ((side == white) ? bitboards[R] : bitboards[r])) return true;    

    if (get_queen_attacks(square, occupancies[both]) & ((side == white) ? bitboards[Q] : bitboards[q])) return true;
    
    if (king_attacks[square] & ((side == white) ? bitboards[K] : bitboards[k])) return true;


    return false;
}

void print_attacked_squares(int side);


class MoveList {
    private:
        Move moves[256];
        int count = 0;

    public:
        
        void clear() { count = 0; }

        
        inline void add_move(Move move) {
            
            if (count < 256) {
                moves[count++] = move;
            }

        }

        
        int get_count() const { return count; }
        Move get_move(int index) const { return moves[index]; }
};

void print_move(Move move);

void print_move_list(const MoveList& move_list);




static inline void generate_moves(MoveList& move_list) {
    int source_square, target_square;

    Bitboard bitboard, attacks;

    for (int piece = P; piece <= k; piece++) {
        bitboard = bitboards[piece];
        
        if (side == white) {
            if (piece == P) {
                while (bitboard) {
                    source_square = get_lsb_index(bitboard);
                    
                    target_square = source_square - 8;
                    
                    if (!(target_square < a8) && !get_bit(occupancies[both], target_square)) {
                        if (source_square >= a7 && source_square <= h7) {
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, Q, 0, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, R, 0, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, B, 0, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, N, 0, 0, 0, 0));
                        }
                        
                        else {
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 0, 0, 0, 0));


                            if ((source_square >= a2 && source_square <= h2) && !get_bit(occupancies[both], target_square - 8)) {
                                move_list.add_move(MoveBuilder::encode(source_square, target_square - 8, piece, 0, 0, 1, 0, 0));
                            }
                        }
                    }
                    attacks = pawn_attacks[white][source_square] & occupancies[black];

                    while (attacks) {
                        target_square = get_lsb_index(attacks);

                        if (source_square >= a7 && source_square <= h7) {
                             move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, Q, 1, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, R, 1, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, B, 1, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, N, 1, 0, 0, 0));
                        }
                        
                        else{
                           move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 1, 0, 0, 0));
                        }

                        pop_bit(attacks, target_square);
                    }

                    if (enpassant != no_sq) {
                        Bitboard enpassant_attacks = pawn_attacks[side][source_square] & (1ULL << enpassant);
                        
                        if (enpassant_attacks) {
                            int target_enpassant = get_lsb_index(enpassant_attacks);
                            move_list.add_move(MoveBuilder::encode(source_square, target_enpassant, piece, 0, 1, 0, 1, 0));

                        }
                    }
                    
                    pop_bit(bitboard, source_square);
                }
            }

            if (piece == K) {

                if (castle & wk) {
                    if (!get_bit(occupancies[both], f1) && !get_bit(occupancies[both], g1)) {
                        if (!is_square_attacked(e1, black) && !is_square_attacked(f1, black)) {
                            move_list.add_move(MoveBuilder::encode(e1, g1, piece, 0, 0, 0, 0, 1));
                        }
                    }

                }

                if (castle & wq) {
                    if (!get_bit(occupancies[both], d1) && !get_bit(occupancies[both], c1) && !get_bit(occupancies[both], b1)) {
                        if (!is_square_attacked(e1, black) && !is_square_attacked(d1, black)) {
                            move_list.add_move(MoveBuilder::encode(e1, c1, piece, 0, 0, 0, 0, 1));
                        }                        
                    }
                }

            }

        } else {
            if (piece == p) {
                while (bitboard) {
                    source_square = get_lsb_index(bitboard);
                    
                    target_square = source_square + 8;
                    
                    if (!(target_square > h1) && !get_bit(occupancies[both], target_square)) {
                        if (source_square >= a2 && source_square <= h2) {
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, q, 0, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, r, 0, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, b, 0, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, n, 0, 0, 0, 0));
                        }
                        
                        else {
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 0, 0, 0, 0));


                            if ((source_square >= a7 && source_square <= h7) && !get_bit(occupancies[both], target_square + 8)) {
                                move_list.add_move(MoveBuilder::encode(source_square, target_square + 8, piece, 0, 0, 1, 0, 0));

                            }
                        }
                    }

                    attacks = pawn_attacks[black][source_square] & occupancies[white];

                    while (attacks) {
                        target_square = get_lsb_index(attacks);
                        
                        if (source_square >= a2 && source_square <= h2) {
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, q, 1, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, r, 1, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, b, 1, 0, 0, 0));
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, n, 1, 0, 0, 0));
                        
                        }
                        
                        else {
                            move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 1, 0, 0, 0));

                        }
                        pop_bit(attacks, target_square);
                    }
                    
                    if (enpassant != no_sq) {
                        Bitboard enpassant_attacks = pawn_attacks[side][source_square] & (1ULL << enpassant);
                        
                        if (enpassant_attacks) {
                            int target_enpassant = get_lsb_index(enpassant_attacks);
                            move_list.add_move(MoveBuilder::encode(source_square, target_enpassant, piece, 0, 1, 0, 1, 0));

                        }
                    }

                    
                    pop_bit(bitboard, source_square);
                }
            }

            if (piece == k) {

                if (castle & bk) {
                    if (!get_bit(occupancies[both], f8) && !get_bit(occupancies[both], g8)) {
                        if (!is_square_attacked(e8, white) && !is_square_attacked(f8, white)) {
                            move_list.add_move(MoveBuilder::encode(e8, g8, piece, 0, 0, 0, 0, 1));

                        }
                    }

                }

                if (castle & bq) {
                    if (!get_bit(occupancies[both], d8) && !get_bit(occupancies[both], c8) && !get_bit(occupancies[both], b8)) {
                        if (!is_square_attacked(e8, white) && !is_square_attacked(d8, white)) {
                            move_list.add_move(MoveBuilder::encode(e8, c8, piece, 0, 0, 0, 0, 1));

                        }                        
                    }
                }

            }

        }
        
        
        if ((side == white) ? piece == N : piece == n) {
            while (bitboard) {
                source_square = get_lsb_index(bitboard);

                attacks = knight_attacks[source_square] & ((side == white) ? ~occupancies[white] : ~occupancies[black]);

                while (attacks) {
                    target_square = get_lsb_index(attacks);

                    if (!get_bit( (side == white) ? occupancies[black] : occupancies[white], target_square )) {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 0, 0, 0, 0));

                    } else {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 1, 0, 0, 0));

                    }
                    

                    pop_bit(attacks, target_square);
                }

                pop_bit(bitboard, source_square);
            }
        }

        if ((side == white) ? piece == B : piece == b) {
            while (bitboard) {
                source_square = get_lsb_index(bitboard);

                attacks = get_bishop_attacks(source_square, occupancies[both]) & ((side == white) ? ~occupancies[white] : ~occupancies[black]);

                while (attacks) {
                    target_square = get_lsb_index(attacks);

                    if (!get_bit(((side == white) ? occupancies[black] : occupancies[white]), target_square)) {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 0, 0, 0, 0));

                    } else {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 1, 0, 0, 0));

                    }
                    

                    pop_bit(attacks, target_square);
                }

                pop_bit(bitboard, source_square);
            }
        }


        if ((side == white) ? piece == R : piece == r) {
            while (bitboard) {
                source_square = get_lsb_index(bitboard);

                attacks = get_rook_attacks(source_square, occupancies[both]) & ((side == white) ? ~occupancies[white] : ~occupancies[black]);

                while (attacks) {
                    target_square = get_lsb_index(attacks);

                    if (!get_bit(((side == white) ? occupancies[black] : occupancies[white]), target_square)) {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 0, 0, 0, 0));

                    } else {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 1, 0, 0, 0));
                    }
                    

                    pop_bit(attacks, target_square);
                }

                pop_bit(bitboard, source_square);
            }
        }


        if ((side == white) ? piece == Q : piece == q) {
            while (bitboard) {
                source_square = get_lsb_index(bitboard);

                attacks = get_queen_attacks(source_square, occupancies[both]) & ((side == white) ? ~occupancies[white] : ~occupancies[black]);

                while (attacks) {
                    target_square = get_lsb_index(attacks);

                    if (!get_bit(((side == white) ? occupancies[black] : occupancies[white]), target_square)) {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 0, 0, 0, 0));
                    } else {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 1, 0, 0, 0));
                    }
                    

                    pop_bit(attacks, target_square);
                }

                pop_bit(bitboard, source_square);
            }
        }


        if ((side == white) ? piece == K : piece == k) {
            while (bitboard) {
                source_square = get_lsb_index(bitboard);

                attacks = king_attacks[source_square] & ((side == white) ? ~occupancies[white] : ~occupancies[black]);

                while (attacks) {
                    target_square = get_lsb_index(attacks);

                    if (!get_bit(((side == white) ? occupancies[black] : occupancies[white]), target_square)) {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 0, 0, 0, 0));
                    } else {
                        move_list.add_move(MoveBuilder::encode(source_square, target_square, piece, 0, 1, 0, 0, 0));
                    }
                    

                    pop_bit(attacks, target_square);
                }

                pop_bit(bitboard, source_square);
            }
        }
    }

}

struct BoardCopy {
    Bitboard bb[12];
    Bitboard occ[3];
    int side;
    int ep;
    int cas;


    BoardCopy() {
        for (int i = 0; i < 12; i++) bb[i] = bitboards[i];
        for (int i = 0; i < 3; i++) occ[i] = occupancies[i];
        
        side = ::side; 
        ep = enpassant;
        cas = castle;
    }

    void restore() const {
        for (int i = 0; i < 12; i++) bitboards[i] = bb[i];
        for (int i = 0; i < 3; i++) occupancies[i] = occ[i];
        
        ::side = side;
        enpassant = ep;
        castle = cas;
    }
};

static inline int make_move(Move move, int move_flag) {
    if (move_flag == all_moves) {
        BoardCopy backup;

        int source_square = MoveBuilder::get_source(move);
        int target_square = MoveBuilder::get_target(move);
        int piece = MoveBuilder::get_piece(move);
        int promoted = MoveBuilder::get_promoted(move);
        bool capture = MoveBuilder::get_capture(move);
        bool double_pawn_push = MoveBuilder::get_double(move);
        bool enpassant_flag = MoveBuilder::get_enpassant(move);
        bool castling_flag = MoveBuilder::get_castling(move);

        pop_bit(bitboards[piece], source_square);
        set_bit(bitboards[piece], target_square);

        if (capture) {
            int start_piece, end_piece;

            if (side == white) {
                start_piece = p;
                end_piece = k;
            } else {
                start_piece = P;
                end_piece = K;   
            }

            for (int bb_piece = start_piece; bb_piece <= end_piece; bb_piece++) {
                if (get_bit(bitboards[bb_piece], target_square)) {
                    pop_bit(bitboards[bb_piece], target_square);
                    break;
                }
            }
        }

        if (promoted) {
            //pop_bit(bitboards[piece], target_square);
            pop_bit(bitboards[(side == white) ? P : p], target_square);
            set_bit(bitboards[promoted], target_square);
        }

        if (enpassant_flag) {
            (side == white) ? (pop_bit(bitboards[p], target_square + 8)) : (pop_bit(bitboards[P], target_square - 8));
        }


        
        /*
        
            BUG
        
        */


        /*
        
            d7 pawn kills at c6 for some fucking reason
            check black pawn move generation

        */

        // printf("enpassant before: %d\n", enpassant_flag);
        enpassant = no_sq;
        // printf("enpassant after: %d\n", enpassant_flag);

        if (double_pawn_push) {
            (side == white) ? (enpassant = target_square + 8) : (enpassant = target_square - 8);
        }


        if (castling_flag) {
            switch (target_square) {

                case (g1):
                    pop_bit(bitboards[R], h1);
                    set_bit(bitboards[R], f1);
                    break;
                case (c1):
                    pop_bit(bitboards[R], a1);
                    set_bit(bitboards[R], d1);
                    break;
                case (g8):
                    pop_bit(bitboards[r], h8);
                    set_bit(bitboards[r], f8);
                    break;
                case (c8):
                    pop_bit(bitboards[r], a8);
                    set_bit(bitboards[r], d8);
                    break;
                    
            }
        }

        castle &= castling_rights[source_square];
        castle &= castling_rights[target_square];

        memset(occupancies, 0ULL, 24);

        for (int bb_piece = P; bb_piece <= K; bb_piece++) {
            occupancies[white] |= bitboards[bb_piece];
        }

        for (int bb_piece = p; bb_piece <= k; bb_piece++) {
            occupancies[black] |= bitboards[bb_piece];
        }

        occupancies[both] |= occupancies[white];
        occupancies[both] |= occupancies[black];

        side ^= 1;

        if (is_square_attacked((side == white) ? get_lsb_index(bitboards[k]) : get_lsb_index(bitboards[K]), side)) {
            backup.restore();
            return 0;
        } else {
            return 1;
        }

    } else {
        if (MoveBuilder::get_capture(move)) {
            make_move(move, all_moves);
        } else { 
            return 0;
        } 
    }
    return -1;
}