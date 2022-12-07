# cloud-based-applications-with-gcp

## Description

The aim of the project is to create app for storing images in bucket. 
After each upload cloud function will send an email notification to user.

## Architecture

![Architecture](data/gcp-arch.png?raw=true)

## Deployment

Deployment tool: Terraform

## Used GCP Services

- Cloud Run (Spring App)
- Pub/Sub (add file events)
- Cloud Function (Email notifier)
- Cloud Storage