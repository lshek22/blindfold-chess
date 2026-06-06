#pragma once

#include "bitboard.h"
#include "enums.h"
#include "attack_tables.h"
#include "movegen.h"


#include <string>
#include <string_view>
#include <sstream>
#include <cstring>
#include <cstdlib>
#include <cstdio>



int parse_move(std::string move_string);

void parse_position(std::string_view command);

void parse_go(std::string_view command);

void uci_loop();


extern int quit;

extern int movestogo;

extern int movetime;

extern int search_time;

extern int inc;

// extern int starttime;

// extern int stoptime;

extern uint64_t starttime;
extern uint64_t stoptime;

extern int timeset;

extern int stopped;

int input_waiting();

void read_input();

void communicate();