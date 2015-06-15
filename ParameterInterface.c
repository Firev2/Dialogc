#include <jni.h>
#include "ParameterManager.h"
#include "ParameterInterface.h"
#include <stdio.h>

ParameterManager* pm = NULL;
FILE* fp; /* global file solves some weird JVM crash error, need to fix this  */

/*
Function J_create() : Creates a new ParameterManager object and stores it in the global pm variable for usage later.
pre: n/a
post: pm is written with a newly created ParameterManager
*/
JNIEXPORT void JNICALL Java_ParameterInterface_J_1create(JNIEnv * env, jobject obj) {
       
     pm = PM_create(1);
}

/*
Function J_destroy() : destroys any data associated with the global pm variable. 
pre: pm is a non-null pointer to a ParameterManager
post: any memory allocated in pm is freed
*/
JNIEXPORT void JNICALL Java_ParameterInterface_J_1destroy(JNIEnv * env, jobject obj) {

    if(pm != NULL) {
        PM_destroy(pm);
    }
}

/*
Function J_manage(name, type, required) : Registers a parameter name for use in parsing later.
pre: type is an integer between 1 and 5. name is a valid string.
post: registers name and stores it in pm's list of registered parameters.
*/
JNIEXPORT void JNICALL Java_ParameterInterface_J_1manage(JNIEnv *env, jobject obj, jstring name, int type, jboolean required) {

    if(pm == (ParameterManager*)NULL)
        return;

    param_t param_type;

    const jbyte* utf8_name = (*env)->GetStringUTFChars(env, name, NULL);
    char* utf8_copy = (char*)malloc(sizeof(char)*(strlen(utf8_name)+1));

    if(utf8_name != NULL) {

        strcpy(utf8_copy,utf8_name);

        switch(type) {

            case 1:
                param_type = INT_TYPE;
                break;
            case 2:
                param_type = REAL_TYPE;
                break;
            case 3:
                param_type = BOOLEAN_TYPE;
                break;
            case 4:
                param_type = STRING_TYPE;
                break;
            case 5:
                param_type = LIST_TYPE;
                break;
            default: 
                (*env)->ReleaseStringUTFChars(env, name, utf8_name);
                return;
        };

        PM_manage(pm, utf8_copy, param_type, required);              
        (*env)->ReleaseStringUTFChars(env, name, utf8_name);
        free(utf8_copy);
    }
}


/*
Function J_parseFrom(file, first_pass) : parses a file, searching for registered parameters. If first_pass is true, the file is opened for reading. If it's false, the file is closed (two passes on the file are required to parse it)

pre: file is the name of an existing .config file
post: returns 1 if parsing is successful, returns 0 if parsing fails.
*/
JNIEXPORT jint JNICALL Java_ParameterInterface_J_1parseFrom(JNIEnv* env, jobject obj, jstring file, jboolean first_pass) {

    const jbyte* utf8_file = (*env)->GetStringUTFChars(env, file, NULL);
    char* pfile;
    int parse_result = 0;

    if(utf8_file != NULL) {

        pfile = (char*)malloc(sizeof(char)*(strlen(utf8_file)+1));
        strcpy(pfile,utf8_file);
        
        if(first_pass == true)
            fp = fopen(pfile,"r");
        
        if(fp == (FILE*)NULL) {
            return 0;
        }

        parse_result = PM_parseFrom(pm,fp,'#');

        if(parse_result == 0) {
            return 0;     
        }
       
        if(first_pass == false)
            fclose(fp);

        free(pfile);
        (*env)->ReleaseStringUTFChars(env, file, utf8_file);        
    }
    else 
        return 0;
    
    return 1;
}

/*
Function J_hasValue(name) : checks whether or not the name of a parameter has a filled value
pre: n/a
post: returns true if name is mapped to a value, false if it is not
*/
JNIEXPORT jboolean JNICALL Java_ParameterInterface_J_1hasValue(JNIEnv* env, jobject obj, jstring name) {

    jboolean has_value = false;
    const jbyte* utf8_name = (*env)->GetStringUTFChars(env, name, NULL);
    char* pname;

    if(utf8_name != NULL) {

        pname = (char*)malloc(sizeof(char)*(strlen(utf8_name)+1));
        strcpy(pname,utf8_name);

        has_value = PM_hasValue(pm,pname);
        free(pname);
        (*env)->ReleaseStringUTFChars(env, name, utf8_name);
    }
    
    return has_value;
}

