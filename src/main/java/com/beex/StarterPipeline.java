package com.beex;

import com.beex.dao.dataflow.CompanyCodePipeline;
import com.beex.dao.dataflow.options.BeeXPipelineOptions;
import com.sap.conn.jco.JCoException;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

public class StarterPipeline {

  public static void setupOptions(BeeXPipelineOptions options) {
    // BaseBucketで指定されたバケットを使い回して利用
    options.setStagingLocation(String.format("gs://%s/staging", options.getBaseBucket()));
    options.setTempLocation(String.format("gs://%s/temp", options.getBaseBucket()));
    options.setTemplateLocation(String.format("gs://%s/template.json", options.getBaseBucket()));

    // Dataflow WorkerにはパブリックIPを付けない
    options.setUsePublicIps(false);
  }

  public static void main(String[] args) {
    BeeXPipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(BeeXPipelineOptions.class);
    setupOptions(options);

    try {
      // Dataflow Jobを実行
      CompanyCodePipeline.build(options).run();
    } catch (JCoException jex) {
      jex.printStackTrace();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
