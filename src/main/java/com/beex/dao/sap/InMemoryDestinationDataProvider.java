package com.beex.dao.sap;

import java.util.HashMap;
import java.util.Properties;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class InMemoryDestinationDataProvider implements DestinationDataProvider {
    private DestinationDataEventListener el;
    private HashMap<String, Properties> destinationProperties = new HashMap<String, Properties>();

    @Override
    public Properties getDestinationProperties(String destName) throws DataProviderException {
        try {
            Properties p = this.destinationProperties.get(destName);
            if (p != null && p.isEmpty())
                throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION,
                        "destination configuration is incorrect", null);

            return p;

        } catch (RuntimeException re) {
            throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, re);
        }
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
        this.el = eventListener;
    }

    @Override
    public boolean supportsEvents() {
        return true;
    }

    public void addProperties(String destName, Properties properties) {
        synchronized (destinationProperties) {
            this.destinationProperties.put(destName, properties);
            this.el.updated(destName);
        }
    }

}