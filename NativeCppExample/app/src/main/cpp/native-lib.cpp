#include <jni.h>
#include <string>
#include <C/PDF/TRN_PDFNet.h>
#include <C/Common/TRN_Types.h>
#include <C/Common/TRN_UString.h>
#include <Common/UString.h>
#include <PDF/PDFNet.h>

#include <PDF/PDFDoc.h>
#include <PDF/ElementBuilder.h>


extern "C" JNIEXPORT jstring JNICALL
Java_com_pdftron_nativecppexample_MainActivity_fontListFromJNI(
        JNIEnv *env,
        jobject /* this */) {

    double version = pdftron::PDFNet::GetVersion();
    //  Ask font list to PDFTron
    pdftron::UString fonts = pdftron::PDFNet::GetSystemFontList();

    // Return result
    auto utf8String = fonts.ConvertToUtf8();
    return env->NewStringUTF(utf8String.c_str());
}
