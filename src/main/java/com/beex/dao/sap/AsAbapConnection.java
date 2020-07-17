package com.beex.dao.sap;

import java.util.Properties;

import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class AsAbapConnection {
    public enum AS_ABAP_DESTINATION {
        DEMO
    }

    private static JCoDestination dest;

    public static JCoDestination getDestination(String host, String sysnr, String client, String user, String password)
            throws JCoException {
        if (dest != null) {
            return dest;
        } else {
            InMemoryDestinationDataProvider provider = new InMemoryDestinationDataProvider();
            Environment.registerDestinationDataProvider(provider);

            Properties props = new Properties();
            props.put(DestinationDataProvider.JCO_ASHOST, host);
            props.put(DestinationDataProvider.JCO_SYSNR, sysnr);
            props.put(DestinationDataProvider.JCO_CLIENT, client);
            props.put(DestinationDataProvider.JCO_USER, user);
            props.put(DestinationDataProvider.JCO_PASSWD, password);
            provider.addProperties("", props);
            dest = JCoDestinationManager.getDestination("");
            return dest;
        }

    }

}