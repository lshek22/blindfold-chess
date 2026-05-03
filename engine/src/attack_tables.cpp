#include "attack_tables.h"
#include "enums.h"
#include <stdio.h>

Bitboard pawn_attacks[2][64];
Bitboard knight_attacks[64];
Bitboard king_attacks[64];


Bitboard generate_pawn_attacks(int side, int square){

    Bitboard bitboard = 0ULL;
    Bitboard attack_table = 0ULL;

    set_bit(bitboard, square);
    
    // white is 0 so !side == 1
    if (!side) {
        if ((bitboard >> 7) & not_a_file) attack_table |= (bitboard >> 7);
        if ((bitboard >> 9) & not_h_file) attack_table |= (bitboard >> 9);
    } else {
        if ((bitboard << 7) & not_h_file) attack_table |= (bitboard << 7);
        if ((bitboard << 9) & not_a_file) attack_table |= (bitboard << 9);
    }

    //print_bitboard(bitboard);

    return attack_table;
}

Bitboard generate_knight_attacks(int square){

    Bitboard bitboard = 0ULL;
    Bitboard attack_table = 0ULL;

    set_bit(bitboard, square);
    
    
    if ((bitboard >> 17) & not_h_file) attack_table |= (bitboard >> 17);
    if ((bitboard >> 15) & not_a_file) attack_table |= (bitboard >> 15);
    if ((bitboard >> 10) & not_hg_file) attack_table |= (bitboard >> 10);
    if ((bitboard >> 6) & not_ab_file) attack_table |= (bitboard >> 6);
    
    if ((bitboard << 17) & not_a_file) attack_table |= (bitboard << 17);
    if ((bitboard << 15) & not_h_file) attack_table |= (bitboard << 15);
    if ((bitboard << 10) & not_ab_file) attack_table |= (bitboard << 10);
    if ((bitboard << 6) & not_hg_file) attack_table |= (bitboard << 6);
    
    //print_bitboard(bitboard);

    return attack_table;
}

Bitboard generate_king_attacks(int square){

    Bitboard bitboard = 0ULL;
    Bitboard attack_table = 0ULL;

    set_bit(bitboard, square);
    
    
    if (bitboard >> 8) attack_table |= (bitboard >> 8);
    if ((bitboard >> 9) & not_h_file) attack_table |= (bitboard >> 9);
    if ((bitboard >> 7) & not_a_file) attack_table |= (bitboard >> 7);
    if ((bitboard >> 1) & not_h_file) attack_table |= (bitboard >> 1);
    
    if (bitboard << 8) attack_table |= (bitboard << 8);
    if ((bitboard << 9) & not_a_file) attack_table |= (bitboard << 9);
    if ((bitboard << 7) & not_h_file) attack_table |= (bitboard << 7);
    if ((bitboard << 1) & not_a_file) attack_table |= (bitboard << 1);
    
    //print_bitboard(bitboard);

    return attack_table;
}

Bitboard generate_bishop_attacks(int square){

    
    Bitboard attack_table = 0ULL;

    
    int rank, file, target_rank = square / 8, target_file = square % 8;

    for (rank = target_rank + 1, file = target_file + 1;
            file < 7 && rank < 7; file++, rank++) attack_table |= (1ULL << (rank*8+file));
    for (rank = target_rank - 1, file = target_file + 1;
            file < 7 && rank > 0; file++, rank--) attack_table |= (1ULL << (rank*8+file));
    for (rank = target_rank + 1, file = target_file - 1;
            file > 0 && rank < 7; file--, rank++) attack_table |= (1ULL << (rank*8+file));
    for (rank = target_rank - 1, file = target_file - 1;
            file > 0 && rank > 0; file--, rank--) attack_table |= (1ULL << (rank*8+file));

    
    //print_bitboard(bitboard);

    return attack_table;
}


Bitboard bishop_attacks_innit(int square, Bitboard blockers){

    
    Bitboard attack_table = 0ULL;

    
    int rank, file, target_rank = square / 8, target_file = square % 8;

    for (rank = target_rank + 1, file = target_file + 1; file <= 7 && rank <= 7; file++, rank++) {
        attack_table |= (1ULL << (rank*8+file));
        if ((1ULL << (rank*8+file)) & blockers) break;
    }
    for (rank = target_rank - 1, file = target_file + 1; file <= 7 && rank >= 0; file++, rank--) {
        attack_table |= (1ULL << (rank*8+file));
        if ((1ULL << (rank*8+file)) & blockers) break;
    }
    for (rank = target_rank + 1, file = target_file - 1; file >= 0 && rank <= 7; file--, rank++) {
        attack_table |= (1ULL << (rank*8+file));
        if ((1ULL << (rank*8+file)) & blockers) break;
    }
    for (rank = target_rank - 1, file = target_file - 1; file >= 0 && rank >= 0; file--, rank--) {
        attack_table |= (1ULL << (rank*8+file));
        if ((1ULL << (rank*8+file)) & blockers) break;
    }
    

    return attack_table;
}

Bitboard generate_rook_attacks(int square){

    
    Bitboard attack_table = 0ULL;

    
    int rank, file, target_rank = square / 8, target_file = square % 8;

    for (rank = target_rank + 1; rank < 7; rank++) attack_table |= (1ULL << (rank*8+target_file));
    for (rank = target_rank - 1; rank > 0; rank--) attack_table |= (1ULL << (rank*8+target_file));
    for (file = target_file + 1; file < 7; file++) attack_table |= (1ULL << (target_rank*8+file));
    for (file = target_file - 1; file > 0; file--) attack_table |= (1ULL << (target_rank*8+file));

    
    //print_bitboard(bitboard);

    return attack_table;
}

Bitboard rook_attacks_innit(int square, Bitboard blockers){

    
    Bitboard attack_table = 0ULL;

    
    int rank, file, target_rank = square / 8, target_file = square % 8;

    for (rank = target_rank + 1; rank <= 7; rank++){
        attack_table |= (1ULL << (rank*8+target_file));
        if((1ULL << (rank*8+target_file)) & blockers) break;
    }
    for (rank = target_rank - 1; rank >= 0; rank--){
        attack_table |= (1ULL << (rank*8+target_file));
        if((1ULL << (rank*8+target_file)) & blockers) break;
    }
    for (file = target_file + 1; file <= 7; file++){
        attack_table |= (1ULL << (target_rank*8+file));
        if((1ULL << (target_rank*8+file)) & blockers) break;
    }    
    for (file = target_file - 1; file >= 0; file--){
        attack_table |= (1ULL << (target_rank*8+file));
        if((1ULL << (target_rank*8+file)) & blockers) break;
    }
    
    //print_bitboard(bitboard);

    return attack_table;
}


void leaper_precalc_table(){
    for(int square= 0; square<64 ; square++){
        pawn_attacks[white][square] = generate_pawn_attacks(white, square);
        pawn_attacks[black][square] = generate_pawn_attacks(black, square);
        knight_attacks[square] = generate_knight_attacks(square);
        king_attacks[square] = generate_king_attacks(square);

    }

}