package com.beex.dao.dataflow.options;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Validation.Required;

public interface BeeXPipelineOptions extends SapJcoOptions {
    @Description("Base Bucket")
    @Required
    String getBaseBucket();

    void setBaseBucket(String value);

    @Description("BigQuery Dataset")
    @Required
    String getBqDataSet();

    void setBqDataSet(String value);

    @Description("BigQuery Table Name")
    @Required
    String getBqTableName();

    void setBqTableName(String value);

}