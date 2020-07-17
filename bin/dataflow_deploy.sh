#!/bin/bash

# 変更が必要
PROJECT_NAME=YOUR_PROJECT
BUCKET_NAME=YOUR_BUCKET

# チュートリアルでは以下の設定は変更不要
SUBNET=regions/asia-northeast1/subnetworks/sapbq-dataflow-subnetwork
BQ_DATASET=sap_dataset_sample
BQ_TABLENAME=company_code



ARGS="--runner=DataflowRunner"
ARGS="${ARGS} --project=${PROJECT_NAME}"
ARGS="${ARGS} --subnetwork=${SUBNET}"
ARGS="${ARGS} --baseBucket=${BUCKET_NAME}"
ARGS="${ARGS} --bqDataSet=${BQ_DATASET}"
ARGS="${ARGS} --bqTableName=${BQ_TABLENAME}"


mvn compile 
mvn dependency:build-classpath -Dmdep.outputFile=./target/cp.txt
java -cp ./target/classes/:$(cat ./target/cp.txt) com.beex.StarterPipeline ${ARGS}