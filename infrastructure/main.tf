terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = ">= 4.34.0"
    }
  }
}

resource "google_storage_bucket" "static"{
  name          = var.bucket_name
  location      = var.bucket_location
  force_destroy = true

  uniform_bucket_level_access = true
  public_access_prevention = "enforced"
}

resource "google_pubsub_topic" "topic" {
  name = var.pubsub_topic_name
}

resource "google_pubsub_subscription" "subscription" {
    name = var.pubsub_push_subscription
    topic = google_pubsub_topic.topic.name
    ack_deadline_seconds = 20
}

resource "google_storage_bucket" "function_bucket" {
    name     = "agh-gcp-project-2022-function"
    location = var.bucket_location
}

data "archive_file" "source" {
    type        = "zip"
    source_dir  = var.cf_source_dir
    output_path = "/tmp/function.zip"
}

resource "google_storage_bucket_object" "zip" {
    source       = data.archive_file.source.output_path
    content_type = "application/zip"

    name         = "src-${data.archive_file.source.output_md5}.zip"
    bucket       = google_storage_bucket.function_bucket.name

    depends_on   = [
        google_storage_bucket.function_bucket,  # declared in `storage.tf`
        data.archive_file.source
    ]
}

resource "google_cloudfunctions_function" "notifier-function" {
  name     = "notifier_function"
  runtime  = "nodejs12"
  region = "us-central1"
  event_trigger {
    event_type = "google.pubsub.topic.publish"
    resource   = google_pubsub_topic.topic.name
  }

  source_archive_bucket = google_storage_bucket.function_bucket.name
  source_archive_object = google_storage_bucket_object.zip.name

    environment_variables = {
    "USER" = data.google_secret_manager_secret_version.notifier-user.secret_data
    "PASSWORD" = data.google_secret_manager_secret_version.notifier-password.secret_data
  }

  entry_point           = "bq_alerts"
  timeout               = 60
  depends_on   = [
    google_storage_bucket_object.zip,
    google_pubsub_topic.topic
  ]
}

resource "google_sql_database_instance" "uploader" {
  name = "uploader-sql-instance"
  region = "us-central1"
  database_version = "POSTGRES_11"
  settings {
    tier = "db-f1-micro"
  }
}

resource "google_sql_user" "user" {
  name     = data.google_secret_manager_secret_version.sql-user.secret_data
  instance = google_sql_database_instance.uploader.name
  password =  data.google_secret_manager_secret_version.sql-password.secret_data
}

resource "google_sql_database" "uploader-db" {
  name   = "uploader-db"
  instance = google_sql_database_instance.uploader.name
  charset = "utf8"
}

resource "google_cloud_run_service" "api" {
  name     = var.cloud_run_name
  location = "us-central1"

  template {
    spec {
      containers {
        image = var.docker_image
        env {
          name  = "GOOGLE_CLOUD_PROJECT"
          value = var.project_id
        }
        env {
          name  = "DB_USER"
          value = data.google_secret_manager_secret_version.sql-user.secret_data
        }
        env {
          name  = "DB_PASSWORD"
          value = data.google_secret_manager_secret_version.sql-password.secret_data
        }
        env {
            name = "GOOGLE_CLOUD_BUCKET"
            value = var.bucket_name
        }
        env {
            name = "TOPIC_NAME"
            value = var.pubsub_topic_name
        }
      }
    }
  }
  autogenerate_revision_name = true

  traffic {
    percent         = 100
    latest_revision = true
  }
}

data "google_iam_policy" "noauth" {
  binding {
    role    = "roles/run.invoker"
    members = [
      "allUsers",
    ]
  }
}

resource "google_cloud_run_service_iam_policy" "noauth" {
  location = google_cloud_run_service.api.location
  project  = google_cloud_run_service.api.project
  service  = google_cloud_run_service.api.name

  policy_data = data.google_iam_policy.noauth.policy_data
}

