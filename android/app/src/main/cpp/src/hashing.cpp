#include "hashing.h"
#include "bitboard.h"
#include "attack_tables.h"

namespace HashKeys {
    Bitboard piece_keys[12][64];
    Bitboard enpassant_keys[64];
    Bitboard castle_keys[16];
    Bitboard side_key;

    void init_random_keys() {
        state = 1804289383;

        for (int piece = P; piece <= k; piece++) {
            for (int square = 0; square < 64; square++) {
                piece_keys[piece][square] = get_random_U64_number();
            }
        }

        for (int square = 0; square < 64; square++) {
            enpassant_keys[square] = get_random_U64_number();
        }
        for (int index = 0; index < 16; index++){
            castle_keys[index] = get_random_U64_number();
        }
    
        HashKeys::side_key = get_random_U64_number();
    }

    Bitboard generate_hash_key() {
        Bitboard final_key = 0ULL;
    
        Bitboard bitboard;
        
        for (int piece = P; piece <= k; piece++) {
            bitboard = bitboards[piece];
            
            while (bitboard) {
                int square = get_lsb_index(bitboard);
                
                final_key ^= piece_keys[piece][square];
                
                pop_bit(bitboard, square);
            }
        }
        
        if (enpassant != no_sq) {
            final_key ^= enpassant_keys[enpassant];
        }
        final_key ^= castle_keys[castle];
        
        if (side == black) final_key ^= HashKeys::side_key;
        
        return final_key;
    }
}