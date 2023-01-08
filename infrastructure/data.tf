data "google_secret_manager_secret_version" "notifier-user" {
  secret = "NOTIFIER-FUNCTION-USER"
}

data "google_secret_manager_secret_version" "notifier-password" {
  secret = "NOTIFIER-FUNCTION-PASSWORD"
  
}

data "google_secret_manager_secret_version" "sql-user" {
  secret = "SQL-USER"
}

data "google_secret_manager_secret_version" "sql-password" {
  secret = "SQL-PASSWORD"
}