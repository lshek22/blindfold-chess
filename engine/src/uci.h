#pragma once

#include "bitboard.h"
#include "enums.h"
#include "attack_tables.h"
#include "movegen.h"

#include <string>
#include <string_view>
#include <sstream>


void search_position(int depth);

int parse_move(string move_string);

void parse_position(std::string_view command);

void parse_go(std::string_view command);

void uci_loop();