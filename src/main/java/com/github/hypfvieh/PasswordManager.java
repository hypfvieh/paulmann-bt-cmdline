package com.github.hypfvieh;

import java.util.Properties;

import com.github.hypfvieh.util.FileIoUtil;

public class PasswordManager {
    private static final PasswordManager INSTANCE = new PasswordManager();
    
    private final Properties macToPassword;
    private String defaultPassword;
    
    private PasswordManager() {
        macToPassword = FileIoUtil.readProperties(getClass().getClassLoader().getResourceAsStream("devicePasswords.properties"));
    }
    
    public static PasswordManager getInstance() {
        return INSTANCE;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String _defaultPassword) {
        defaultPassword = _defaultPassword;
        FileIoUtil.writeProperties("devicePasswords.properties", macToPassword);
    }
    
    public void putDevicePassword(String _mac, String _password) {
        macToPassword.setProperty(_mac, _password);
        FileIoUtil.writeProperties("devicePasswords.properties", macToPassword);
    }

    public String getDevicePassword(String _mac) {
        return macToPassword.getProperty(_mac, defaultPassword);
    }
    
}
