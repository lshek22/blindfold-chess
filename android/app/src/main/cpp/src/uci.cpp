#include "uci.h"
#include "attack_tables.h"
#include <iostream>
#include "search.h"
#include <unistd.h>
#include "movegen.h"


int quit = 0;

int movestogo = 30;

int movetime = -1;

int search_time = -1;

int inc = 0;

// int starttime = 0;

// int stoptime = 0;

uint64_t starttime = 0;
uint64_t stoptime = 0;

int timeset = 0;

int stopped = 0;


#ifdef _WIN32
    #define WIN32_LEAN_AND_MEAN
    #define NOGDI
    #define NOUSER
    #include <windows.h>
    #include <conio.h>

    int input_waiting() {
        return _kbhit();
    }

#else
    #include <sys/select.h>
    #include <unistd.h>

    int input_waiting() {
        fd_set readfds;
        struct timeval tv;
        FD_ZERO(&readfds);
        FD_SET(fileno(stdin), &readfds);
        tv.tv_sec = 0; tv.tv_usec = 0;
        select(16, &readfds, 0, 0, &tv);
        return (FD_ISSET(fileno(stdin), &readfds));
    }

#endif


void read_input()
{
    int bytes;
    
  
    char input[256] = "", *endc;

  
    if (input_waiting())
    {
        
        stopped = 1;
        
    
        do
        {
           
            bytes=read(fileno(stdin), input, 256);
        }
        
       
        while (bytes < 0);
        
        
        endc = strchr(input,'\n');
        
      
        if (endc) *endc=0;
        
        
        if (strlen(input) > 0)
        {
          
            if (!strncmp(input, "quit", 4))
            {
                  
                quit = 1;
            }

       
            else if (!strncmp(input, "stop", 4))    {
                
                quit = 1;
            }
        }   
    }
}


void communicate() {
	
    if(timeset == 1 && get_time_ms() > stoptime) {
		
		stopped = 1;
	}
	
    
	//read_input();
}


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

            repetition_index++;
            
            repetition_table[repetition_index] = hash_key;
            
            make_move(move, all_moves);
        }
    }
    //print_board();

}

// void parse_go(std::string_view command) {
//     int depth = 6; 
    
//     size_t depth_pos = command.find("depth");
    
//     if (depth_pos != std::string_view::npos) {
//         std::string_view depth_part = command.substr(depth_pos + 5);
        
//         std::istringstream depth_stream{std::string(depth_part)};
        
//         if (!(depth_stream >> depth)) {
//             depth = 6; 
//         }
//     }
    
//     search_position(depth);
//     //printf("depth: %d\n", depth);
// }


void parse_go(std::string_view command) {
    int depth = -1;
    size_t pos = std::string_view::npos;

    // Reset clock tracking defaults per search command
    inc = 0;
    search_time = -1;
    movetime = -1;
    movestogo = 30;
    timeset = 0;

    if (command.find("infinite") != std::string_view::npos) {
        // Handle infinite
    }

    // Modern C++ equivalent: search using command.find()
    // command.data() + pos gives you a pointer to the start of that substring
    if (side == black && (pos = command.find("binc")) != std::string_view::npos)
        inc = atoi(command.data() + pos + 5);

    if (side == white && (pos = command.find("winc")) != std::string_view::npos)
        inc = atoi(command.data() + pos + 5);

    if (side == white && (pos = command.find("wtime")) != std::string_view::npos)
        search_time = atoi(command.data() + pos + 6);

    if (side == black && (pos = command.find("btime")) != std::string_view::npos)
        search_time = atoi(command.data() + pos + 6);

    if ((pos = command.find("movestogo")) != std::string_view::npos)
        movestogo = atoi(command.data() + pos + 10);

    if ((pos = command.find("movetime")) != std::string_view::npos)
        movetime = atoi(command.data() + pos + 9);

    if ((pos = command.find("depth")) != std::string_view::npos)
        depth = atoi(command.data() + pos + 6);

    // Time calculation math...
    if (movetime != -1) {
        search_time = movetime;
        movestogo = 1;
    }

    starttime = get_time_ms();

    if (search_time != -1) {
        timeset = 1;
        search_time /= movestogo;
        search_time -= 50; 
        if (search_time < 0) search_time = 10; 
        stoptime = starttime + search_time + inc;
    }

    if (depth == -1)
        depth = 64;

    printf("time:%d start:%ld stop:%ld depth:%d timeset:%d\n",
           search_time, starttime, stoptime, depth, timeset);

    search_position(depth);
}



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
            //clear_hash_table();
        }
        else if (command.substr(0, 10) == "ucinewgame") 
        {
            parse_position("position startpos");
            clear_hash_table();
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