#include "tt.h"


TT_Entry hash_table[hash_size]; 

void clear_hash_table() {
    for (int index = 0; index < hash_size; index++) {
        hash_table[index].hash_key = 0;
        hash_table[index].depth = 0;
        hash_table[index].flag = 0;
        hash_table[index].score = 0;
    }
}