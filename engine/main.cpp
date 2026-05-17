#include "src/bitboard.h"
#include "src/enums.h"
#include "src/attack_tables.h"
#include "src/movegen.h"
#include "src/uci.h"
#include "src/evaluation.h"

#include <stdio.h>
#include <string>
#include <inttypes.h>

using namespace std;

int main(){
    innit_all();

    string a = "rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 1 ";

    //uci_loop();

    parse_fen(a.data());
    print_board();
    printf("Score: %d\n", evaluate());

}