#include "bitboard.h"
#include "enums.h"
#include <stdio.h>
#include "attack_tables.h"
#include <cstring>


string empty_board = "8/8/8/8/8/8/8/8 w - - ";
string start_position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ";
string tricky_position = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1 ";
string killer_position = "rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1";
string cmk_position = "r2q1rk1/ppp2ppp/2n1bn2/2b1p3/3pP3/3P1NPP/PPP1NPB1/R1BQ1RK1 b - - 0 9 ";


int side = -1;
int enpassant = no_sq;
int castle = 0;

char ascii_pieces[] = "PNBRQKpnbrqk";

char const *unicode_pieces[12] = {"♙", "♘", "♗", "♖", "♕", "♔", "♟︎", "♞", "♝", "♜", "♛", "♚"};

int char_pieces[128];

const char *square_to_coordinates[] = {
    "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
    "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
    "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
    "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
    "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
    "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
    "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
    "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
};

void init_char_pieces() {
    char_pieces['P'] = P;
    char_pieces['N'] = N;
    char_pieces['B'] = B;
    char_pieces['R'] = R;
    char_pieces['Q'] = Q;
    char_pieces['K'] = K;
    char_pieces['p'] = p;
    char_pieces['n'] = n;
    char_pieces['b'] = b;
    char_pieces['r'] = r;
    char_pieces['q'] = q;
    char_pieces['k'] = k;
}

Bitboard bitboards[12];
Bitboard occupancies[3];

void innit_all(){
    init_char_pieces();
    leaper_precalc_table();
    //init_magic_numbers();
    init_sliders_attacks(bishop);
    init_sliders_attacks(rook);
    //set_up_board();
}

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

void print_board() {
    for (int rank = 0; rank < 8; rank++) {
        for (int file = 0; file < 8; file++) {
            int square = rank * 8 + file;

            if(!file) {
                printf(" %d", 8 - rank);
            }

            int piece = -1;

            for (int bb_piece = P; bb_piece <= k; bb_piece++)
            {
                if (get_bit(bitboards[bb_piece], square))
                    piece = bb_piece;
            }

            printf(" %s", (piece == -1) ? "." : unicode_pieces[piece]);
        }
        printf("\n");
    }
    printf("\n   a b c d e f g h\n\n");

    printf("     Side:     %s\n", !side ? "white" : "black");
    
    printf("     Enpassant:   %s\n", (enpassant != no_sq) ? square_to_coordinates[enpassant] : "no");
    
    printf("     Castling:  %c%c%c%c\n\n", (castle & wk) ? 'K' : '-',
                                           (castle & wq) ? 'Q' : '-',
                                           (castle & bk) ? 'k' : '-',
                                           (castle & bq) ? 'q' : '-');

}

void set_up_board() {
    // set white pawns
    set_bit(bitboards[P], a2);
    set_bit(bitboards[P], b2);
    set_bit(bitboards[P], c2);
    set_bit(bitboards[P], d2);
    set_bit(bitboards[P], e2);
    set_bit(bitboards[P], f2);
    set_bit(bitboards[P], g2);
    set_bit(bitboards[P], h2);
    
    // set white knights
    set_bit(bitboards[N], b1);
    set_bit(bitboards[N], g1);
    
    // set white bishops
    set_bit(bitboards[B], c1);
    set_bit(bitboards[B], f1);
    
    // set white rooks
    set_bit(bitboards[R], a1);
    set_bit(bitboards[R], h1);
    
    // set white queen & king
    set_bit(bitboards[Q], d1);
    set_bit(bitboards[K], e1);
    
    // set white pawns
    set_bit(bitboards[p], a7);
    set_bit(bitboards[p], b7);
    set_bit(bitboards[p], c7);
    set_bit(bitboards[p], d7);
    set_bit(bitboards[p], e7);
    set_bit(bitboards[p], f7);
    set_bit(bitboards[p], g7);
    set_bit(bitboards[p], h7);
    
    // set white knights
    set_bit(bitboards[n], b8);
    set_bit(bitboards[n], g8);
    
    // set white bishops
    set_bit(bitboards[b], c8);
    set_bit(bitboards[b], f8);
    
    // set white rooks
    set_bit(bitboards[r], a8);
    set_bit(bitboards[r], h8);
    
    // set white queen & king
    set_bit(bitboards[q], d8);
    set_bit(bitboards[k], e8);
    
    // init side
    side = black;
    
    // init enpassant
    enpassant = e3;
    
    // init castling
    castle |= wk;
    castle |= wq;
    castle |= bk;
    castle |= bq;
    
    // print chess board
    print_board();
}

void parse_fen(char *fen) {
    memset(bitboards, 0ULL, sizeof(bitboards));
    
    memset(occupancies, 0ULL, sizeof(occupancies));

    side = 0;
    enpassant = no_sq;
    castle = 0;

    for (int rank = 0; rank < 8; rank++) {
        for (int file = 0; file < 8; file++) {
            int square = rank * 8 + file;

            if ((*fen >= 'a' && *fen <= 'z') || (*fen >= 'A' && *fen <= 'Z')) {
                int piece = char_pieces[*fen];

            

                set_bit(bitboards[piece], square);


                *fen++; 
            }

            if (*fen >= '0' && *fen <= '9') {
                int offset = *fen - '0';

                int piece = -1;

                for (int p = P; p <= k; p++) {
                    if (get_bit(bitboards[p], square)) {
                        piece = p;
                    }
                }

                if (piece == -1) {
                    file--;
                }

                file += offset;
                *fen++;
            }

            if (*fen == '/') {
                *fen++;
            }

        }
    }

    /*
    
        check *fen is uncesessary fen is enough
    
    
    */


    *fen++;

    if (*fen == 'w') {
        side = white;
    } else {
        side = black;
    }

    fen += 2;

    while (*fen != ' ') {

        switch (*fen) {
            case 'K' : castle |= wk; break;
            case 'Q' : castle |= wq; break;
            case 'k' : castle |= bk; break;
            case 'q' : castle |= bq; break;
            case '-' : break;
        }

        *fen++;
    }
   

    *fen++;

    if (*fen != '-') {
        int file = fen[0] -'a';
        int rank = 8 - (fen[1] - '0');

        enpassant = rank * 8 + file;
    } else {
        enpassant = no_sq;
    }

    for (int piece = P; piece <= K; piece++) {
        occupancies[white] |= bitboards[piece];
    }

    for (int piece = p; piece <= k; piece++) {
        occupancies[black] |= bitboards[piece];
    }

    occupancies[both] |= occupancies[white];
    occupancies[both] |= occupancies[black];


    printf("fen : '%s'\n", fen);

}