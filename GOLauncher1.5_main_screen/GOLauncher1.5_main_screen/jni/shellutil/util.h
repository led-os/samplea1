/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_jiubang_ggheart_plugin_shell_ShellUtil */

#ifndef _Included_com_jiubang_ggheart_plugin_shell_ShellUtil
#define _Included_com_jiubang_ggheart_plugin_shell_ShellUtil
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_jiubang_ggheart_plugin_shell_ShellUtil
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jiubang_ggheart_plugin_shell_ShellUtil_init
  (JNIEnv *, jclass);

/*
 * Class:     com_jiubang_ggheart_plugin_shell_ShellUtil
 * Method:    glVertexAttribPointer
 * Signature: (IIIZII)V
 */
JNIEXPORT void JNICALL Java_com_jiubang_ggheart_plugin_shell_ShellUtil_glVertexAttribPointer
  (JNIEnv *, jclass, jint, jint, jint, jboolean, jint, jint);

/*
 * Class:     com_jiubang_ggheart_plugin_shell_ShellUtil
 * Method:    glDrawElements
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_com_jiubang_ggheart_plugin_shell_ShellUtil_glDrawElements
  (JNIEnv *, jclass, jint, jint, jint, jint);

/*
 * Class:     com_jiubang_ggheart_plugin_shell_ShellUtil
 * Method:    saveScreenshotTGA
 * Signature: (IIIILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_jiubang_ggheart_plugin_shell_ShellUtil_saveScreenshotTGA
  (JNIEnv *, jclass, jint, jint, jint, jint, jstring);

/*
 * Class:     com_jiubang_ggheart_plugin_shell_ShellUtil
 * Method:    saveScreenshot
 * Signature: (IIII[I)V
 */
JNIEXPORT void JNICALL Java_com_jiubang_ggheart_plugin_shell_ShellUtil_saveScreenshot
  (JNIEnv *, jclass, jint, jint, jint, jint, jintArray);

/*
 * Class:     com_jiubang_ggheart_plugin_shell_ShellUtil
 * Method:    convertToHSVInternal
 * Signature: (Landroid/graphics/Bitmap;Z)Z
 */
JNIEXPORT jboolean JNICALL Java_com_jiubang_ggheart_plugin_shell_ShellUtil_convertToHSVInternal
  (JNIEnv *, jclass, jobject, jboolean);

#ifdef __cplusplus
}
#endif
#endif
