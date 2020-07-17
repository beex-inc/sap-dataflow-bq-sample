package com.beex.model;

import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

@DefaultCoder(AvroCoder.class)
public class CompanyCode {
    final public String code;
    final public String name;

    public CompanyCode() {
        this.code = "";
        this.name = "";
    }

    public CompanyCode(String code, String name) {
        this.code = code;
        this.name = name;
    }
}