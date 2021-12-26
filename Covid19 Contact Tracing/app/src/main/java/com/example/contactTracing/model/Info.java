package com.example.contactTracing.model;

public class Info {
    private String f_name, l_name, ml;

    public Info() {
    }

    public Info(String f_name, String l_name, String ml) {
        this.f_name = f_name;
        this.l_name = l_name;
        this.ml = ml;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public String getMl() {
        return ml;
    }

    public void setMl(String ml) {
        this.ml = ml;
    }
}
