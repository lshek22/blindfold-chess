#include "src/bitboard.h"
#include "src/enums.h"
#include "src/attack_tables.h"
#include "src/movegen.h"

#include <stdio.h>
#include <string>

using namespace std;

int main(){
    innit_all();

    string a = "8/8/8/3Q4/8/8/8/8 w - - ";
    
    parse_fen(a.data());
    print_board(); 

    print_attacked_squares(white);

}