#include "src/bitboard.h"
#include "src/enums.h"
#include "src/attack_tables.h"
#include "src/movegen.h"
#include "src/uci.h"
// #include "src/evaluation.h"
#include "src/search.h"
#include "src/tt.h"



#include <stdio.h>
#include <string>
#include <inttypes.h>

using namespace std;

int main(){
    innit_all();

    // string a = "8/8/8/P7/p7/8/8/8 w - - ";

    uci_loop();

    // parse_fen(a.data());
    // print_board();
    // printf("score: %d\n", evaluate());

    // parse_fen(repetitions.data());
    // print_board();

    // /search_position(10);
    //generate_hash_key();
    //perft_test(6);



}