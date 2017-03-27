package com.ecfront.dew.appexample;

import java.io.Serializable;

public class MQDTO implements Serializable {

    private String a;
    private int b;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
