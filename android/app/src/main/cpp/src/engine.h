#pragma once
#include <string>

void init_engine();

void set_position(const std::string& fen);

std::string get_best_move(int depth);

std::string make_engine_move();

std::string get_board();

bool make_move_string(const std::string& moveStr);

bool is_checkmate();

bool insufficient_material();

bool is_draw();
