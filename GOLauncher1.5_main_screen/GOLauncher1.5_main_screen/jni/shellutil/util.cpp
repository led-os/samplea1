#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/bitmap.h>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <android/log.h>

#include "util.h"
//#include "log.h"

#define  LOG_TAG    "ShellUtil"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define MAX(a, b) ((a) > (b) ? (a) : (b))
#define MIN(a, b) ((a) < (b) ? (a) : (b))

const char TGA_UHEADER[] = { 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

void Java_com_jiubang_ggheart_plugin_shell_ShellUtil_glVertexAttribPointer(JNIEnv * env, jclass cls,
		jint indx, jint size, jint type, jboolean normalized, jint stride,
		jint offset) {
	glVertexAttribPointer(indx, size, type, normalized, stride,
			(const void*) (offset));
}

void Java_com_jiubang_ggheart_plugin_shell_ShellUtil_glDrawElements(JNIEnv * env, jclass cls,
		jint mode, jint count, jint type, jint offset) {
	glDrawElements(mode, count, type, (const void*) (offset));
}

void Java_com_jiubang_ggheart_plugin_shell_ShellUtil_saveScreenshotTGA(JNIEnv * env, jclass cls, jint x,
		jint y, jint w, jint h, jstring fileName) {

	const char *nativeFileName = env->GetStringUTFChars(fileName, 0);

	unsigned short width = w, height = h;
	unsigned char bpp = 32;
	unsigned char c = 0;
	unsigned imageSize = w * h * (bpp / 8);
	unsigned char * data = new unsigned char[imageSize];
	if (!data) {
		LOGE("Cannot allocate memory.");
		return;
	}

	FILE* f = fopen(nativeFileName, "wb");
	if (!f) {
		LOGE("Cannot open file \"%s\"", nativeFileName);
		return;
	}

	glReadPixels(x, y, w, h, GL_RGBA, GL_UNSIGNED_BYTE, data);
	//Swap red and blue bytes
	for (unsigned int i = 0, Bpp = (bpp / 8); i < imageSize; i += Bpp) {
		unsigned char tmp = data[i];
		data[i] = data[i + 2];
		data[i + 2] = tmp;
	}
	fwrite(TGA_UHEADER, 1, sizeof(TGA_UHEADER), f);
	fwrite(&width, 1, 2, f);
	fwrite(&height, 1, 2, f);
	fwrite(&bpp, 1, 1, f);
	fwrite(&c, 1, 1, f);
	fwrite(data, 1, imageSize, f);

	fclose(f);
	delete[] data;

	env->ReleaseStringUTFChars(fileName, nativeFileName);
}

void Java_com_jiubang_ggheart_plugin_shell_ShellUtil_saveScreenshot(JNIEnv *env, jclass cls, jint x,
		jint y, jint w, jint h, jintArray buffer) {
	int* nativeBuffer = (int*) env->GetIntArrayElements(buffer, 0);

	unsigned short width = w, height = h;
	unsigned char bpp = 32;
	unsigned imageSize = w * h * (bpp / 8);
	unsigned char * data = (unsigned char *) nativeBuffer;

	glReadPixels(x, y, w, h, GL_RGBA, GL_UNSIGNED_BYTE, data);

	int lineWidth = w;
	unsigned int* src = (unsigned int*) nativeBuffer;
	unsigned int* dst = src + lineWidth * (h - 1);
	for(int i = 0; i < h - 1 - i; ++i){
		for(int j = 0; j < w; ++j, ++src, ++dst){
			register int pixel1 = *src;
			register int pixel2 = *dst;
			*dst = ((pixel1 & 0xFF) << 16) | ((pixel1 >> 16) & 0xFF) | (pixel1 & 0xFF00FF00);
			*src = ((pixel2 & 0xFF) << 16) | ((pixel2 >> 16) & 0xFF) | (pixel2 & 0xFF00FF00);
		}
		dst -= lineWidth * 2;
	}

	env->ReleaseIntArrayElements(buffer, nativeBuffer, 0);
}

jboolean Java_com_jiubang_ggheart_plugin_shell_ShellUtil_convertToHSVInternal
  (JNIEnv *env, jclass cls, jobject bitmap, jboolean optimized)
{
	AndroidBitmapInfo	info;
    unsigned char*		pixels;
    int					ret;

	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return false;
	}

	if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		LOGE("Bitmap format is not RGB_RGBA !");
		return false;
	}

	int sizeOfPixels = info.width * info.height * 4;
	if (sizeOfPixels <= 0) {
		LOGE("Bitmap size is not positive !");
		return false;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void **) &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		return false;
	}

    unsigned char* endOfPixels = pixels + sizeOfPixels;

	if (!optimized) {
		while (pixels < endOfPixels) {
			unsigned int R = pixels[0];
			unsigned int G = pixels[1];
			unsigned int B = pixels[2];
			unsigned int A = pixels[3];
			int maxRGB = MAX(R, MAX(G, B));
			int minRGB = MIN(R, MIN(G, B));
			float deltaRGB = maxRGB - minRGB;
			float h = 0;
			float s = 0;
			float V = maxRGB; 						// V = v * a
			if (deltaRGB > 0) {
				s = deltaRGB / V;
				if (R == maxRGB) {
					h = (G - B) / deltaRGB;
				} else if (G == maxRGB) {
					h = (B - R) / deltaRGB + 2.0f;
				} else {
					h = (R - G) / deltaRGB + 4.0f;
				}
			}
			pixels[0] = (int) (h * (255 / 6.0f));	// h
			pixels[1] = (int) (s * V); 				// s * v * a
			pixels[2] = (int) ((1 - s) * V); 		// (1 - s) * v * a

			pixels += 4;
		}
	} else {
		while (pixels < endOfPixels) {
			// if optimized, we assume: R >= G = B,
			// it implies, hue == 0, and we only can set hue rather than shift hue.
			// outR will be ignored, outG = s * V = R - G, outB = (1 - s) * V = B
			pixels[1] = pixels[0] - pixels[1];

			pixels += 4;
		}
	}

    AndroidBitmap_unlockPixels(env, bitmap);

    return true;
}
