#!/bin/sh
set -eu

cluster=${1:-jenkins-practice}
context="kind-${cluster}"
namespace=jenkins-practice
log_file=$(mktemp)
kubectl --context "$context" -n "$namespace" port-forward --address 127.0.0.1 service/jenkins-java-practice 18080:80 > "$log_file" 2>&1 &
forward_pid=$!
trap 'kill "$forward_pid" 2>/dev/null || true; wait "$forward_pid" 2>/dev/null || true; rm -f "$log_file"' EXIT

for attempt in 1 2 3 4 5 6 7 8 9 10; do
    if response=$(curl --fail --silent --show-error --max-time 2 http://127.0.0.1:18080/health 2>/dev/null); then
        if [ "$response" = '{"status":"ok"}' ]; then
            printf '%s\n' 'Deployment health check passed'
            exit 0
        fi
    fi
    sleep 1
done

cat "$log_file" >&2
printf '%s\n' 'Deployment health check failed' >&2
exit 1
