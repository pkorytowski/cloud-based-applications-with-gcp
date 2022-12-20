terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = ">= 4.34.0"
    }
  }
}

resource "google_storage_bucket" "static"{
  name          = "agh-gcp-project-2022"
  location      = "US"
  force_destroy = true

  uniform_bucket_level_access = true
  public_access_prevention = "enforced"
}

resource "google_pubsub_topic" "topic" {
  name = "uploader-topic"
}

resource "google_pubsub_subscription" "subscription" {
    name = "uploader-push-subscription"
    topic = google_pubsub_topic.topic.name

    ack_deadline_seconds = 20

}

resource "google_storage_bucket" "function_bucket" {
    name     = "agh-gcp-project-2022-function"
    location = "US"
}

data "archive_file" "source" {
    type        = "zip"
    source_dir  = "../cf"
    output_path = "/tmp/function.zip"
}

# Add source code zip to the Cloud Function's bucket
resource "google_storage_bucket_object" "zip" {
    source       = data.archive_file.source.output_path
    content_type = "application/zip"

    # Append to the MD5 checksum of the files's content
    # to force the zip to be updated as soon as a change occurs
    name         = "src-${data.archive_file.source.output_md5}.zip"
    bucket       = google_storage_bucket.function_bucket.name

    # Dependencies are automatically inferred so these lines can be deleted
    depends_on   = [
        google_storage_bucket.function_bucket,  # declared in `storage.tf`
        data.archive_file.source
    ]
}

# Create the cloud function
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
  name     = "user"
  instance = google_sql_database_instance.uploader.name
  password = "abc"
}

resource "google_sql_database" "uploader-db" {
  name   = "uploader-db"
  instance = google_sql_database_instance.uploader.name
  charset = "utf8"
}

