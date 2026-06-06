#include <jni.h>
#include <string>

void innit_all();
void parse_position(std::string_view command);
void parse_go(std::string_view command);
std::string get_board_string();
std::string get_move_as_string(int move);
extern int pv_table[64][64];
extern int stopped;
extern int timeset;
void search_position(int depth);
int64_t get_time_ms();
extern uint64_t starttime;


extern "C" JNIEXPORT void JNICALL
Java_com_example_blindfoldchess_ChessEngine_initEngine(JNIEnv* env, jobject thiz) {
    innit_all();
    parse_position("position startpos");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_blindfoldchess_ChessEngine_getBoardState(JNIEnv* env, jobject thiz) {
    std::string current_state = get_board_string();
    return env->NewStringUTF(current_state.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_blindfoldchess_ChessEngine_sendPositionCommand(JNIEnv* env, jobject thiz, jstring command) {
    const char* native_command = env->GetStringUTFChars(command, nullptr);

    parse_position(std::string_view(native_command));

    env->ReleaseStringUTFChars(command, native_command);

    std::string new_state = get_board_string();
    return env->NewStringUTF(new_state.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_blindfoldchess_ChessEngine_sendGoCommand(JNIEnv* env, jobject thiz, jstring command) {
    const char* native_command = env->GetStringUTFChars(command, nullptr);

    parse_go(std::string_view(native_command));

    env->ReleaseStringUTFChars(command, native_command);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_blindfoldchess_ChessEngine_thinkAndGetBestMove(JNIEnv* env, jobject thiz, jstring command) {

    stopped = 0;
    timeset = 0;

    starttime = get_time_ms();

    search_position(5);

    int best_move = pv_table[0][0];
    std::string move_string = get_move_as_string(best_move);

    return env->NewStringUTF(move_string.c_str());
}