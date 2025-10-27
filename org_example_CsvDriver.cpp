#include <cstddef>
#include <cstdlib>
#include "org_example_CsvDriver.h"


JNIEXPORT void JNICALL Java_org_example_CsvDriver_nativeExec(JNIEnv *env, jobject thisObj, jstring jniCmd) {
    const char *cmd = env->GetStringUTFChars(jniCmd, NULL);
    system(cmd);
}