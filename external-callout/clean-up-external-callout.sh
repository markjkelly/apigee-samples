#!/bin/bash

# Copyright 2025 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#echo "Enabling APIs..."
#gcloud services enable cloudbuild.googleapis.com

if [ -z "$PROJECT" ]; then
  echo "No PROJECT variable set"
  exit
fi

if [ -z "$APIGEE_ENV" ]; then
  echo "No APIGEE_ENV variable set"
  exit
fi

if [ -z "$APIGEE_HOST" ]; then
  echo "No APIGEE_HOST variable set"
  exit
fi

TOKEN=$(gcloud auth print-access-token)

echo "Installing apigeecli"
curl -s https://raw.githubusercontent.com/apigee/apigeecli/main/downloadLatest.sh | bash
export PATH=$PATH:$HOME/.apigeecli/bin

echo "Undeploying external-callout"
REV=$(apigeecli envs deployments get --env "$APIGEE_ENV" --org "$PROJECT" --token "$TOKEN" --disable-check | jq .'deployments[]| select(.apiProxy=="external-callout").revision' -r)
apigeecli apis undeploy --name external-callout --env "$APIGEE_ENV" --rev "$REV" --org "$PROJECT" --token "$TOKEN"

echo "Deleting proxy external-callout"
apigeecli apis delete --name external-callout --org "$PROJECT" --token "$TOKEN"

echo "Deleting target server"
apigeecli targetservers delete --name "${GRPC_TARGET_SERVER_NAME}" --org "$PROJECT" --token "$TOKEN"


