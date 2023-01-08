variable "project_id" {
    type = string
    default = "onyx-incentive-370823"
}

variable "bucket_name" {
    type = string
    default = "agh-gcp-project-2022"
}

variable "bucket_location" {
    type = string
    default = "US"
}

variable "pubsub_topic_name" {
    type = string 
    default = "uploader-topic"
}

variable "pubsub_push_subscription" {
    type = string
    default = "uploader-push-subscription"
}

variable "cf_source_dir" {
    type = string
    default = "../cf"
}

variable "cloud_run_name" {
    type = string 
    default = "uploader"
}

variable "docker_image" {
    type = string
    default = "us.gcr.io/onyx-incentive-370823/uploader"
}
