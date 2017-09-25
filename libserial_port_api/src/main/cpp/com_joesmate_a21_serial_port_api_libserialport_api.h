/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_joesmate_a21_serial_port_api_libserialport_api */

#ifndef _Included_com_joesmate_a21_serial_port_api_libserialport_api
#define _Included_com_joesmate_a21_serial_port_api_libserialport_api
#ifdef __cplusplus
extern "C" {
#endif
#undef com_joesmate_a21_serial_port_api_libserialport_api_BT_PACKAGE
#define com_joesmate_a21_serial_port_api_libserialport_api_BT_PACKAGE 512L
/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_open
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1open
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_set_baud
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1set_1baud
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_close
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1close
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_write
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1write
  (JNIEnv *, jclass, jint, jbyteArray, jint);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_read
 * Signature: (I[BJ)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1read
  (JNIEnv *, jclass, jint, jbyteArray, jlong);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_read_all
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1read_1all
  (JNIEnv *, jclass, jint, jbyteArray);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    flush
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_flush
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_ReadBaseMsg
 * Signature: (I[B[BJ)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1ReadBaseMsg
  (JNIEnv *, jclass, jint, jbyteArray, jbyteArray, jlong);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_ReadBaseMsgFp
 * Signature: (I[B[B[BJ)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1ReadBaseMsgFp
  (JNIEnv *, jclass, jint, jbyteArray, jbyteArray, jbyteArray, jlong);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    card_ADPU
 * Signature: (II[B[I[BI)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_card_1ADPU
  (JNIEnv *, jclass, jint, jint, jbyteArray, jintArray, jbyteArray, jint);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_ReadBaseData
 * Signature: (I[BJ)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1ReadBaseData
  (JNIEnv *, jclass, jint, jbyteArray, jlong);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    device_ReadBaseDataFp
 * Signature: (I[BJ)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_device_1ReadBaseDataFp
  (JNIEnv *, jclass, jint, jbyteArray, jlong);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    GetAID
 * Signature: (IB[B)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_GetAID
  (JNIEnv *, jclass, jint, jbyte, jbyteArray);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    RestDefaultKey
 * Signature: (I[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_RestDefaultKey
  (JNIEnv *, jclass, jint, jbyteArray, jbyteArray);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    DownMKey
 * Signature: (IBB[B)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_DownMKey
  (JNIEnv *, jclass, jint, jbyte, jbyte, jbyteArray);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    DownWKey
 * Signature: (IBBB[B)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_DownWKey
  (JNIEnv *, jclass, jint, jbyte, jbyte, jbyte, jbyteArray);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    ActiveKey
 * Signature: (IBB)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_ActiveKey
  (JNIEnv *, jclass, jint, jbyte, jbyte);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    Raw2Bmp
 * Signature: ([BI[BII)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_Raw2Bmp
  (JNIEnv *, jclass, jbyteArray, jint, jbyteArray, jint, jint);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    RF_Control
 * Signature: (IB)I
 */
JNIEXPORT jint JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_RF_1Control
  (JNIEnv *, jclass, jint, jbyte);

/*
 * Class:     com_joesmate_a21_serial_port_api_libserialport_api
 * Method:    Cancel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_joesmate_a21_serial_1port_1api_libserialport_1api_Cancel
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
