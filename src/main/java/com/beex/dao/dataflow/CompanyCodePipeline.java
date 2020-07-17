package com.beex.dao.dataflow;

import java.util.Arrays;
import java.util.List;

import com.beex.dao.dataflow.options.BeeXPipelineOptions;
import com.beex.dao.dataflow.utils.PipelineFileUtils;
import com.beex.dao.sap.AsAbapConnection;
import com.beex.dao.sap.BapiCompanyCode;
import com.beex.model.CompanyCode;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.common.collect.ImmutableList;
import com.sap.conn.jco.JCoException;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.options.ValueProvider;

public class CompanyCodePipeline {
    @SuppressWarnings("serial")
    public static class CompanyCodeReadDoFn extends DoFn<String, CompanyCode> {
        private final String WORKER_LOCLA_LIBPATH = "/usr/lib";
        private final String SAPJCO_LIB_FILENAME = "libsapjco3.so";
        private ValueProvider<String> host;
        private ValueProvider<String> sysnr;
        private ValueProvider<String> client;
        private ValueProvider<String> user;
        private ValueProvider<String> password;
        private String stagingLocation;

        public CompanyCodeReadDoFn(ValueProvider<String> host, ValueProvider<String> sysnr,
                ValueProvider<String> client, ValueProvider<String> user, ValueProvider<String> password,
                String stagingLocation) {
            this.host = host;
            this.sysnr = sysnr;
            this.client = client;
            this.user = user;
            this.password = password;
            this.stagingLocation = stagingLocation;
        }

        @Setup
        public void setUp() throws Exception {
            // Stagingにアップロードしたlibsapjco3.soファイルをWorkerにダウンロードする
            PipelineFileUtils.setUpNativeLibrary(this.stagingLocation, WORKER_LOCLA_LIBPATH, SAPJCO_LIB_FILENAME);
        }

        @ProcessElement
        public void processElement(OutputReceiver<CompanyCode> out) throws JCoException {
            // BAPIでSAP ERPよりCompanyCodeを読み込み
            BapiCompanyCode companyCodeRepository = new BapiCompanyCode(AsAbapConnection.getDestination(this.host.get(),
                    this.sysnr.get(), this.client.get(), this.user.get(), this.password.get()));
            List<CompanyCode> codes = companyCodeRepository.getList();

            // BAPIから取得した形式をGenericRecord形式に変換
            codes.forEach(c -> {
                out.output(c);
            });
        }
    }

    public static Pipeline build(BeeXPipelineOptions options) throws JCoException {
        Pipeline p = Pipeline.create(options);

        // BigQueryの書き込み先テーブルをオプションから指定
        TableReference bqTableSpec = new TableReference().setProjectId(options.getProject())
                .setDatasetId(options.getBqDataSet()).setTableId(options.getBqTableName());

        // BigQueryのスキーマを指定
        TableSchema bqTableSchema = new TableSchema().setFields(
                ImmutableList.of(new TableFieldSchema().setName("code").setType("STRING").setMode("NULLABLE"),
                        new TableFieldSchema().setName("name").setType("STRING").setMode("NULLABLE")));

        // ダミーのスターターを作成
        PCollection<String> dummyStarter = p.apply("Start", Create.of(Arrays.asList("")));

        // ソースデータは2ステップ目で取得
        PCollection<CompanyCode> codes = dummyStarter.apply("Read SAP Data",
                ParDo.of(new CompanyCodeReadDoFn(options.getSapAsHost(), options.getSapSysNR(), options.getSapClient(),
                        options.getSapUser(), options.getSapPassword(), options.getStagingLocation())));

        // BigQueryに書き込み
        codes.apply("Write to BigQuery",
                BigQueryIO.<CompanyCode>write().to(bqTableSpec).withSchema(bqTableSchema)
                        .withFormatFunction(c -> new TableRow().set("code", c.code).set("name", c.name))
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));

        return p;
    }
}