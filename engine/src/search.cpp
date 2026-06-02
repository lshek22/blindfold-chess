#include "search.h"
#include <iostream>
#include "tt.h"

int ply;


void search_position(int depth) {

    int score = 0;

    
    nodes = 0;
    stopped = 0;
    follow_pv = 0, score_pv = 0;


    memset(killer_moves, 0, sizeof(killer_moves));
    memset(history_moves, 0, sizeof(history_moves));
    memset(pv_table, 0, sizeof(pv_table));
    memset(pv_length, 0, sizeof(pv_length));

    
    int alpha = -50000;
    int beta = 50000;

    for (int current_depth = 1; current_depth <= depth; current_depth++) {

    if(stopped == 1) break;
            
    follow_pv = 1;

   

    score = negamax(alpha, beta, current_depth);

    if ((score <= alpha) || (score >= beta)) {
        alpha = -50000;    
        beta = 50000;      
        continue;
    }
        
        alpha = score - 50;
        beta = score + 50;


    // best move placeholder
    // printf("bestmove ");
    // print_move(best_move);
    // printf("\n");

    printf("info score cp %d depth %d nodes %ld pv ", score, current_depth, nodes);


    for (int count = 0; count < pv_length[0]; count++)
    {
        
        print_move(pv_table[0][count]);
        printf(" ");
    }

    std::cout << "\n";
        

    }
    

    std::cout << "bestmove ";
    print_move(pv_table[0][0]);
    std::cout << "\n";
    //std::cout.flush();
    std::cout.flush();
    


    
}

void print_move_scores(MoveList move_list) {
    printf("     Move scores:\n\n");
        
    for (int count = 0; count < move_list.get_count(); count++)
    {
        printf("     move: ");
        print_move(move_list.get_move(count));
        printf(" score: %d\n", score_move(move_list.get_move(count)));
    }
}