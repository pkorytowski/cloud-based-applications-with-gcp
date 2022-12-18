provider "google" {
  # Replace `PROJECT_ID` with your project
  project = "onyx-incentive-370823"
}

resource "google_storage_bucket" "static" {
  name          = "bucket-agh-gcp-project-2022"
  location      = "EU"
  force_destroy = true

  uniform_bucket_level_access = true
  public_access_prevention = "enforced"

}
