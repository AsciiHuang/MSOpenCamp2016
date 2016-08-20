#include <jni.h>
#include <stdio.h>
#include <string.h>
#include "md5.h"

static int getSignature(JNIEnv* env, jobject thiz, jobject context)
{
    jclass context_clazz = (*env)->GetObjectClass(env, context);
    jmethodID methodID_getPackageManager = (*env)->GetMethodID(env, context_clazz, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject packageManager = (*env)->CallObjectMethod(env, context, methodID_getPackageManager);
    jclass pm_clazz = (*env)->GetObjectClass(env, packageManager);
    jmethodID methodID_pm = (*env)->GetMethodID(env, pm_clazz, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jmethodID methodID_pack = (*env)->GetMethodID(env, context_clazz, "getPackageName", "()Ljava/lang/String;");
    jstring application_package = (*env)->CallObjectMethod(env, context, methodID_pack);
    jobject packageInfo = (*env)->CallObjectMethod(env, packageManager, methodID_pm, application_package, 64);
    jclass packageinfo_clazz = (*env)->GetObjectClass(env, packageInfo);

    jfieldID fieldID_signatures = (*env)->GetFieldID(env, packageinfo_clazz, "signatures", "[Landroid/content/pm/Signature;");
    jobjectArray signature_arr = (jobjectArray)(*env)->GetObjectField(env, packageInfo, fieldID_signatures);
    jobject signature = (*env)->GetObjectArrayElement(env, signature_arr, 0);
    jclass signature_clazz = (*env)->GetObjectClass(env, signature);
    jmethodID methodID_hashcode = (*env)->GetMethodID(env, signature_clazz, "hashCode", "()I");
    int hashCode = (*env)->CallIntMethod(env, signature, methodID_hashcode);

    return hashCode;
}

static jstring JNICALL Java_com_ascii_jnisoproject_ReceiptAPIHelper_getParams(JNIEnv *env, jobject thiz, jobject context) {
    char checkValue[43] = "{'\0'}";
    unsigned char decrypt[16];
    int nowTime = time(NULL);

    int signatureHash = getSignature(env, thiz, context);
    sprintf(checkValue, "%dAscii_Receipt_Open_Camp%d", nowTime, signatureHash);
    md5(&checkValue, decrypt);

    char tmp[3] = {'\0'};
    char checksum[33] = {'\0'};
    int i;
    for (i = 0; i < 16; i++) {
        sprintf(tmp, "%02x", (unsigned char)decrypt[i] );
        strcat(checksum, tmp);
    }

    char param[57] = {'\0'};
    sprintf(param, "checksum=%s&now=%d", checksum, nowTime);
    jstring result = (*env)->NewStringUTF(env, param);

    return result;
}

#define registerMethods(a,b,c) _registerMethods(a, b, c, sizeof(c)/sizeof(*c))
static int _registerMethods(JNIEnv *env, char *className, JNINativeMethod *list, int methods)
{
    jclass class;
    int res = 1;

    class = (*env)->FindClass(env, className);
    if(class == 0) {
      return 0;
    }

    if((*env)->RegisterNatives(env, class, list, methods) < 0) {
      res = 0;
    }
    return res;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = 0;
    jint res = -1;

    do {
        if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
            break;
        }
        static JNINativeMethod jniMethods[] = {
                {"getParams", "(Landroid/content/Context;)Ljava/lang/String;",
                        (void *) Java_com_ascii_jnisoproject_ReceiptAPIHelper_getParams}
        };
        if (!registerMethods(env, "com/ascii/jnisoproject/ReceiptAPIHelper", jniMethods)) {
            break;
        }
        res = JNI_VERSION_1_4;
    } while(0);

    return res;
}
