# Cloud Run gRPC Server

## Set up the gRPC server in Cloud Run

1. Clone this repo.

   ```
   https://github.com/GoogleCloudPlatform/apigee-samples.git
   ```

1. Change to the External Callout app directory.

   ```
   cd apigee-samples/external-callout/app
   ```

1. Upload the server code to Cloud Run.
    1. Enable Cloud Build API on GCP project
    1. Enable permissions. In GCP, go to "IAM & Admin &gt; IAM" and add the
       following permissions to $GCP_PROJECT_ID@cloudbuild.gserviceaccount.com:
        1. Cloud Build Service Agent: permission to build on Cloud Build
        1. Storage Object Admin: permission to modify storage objects
        1. Cloud Run Admin: permission required for Cloud Run (see
           [here](https://cloud.google.com/build/docs/deploying-builds/deploy-cloud-run#before_you_begin))
        1. Service Account User: permission required for Cloud Run (see
           [here](https://cloud.google.com/build/docs/deploying-builds/deploy-cloud-run#before_you_begin))
    1. Wait for permissions to propagate.
    1. Check the current project configuration. is using the appropriate
       project.
       ```
       gcloud config list
       ```
       "project" should list the desired project. If it is not correct, run the
       following:
       ```
       gcloud config set project $PROJECT
       ```
    1. Submit the code to Cloud Run. This may take up to a few minutes.
       ```
       gcloud builds submit
       ```
       Once this step is finished, you should see the `gcloud` command end in
       "SUCCESS".

You've successfully set up a gRPC server on Cloud Run!
