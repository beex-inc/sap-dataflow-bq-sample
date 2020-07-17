
#!/bin/bash -ue
cd $(dirname $0)/../terraform

bq rm sap_dataset_sample.company_code
terraform init
terraform destroy