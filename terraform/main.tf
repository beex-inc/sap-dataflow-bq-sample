variable "gcp_project" {}
variable "gcp_region" {
  default = "asia-northeast1"
}
variable "admin_ip_ranges" {
  type    = set(string)
  default = ["0.0.0.0/0"]
}
variable "project_prefix" {
  default = "sapbq"
}

provider "google" {
  version = "~> 3.3"
  project = var.gcp_project
  region  = var.gcp_region
}

resource "google_compute_network" "network" {
  name                    = "${var.project_prefix}-network"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "sap_subnetwork" {
  name                     = "${var.project_prefix}-sap-subnetwork"
  ip_cidr_range            = "10.1.0.0/16"
  region                   = var.gcp_region
  network                  = google_compute_network.network.self_link
  private_ip_google_access = true
}

resource "google_compute_subnetwork" "dataflow_subnetwork" {
  name                     = "${var.project_prefix}-dataflow-subnetwork"
  ip_cidr_range            = "10.2.0.0/16"
  region                   = var.gcp_region
  network                  = google_compute_network.network.self_link
  private_ip_google_access = true
}

resource "google_compute_firewall" "firewall_allow_iap_ssh" {
  name    = "allow-${var.project_prefix}-iap-ssh"
  network = google_compute_network.network.name

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }
  source_ranges = var.admin_ip_ranges

  depends_on = [
    google_compute_network.network
  ]
}

resource "google_compute_firewall" "firewall_allow_dataflow_internal" {
  name    = "allow-${var.project_prefix}-dataflow-internal"
  network = google_compute_network.network.name

  allow {
    protocol = "tcp"
    ports    = ["12345", "12346"]
  }

  source_tags = ["dataflow"]
  target_tags = ["dataflow"]
  depends_on = [
    google_compute_network.network
  ]
}

###################################
# BQ DATASET for BAPI_COMPANYCODE_GETLIST
###################################
resource "google_bigquery_dataset" "bq_dataset_sample" {
  dataset_id    = "sap_dataset_sample"
  friendly_name = "SAP Dataset Sample"
  description   = "SAP Dataset Sample"
  location      = var.gcp_region
}

