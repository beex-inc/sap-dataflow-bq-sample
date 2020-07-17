package com.beex.dao.dataflow.options;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.options.Validation.Required;

public interface SapJcoOptions extends DataflowPipelineOptions {
    @Description("SAP ASHOST")
    @Required
    ValueProvider<String> getSapAsHost();

    void setSapAsHost(ValueProvider<String> value);

    @Description("SAP SYSNR")
    @Required
    ValueProvider<String> getSapSysNR();

    void setSapSysNR(ValueProvider<String> value);

    @Description("SAP CLIENT")
    @Required
    ValueProvider<String> getSapClient();

    void setSapClient(ValueProvider<String> value);

    @Description("SAP USER")
    @Required
    ValueProvider<String> getSapUser();

    void setSapUser(ValueProvider<String> value);

    @Description("SAP PASSWORD")
    @Required
    ValueProvider<String> getSapPassword();

    void setSapPassword(ValueProvider<String> value);

}