#include "search.h"
#include <iostream>


int ply;

TT_Entry hash_table[hash_size]; 

void clear_hash_table() {
    for (int index = 0; index < hash_size; index++) {
        hash_table[index].hash_key = 0;
        hash_table[index].depth = 0;
        hash_table[index].flag = 0;
        hash_table[index].score = 0;
    }
}


void search_position(int depth) {

    int score = 0;

    
    nodes = 0;
    stopped = 0;
    follow_pv = 0, score_pv = 0;


    memset(killer_moves, 0, sizeof(killer_moves));
    memset(history_moves, 0, sizeof(history_moves));
    memset(pv_table, 0, sizeof(pv_table));
    memset(pv_length, 0, sizeof(pv_length));

    
    int alpha = -infinity;
    int beta = infinity;

    for (int current_depth = 1; current_depth <= depth; current_depth++) {

        if(stopped == 1) break;
                
        follow_pv = 1;

    

        score = negamax(alpha, beta, current_depth);

        if ((score <= alpha) || (score >= beta)) {
            alpha = -infinity;    
            beta = infinity;      
            continue;
        }
            
        alpha = score - 50;
        beta = score + 50;



        if (score > -mate_value && score < -mate_score)
            printf("info score mate %d depth %d nodes %ld time %ld pv ", -(score + mate_value) / 2 - 1, current_depth, nodes, get_time_ms() - starttime);
        
        else if (score > mate_score && score < mate_value)
            printf("info score mate %d depth %d nodes %ld time %ld pv ", (mate_value - score) / 2 + 1, current_depth, nodes, get_time_ms() - starttime);   
        else
            printf("info score cp %d depth %d nodes %ld time %ld pv ", score, current_depth, nodes, get_time_ms() - starttime);
        
        
        
        
        
        //printf("info score cp %d depth %d nodes %ld pv ", score, current_depth, nodes);


        for (int count = 0; count < pv_length[0]; count++) {
            
            print_move(pv_table[0][count]);
            printf(" ");
        }

        std::cout << "\n";
            

    }
    

    // std::cout << "bestmove ";
    // print_move(pv_table[0][0]);
    // std::cout << "\n";
    // //std::cout.flush();
    // std::cout.flush();

    printf("bestmove ");
    print_move(pv_table[0][0]);
    printf("\n");
    fflush(stdout);
    


    
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