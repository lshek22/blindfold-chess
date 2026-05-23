#include "uci.h"
#include "attack_tables.h"
#include <iostream>
#include "search.h"




int parse_move(string move_string) {
    MoveList move_list;
    move_list.clear();


    generate_moves(move_list);

    int source_square = ( move_string[0] - 'a' ) + ( 8 - ( move_string[1] - '0' ) ) * 8;
    int target_square = ( move_string[2] - 'a' ) + ( 8 - ( move_string[3] - '0' ) ) * 8;
    

    for (int move_count = 0; move_count < move_list.get_count(); move_count++) {
        Move move = move_list.get_move(move_count);



        if (source_square == MoveBuilder::get_source(move) && target_square == MoveBuilder::get_target(move)) {
            
            
            int promoted_piece = MoveBuilder::get_promoted(move);
            
            if (promoted_piece) {
                if ((promoted_piece == Q || promoted_piece == q) && move_string[4] == 'q')
                    return move;
                
                else if ((promoted_piece == R || promoted_piece == r) && move_string[4] == 'r')
                    return move;
                
                else if ((promoted_piece == B || promoted_piece == b) && move_string[4] == 'b')
                    return move;
                
                else if ((promoted_piece == N || promoted_piece == n) && move_string[4] == 'n')
                    return move;
                
                continue;
            }

            return move;
        }
    }

    return 0;
}

void parse_position(std::string_view command) {
    if (command.substr(0, 9) == "position ") {
        command.remove_prefix(9);
    }

    size_t moves_pos = command.find("moves");
    std::string_view setup_part = (moves_pos == std::string_view::npos) 
                                  ? command 
                                  : command.substr(0, moves_pos);

    if (setup_part.substr(0, 8) == "startpos") {
        parse_fen(start_position.data());
    } else {
        size_t fen_pos = setup_part.find("fen ");
        if (fen_pos != std::string_view::npos) {
            std::string fen_str(setup_part.substr(fen_pos + 4));
            parse_fen(fen_str.data());
        } else {
            parse_fen(start_position.data());
        }
    }

    if (moves_pos != std::string_view::npos) {
        std::string_view moves_part = command.substr(moves_pos + 5);
        
        std::string move_token;
        std::istringstream moves_stream{std::string(moves_part)};

        while (moves_stream >> move_token) {
            int move = parse_move(move_token.data());
            
            if (move == 0) {
                break;
            }
            
            make_move(move, all_moves);
        }
    }
    //print_board();

}

void parse_go(std::string_view command) {
    int depth = 6; 
    
    size_t depth_pos = command.find("depth");
    
    if (depth_pos != std::string_view::npos) {
        std::string_view depth_part = command.substr(depth_pos + 5);
        
        std::istringstream depth_stream{std::string(depth_part)};
        
        if (!(depth_stream >> depth)) {
            depth = 6; 
        }
    }
    
    search_position(depth);
    //printf("depth: %d\n", depth);
}


// void parse_go(std::string_view command) {
//     int depth = 4;  // fallback depth
    
//     // check for explicit depth
//     size_t depth_pos = command.find("depth");
//     if (depth_pos != std::string_view::npos) {
//         std::istringstream depth_stream{std::string(command.substr(depth_pos + 5))};
//         if (!(depth_stream >> depth)) depth = 4;
//         search_position(depth);
//         return;
//     }

//     // handle time-based: wtime/btime/movetime
//     int movetime = -1;
//     int wtime = -1, btime = -1, movestogo = 40;

//     auto parse_int = [&](std::string_view cmd, const char* token) -> int {
//         size_t pos = cmd.find(token);
//         if (pos == std::string_view::npos) return -1;
//         std::istringstream s{std::string(cmd.substr(pos + strlen(token)))};
//         int val; s >> val; return val;
//     };

//     movetime  = parse_int(command, "movetime ");
//     wtime     = parse_int(command, "wtime ");
//     btime     = parse_int(command, "btime ");
//     movestogo = parse_int(command, "movestogo ");
//     if (movestogo == -1) movestogo = 40;

//     if (movetime != -1) {
//         // fixed time per move — just use depth for now
//         depth = 5;
//     } else if (wtime != -1 || btime != -1) {
//         // allocate time simply: pick depth based on available time
//         int time = (side == white) ? wtime : btime;
//         if      (time > 60000) depth = 6;
//         else if (time > 10000) depth = 5;
//         else                   depth = 4;
//     }

//     search_position(depth);
// }


void uci_loop() {
    // std::ios_base::sync_with_stdio(false);
    // std::cin.tie(nullptr);
    setbuf(stdin, NULL);  
    setbuf(stdout, NULL);

    // std::cout << "id name HERRENIUM\n";
    // std::cout << "id author lshek22\n";
    // std::cout << "uciok\n" << std::endl;

    std::cout << "id name HERRENIUM\n";
    std::cout << "id author lshek22\n";
    std::cout << "uciok\n";
    //std::cout.flush();

    std::string input;
    input.reserve(2000);

    while (std::getline(std::cin, input)) {
        if (!input.empty() && input.back() == '\r') {
            input.pop_back();
        }

        if (input.empty()) {
            continue;
        }

        std::string_view command{input};

        if (command.substr(0, 7) == "isready")  {
            // std::cout << "readyok\n" << std::endl;
            std::cout << "readyok\n";
            // std::cout.flush();
        }
        else if (command.substr(0, 8) == "position") 
        {
            parse_position(command);
        }
        else if (command.substr(0, 10) == "ucinewgame") 
        {
            parse_position("position startpos");
        }
        else if (command.substr(0, 2) == "go") 
        {
            parse_go(command);
        }
        else if (command.substr(0, 4) == "quit") 
        {
            break;
        }
        else if (command.substr(0, 3) == "uci") 
        {
            // std::cout << "id name HERRENIUM\n";
            // std::cout << "id author lshek22\n";
            // std::cout << "uciok\n" << std::endl;

            std::cout << "id name HERRENIUM\n";
            std::cout << "id author lshek22\n";
            std::cout << "uciok\n";
            // std::cout.flush();
        }
    }
}