/*
Function J_getString(name) : returns the string value mapped to name
pre: name is the name of a parameter containing a string type
post: returns the value related to name
*/
JNIEXPORT jstring JNICALL Java_ParameterInterface_J_1getString(JNIEnv* env, jobject obj, jstring name) {

    const jbyte* utf8_name;
    char* pname;
    char* getstr;

    utf8_name = (*env)->GetStringUTFChars(env, name, NULL);
    if(utf8_name != NULL) {

        pname = (char*)malloc(sizeof(char)*(strlen(utf8_name)+1));
        if(pname == (char*)NULL) {
            (*env)->ReleaseStringUTFChars(env, name, utf8_name);
            return NULL;
        }
          
        strcpy(pname,utf8_name);

        getstr = PM_getValue(pm,pname).str_val;
         
        (*env)->ReleaseStringUTFChars(env, name, utf8_name);
        free(pname);
    }

    jstring getjstr = (*env)->NewStringUTF(env,getstr);
    return getjstr;
}

/*
Function J_getInt(name) : returns the integer value mapped to name
pre: name is the name of a parameter containing an integer type
post: returns the value related to name
*/
JNIEXPORT jint JNICALL Java_ParameterInterface_J_1getInt(JNIEnv* env, jobject obj, jstring name) {

    const jbyte* utf8_name;
    char* pname;
    int getint;

    utf8_name = (*env)->GetStringUTFChars(env, name, NULL);
    if(utf8_name != NULL) {

        pname = (char*)malloc(sizeof(char)*(strlen(utf8_name)+1));
        strcpy(pname,utf8_name);

        getint = PM_getValue(pm,pname).int_val;

        (*env)->ReleaseStringUTFChars(env, name, utf8_name);
        free(pname);
    }

    return 0;
}

/*
Function J_getReal(name) : returns the float value mapped to name
pre: name is the name of a parameter containing a float type
post: returns the value related to name
*/
JNIEXPORT jfloat JNICALL Java_ParameterInterface_J_1getReal(JNIEnv* env, jobject obj, jstring name) {

    const jbyte* utf8_name;
    char* pname;
    int getfloat;

    utf8_name = (*env)->GetStringUTFChars(env, name, NULL);
    if(utf8_name != NULL) {

        pname = (char*)malloc(sizeof(char)*(strlen(utf8_name)+1));
        strcpy(pname,utf8_name);

        getfloat = PM_getValue(pm,pname).real_val;

        (*env)->ReleaseStringUTFChars(env, name, utf8_name);
        free(pname);
    }

    return getfloat;
}

/*
Function J_getBoolean(name) : returns the bool value mapped to name
pre: name is the name of a parameter containing a boolean type
post: returns the value related to name
*/
JNIEXPORT jboolean JNICALL Java_ParameterInterface_J_1getBoolean(JNIEnv* env, jobject obj, jstring name) {

    const jbyte* utf8_name;
    char* pname;
    int getbool;

    utf8_name = (*env)->GetStringUTFChars(env, name, NULL);
    if(utf8_name != NULL) {

        pname = (char*)malloc(sizeof(char)*(strlen(utf8_name)+1));
        strcpy(pname,utf8_name);

        getbool = PM_getValue(pm,pname).int_val;

        (*env)->ReleaseStringUTFChars(env, name, utf8_name);
        free(pname);
    }

    return getbool;

}

/*
Function J_getList(name) : returns the list structure mapped to name
pre: name is the name of a parameter containing a list type
post: returns the value related to name
*/
JNIEXPORT jobjectArray JNICALL Java_ParameterInterface_J_1getList(JNIEnv* env, jobject obj, jstring name) {

    const jbyte* utf8_name;
    char* pname;
 
    jobjectArray jlist = NULL;
    ParameterList* getlist = NULL;
    char* listval;
    int counter = 0;

    utf8_name = (*env)->GetStringUTFChars(env, name, NULL);
    if(utf8_name != NULL) {

        pname = (char*)malloc(sizeof(char)*(strlen(utf8_name)+1));
        strcpy(pname,utf8_name);
             
        getlist = PM_getValue(pm,pname).list_val;
        int len = PL_length(getlist);
        
        jlist = (jobjectArray) (*env)->NewObjectArray(env, len, (*env)->FindClass(env, "java/lang/String"), NULL);

        while(counter < len) {
            
            listval = PL_next(getlist);
            if(listval != NULL) {            

                (*env)->SetObjectArrayElement(env, jlist, counter, (*env)->NewStringUTF(env, listval));
                counter++;
            }
        }

        (*env)->ReleaseStringUTFChars(env, name, utf8_name);
        free(pname);
    }

    return jlist;
}
