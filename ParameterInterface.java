// Alex Schwarz
// SID: 0719732
// CIS*2750 A2
// ParameterInterface.java


//"Connector" between C library and java functions. 
//Performs necessary type conversions between the two languages
//Any reference to the A1 library from java, this class should be used
public class ParameterInterface {

    //our .so file
    static { System.loadLibrary("JNIpm"); }

    //class constructor
    //pre: N/a
    //post: ParameterManager is initialized
    public ParameterInterface() {
        J_create();
    }

    public native void J_create();
    public native void J_destroy();
    public native void J_manage(String name, int type, boolean required);
    public native int J_parseFrom(String file, boolean first_pass);

    public native boolean J_hasValue(String name);

    public native String J_getString(String name);
    public native int J_getInt(String name);
    public native float J_getReal(String name);
    public native boolean J_getBoolean(String name);
    public native String[] J_getList(String name);
   
};
