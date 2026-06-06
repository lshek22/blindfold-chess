#include "movegen.h"
#include "attack_tables.h"
#include "bitboard.h"
#include "enums.h"


const int castling_rights[64] = {
     7, 15, 15, 15,  3, 15, 15, 11,
    15, 15, 15, 15, 15, 15, 15, 15,
    15, 15, 15, 15, 15, 15, 15, 15,
    15, 15, 15, 15, 15, 15, 15, 15,
    15, 15, 15, 15, 15, 15, 15, 15,
    15, 15, 15, 15, 15, 15, 15, 15,
    15, 15, 15, 15, 15, 15, 15, 15,
    13, 15, 15, 15, 12, 15, 15, 14
};

char promoted_pieces[] = {
    ' ', ' ', ' ', ' ', ' ', ' ',
    ' ', ' ', 
    'q', 'r', 'b', 'n',  
    'q', 'r', 'b', 'n'
};

char get_promoted_char(int piece) {
    switch (piece) {
        case Q: case q: return 'q';
        case R: case r: return 'r';
        case B: case b: return 'b';
        case N: case n: return 'n';
        default: return ' ';
    }
}

void print_attacked_squares(int side) {
    printf("\n");
    
    for (int rank = 0; rank < 8; rank++)
    {
        for (int file = 0; file < 8; file++)
        {
            int square = rank * 8 + file;
            
            if (!file)
                printf("  %d ", 8 - rank);
            
            printf(" %d", is_square_attacked(square, side) ? 1 : 0);
        }
        
        printf("\n");
    }
    
    printf("\n     a b c d e f g h\n\n");
}


void print_move(Move move) {
    int promoted = MoveBuilder::get_promoted(move);


    if (promoted)
        printf("%s%s%c", square_to_coordinates[MoveBuilder::get_source(move)],
                           square_to_coordinates[MoveBuilder::get_target(move)],
                           promoted_pieces[promoted]);
    else
        printf("%s%s", square_to_coordinates[MoveBuilder::get_source(move)],
                           square_to_coordinates[MoveBuilder::get_target(move)]);
    
}



void print_move_list(const MoveList& move_list) {

    if (!move_list.get_count()) {
        printf("\n     No move in the move list!\n");
        return;
    }


    printf("\n     move    piece   capture   double    enpass    castling\n\n");
    
    for (int i = 0; i < move_list.get_count(); i++) {
        Move move = move_list.get_move(i);
        
        int promoted = MoveBuilder::get_promoted(move);

        printf("    %s%s%c   %s       %d         %d         %d         %d\n", 
               square_to_coordinates[MoveBuilder::get_source(move)],
               square_to_coordinates[MoveBuilder::get_target(move)],
               promoted ? promoted_pieces[promoted] : ' ',
               unicode_pieces[MoveBuilder::get_piece(move)],
               MoveBuilder::get_capture(move) ? 1 : 0,
               MoveBuilder::get_double(move) ? 1 : 0,
               MoveBuilder::get_enpassant(move) ? 1 : 0,
               MoveBuilder::get_castling(move) ? 1 : 0);
    }

    printf("\n\n    Total number of moves: %d\n\n", move_list.get_count());
}


int64_t get_time_ms() {
    auto now = std::chrono::steady_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch());
    return duration.count();
}


Bitboard nodes;

void perft_test(int depth) {
    printf("\n     Performance test\n\n");
    MoveList move_list;
   
    move_list.clear();
   
    
    generate_moves(move_list);
  

    long start = get_time_ms();

    
    for (int move_count = 0; move_count <move_list.get_count(); move_count++) {   
        
        Move move = move_list.get_move(move_count);

        //BoardCopy backup;
        copy_board();

        if (!make_move(move, all_moves)) {
            continue;
        }

        long cummulative_nodes = nodes;

        
        perft_driver(depth - 1);

        long old_nodes = nodes - cummulative_nodes;

        int source_square = MoveBuilder::get_source(move);
        int target_square = MoveBuilder::get_target(move);
        int promoted = MoveBuilder::get_promoted(move);

        //backup.restore();
        take_back();


        printf("     move: %s%s%c  nodes: %ld\n", square_to_coordinates[source_square],
                                                 square_to_coordinates[target_square],
                                                 promoted ? promoted_pieces[promoted] : ' ',
                                                 old_nodes);
        


    }

    printf("\n    Depth: %d\n", depth);
    printf("    Nodes: %ld\n", nodes);
    printf("     Time: %ld\n\n", get_time_ms() - start);
}