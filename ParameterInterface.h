/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ParameterInterface */

#ifndef _Included_ParameterInterface
#define _Included_ParameterInterface
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ParameterInterface
 * Method:    J_create
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_ParameterInterface_J_1create
  (JNIEnv *, jobject);

/*
 * Class:     ParameterInterface
 * Method:    J_destroy
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_ParameterInterface_J_1destroy
  (JNIEnv *, jobject);

/*
 * Class:     ParameterInterface
 * Method:    J_manage
 * Signature: (Ljava/lang/String;IZ)V
 */
JNIEXPORT void JNICALL Java_ParameterInterface_J_1manage
  (JNIEnv *, jobject, jstring, jint, jboolean);

/*
 * Class:     ParameterInterface
 * Method:    J_parseFrom
 * Signature: (Ljava/lang/String;Z)I
 */
JNIEXPORT jint JNICALL Java_ParameterInterface_J_1parseFrom
  (JNIEnv *, jobject, jstring, jboolean);

/*
 * Class:     ParameterInterface
 * Method:    J_hasValue
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_ParameterInterface_J_1hasValue
  (JNIEnv *, jobject, jstring);

/*
 * Class:     ParameterInterface
 * Method:    J_getString
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_ParameterInterface_J_1getString
  (JNIEnv *, jobject, jstring);

/*
 * Class:     ParameterInterface
 * Method:    J_getInt
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_ParameterInterface_J_1getInt
  (JNIEnv *, jobject, jstring);

/*
 * Class:     ParameterInterface
 * Method:    J_getReal
 * Signature: (Ljava/lang/String;)F
 */
JNIEXPORT jfloat JNICALL Java_ParameterInterface_J_1getReal
  (JNIEnv *, jobject, jstring);

/*
 * Class:     ParameterInterface
 * Method:    J_getBoolean
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_ParameterInterface_J_1getBoolean
  (JNIEnv *, jobject, jstring);

/*
 * Class:     ParameterInterface
 * Method:    J_getList
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_ParameterInterface_J_1getList
  (JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif
