package com.ioter.medical.common.util;

import android.os.Environment;

import java.io.File;

public class VariableConstant
{

    public static final String APP_PACKAGE_MAIN = "com.ioter.medical";
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + "ioter" + File.separator;
}