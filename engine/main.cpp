#include "src/bitboard.h"
#include "src/enums.h"
#include "src/attack_tables.h"
#include "src/movegen.h"

#include <stdio.h>
#include <string>

using namespace std;

int main(){
    innit_all();

    string a = "r3k2r/p1ppRpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBqPPP/R3K2R b KQkq - 0 1 ";

    parse_fen(a.data());
    print_board();
    
    MoveList move_list;
    move_list.clear();

    generate_moves(move_list);
    
    for (int move_count = 0; move_count <move_list.get_count(); move_count++) {


        Move move = move_list.get_move(move_count);

        BoardCopy backup;

        if (!make_move(move, all_moves)) {
            continue;
        }
        print_board();
        //print_bitboard(occupancies[both]);
        getchar();

        backup.restore();
        print_board();
        //print_bitboard(occupancies[both]);
        getchar();
    }
    
    print_board();

}