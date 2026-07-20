#include <jni.h>
#include <string>

#include "engine.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_example_blindfoldchess_Engine_initEngine(
        JNIEnv* env,
        jobject /* this */) {

    init_engine();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_blindfoldchess_Engine_setPosition(
        JNIEnv* env,
        jobject /* this */,
        jstring fen) {

    const char* fenChars = env->GetStringUTFChars(fen, nullptr);

    set_position(std::string(fenChars));

    env->ReleaseStringUTFChars(fen, fenChars);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_blindfoldchess_Engine_getBestMove(
        JNIEnv* env,
        jobject /* this */,
        jint depth) {

    std::string move = get_best_move(depth);

    return env->NewStringUTF(move.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_blindfoldchess_Engine_getBoard(
        JNIEnv* env,
        jobject /* this */) {

    std::string board = get_board();

    return env->NewStringUTF(board.c_str());
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_blindfoldchess_Engine_makeMove(
        JNIEnv* env,
        jobject thiz,
        jstring move) {

    const char* str = env->GetStringUTFChars(move, nullptr);

    bool success = make_move_string(str);

    env->ReleaseStringUTFChars(move, str);

    return success;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_blindfoldchess_Engine_isCheckmate(
        JNIEnv* env,
        jobject thiz) {

    return is_checkmate();
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_blindfoldchess_Engine_isDraw(
        JNIEnv* env,
        jobject thiz) {

    return is_draw();
}