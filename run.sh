#!/usr/bin/env bash
set -euo pipefail

ENV_NAME="${1:-dev}"          # accepts ./run.sh prod, defaults to dev
ENV_FILE=".env.${ENV_NAME}"

clear

if [ ! -f "$ENV_FILE" ]; then
  echo "Error: $ENV_FILE not found" >&2
  exit 1
fi

set -a
source <(grep -v '^#' "$ENV_FILE" | grep -v '^$')
set +a

./mvnw spring-boot:run
