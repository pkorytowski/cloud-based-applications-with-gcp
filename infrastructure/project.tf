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

resource "google_project_service" "run_api" {
  service = "run.googleapis.com"

  disable_on_destroy = true
}

resource "google_cloud_run_service" "run_service" {
  name = "uploader"
  location = "eu-central1"

  template {
    spec {
      containers {
        image = "gcr.io/google-samples/hello-app:1.0"
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  # Waits for the Cloud Run API to be enabled
  depends_on = [google_project_service.run_api]
}

resource "google_cloud_run_service_iam_member" "run_all_users" {
  service  = google_cloud_run_service.run_service.name
  location = google_cloud_run_service.run_service.location
  role     = "roles/run.invoker"
  member   = "allUsers"
}