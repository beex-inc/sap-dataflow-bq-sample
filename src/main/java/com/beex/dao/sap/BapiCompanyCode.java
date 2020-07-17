package com.beex.dao.sap;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import java.util.ArrayList;
import java.util.List;

import com.beex.model.CompanyCode;

public class BapiCompanyCode {
    String moduleName = "BAPI_COMPANYCODE_GETLIST";
    JCoFunction function;
    JCoDestination destination;

    public BapiCompanyCode(JCoDestination dest) throws JCoException {
        this.destination = dest;
        this.function = this.destination.getRepository().getFunction(this.moduleName);
        if (this.function == null)
            throw new RuntimeException(String.format("%s not found in SAP.", this.moduleName));
    }

    public List<CompanyCode> getList() throws JCoException {

        // 汎用モジュールの実行とCompany Code取得
        this.function.execute(this.destination);
        JCoStructure retStruct = this.function.getExportParameterList().getStructure("RETURN");
        if (!(retStruct.getString("TYPE").equals("") || retStruct.getString("TYPE").equals("S")))
            throw new RuntimeException(retStruct.getString("MESSAGE"));
        JCoTable codes = this.function.getTableParameterList().getTable("COMPANYCODE_LIST");

        // JCoTableを配列に変換
        List<CompanyCode> clist = new ArrayList<CompanyCode>();
        codes.firstRow();
        do {
            clist.add(new CompanyCode(codes.getString("COMP_CODE"), codes.getString("COMP_NAME")));
        } while (codes.nextRow());

        return clist;
    }
}