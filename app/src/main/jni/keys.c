#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_rheophotos_repository_api_NetworkInterceptor_getAPIKey(JNIEnv *env, jobject instance) {

 return (*env)->  NewStringUTF(env, "5fe78bba22674ba3b834dd6cf668291b");
}