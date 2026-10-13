#!/bin/sh
set -eu

image=${1:?usage: deploy.sh IMAGE [KIND_CLUSTER]}
cluster=${2:-jenkins-practice}
context="kind-${cluster}"
namespace=jenkins-practice

kind load docker-image "$image" --name "$cluster"
kubectl --context "$context" apply -f java-app/k8s/namespace.yaml
kubectl --context "$context" apply -f java-app/k8s/service.yaml
sed "s|jenkins-java-practice:demo|${image}|" java-app/k8s/deployment.yaml | kubectl --context "$context" apply -f -
kubectl --context "$context" -n "$namespace" rollout status deployment/jenkins-java-practice --timeout=120s
kubectl --context "$context" -n "$namespace" get deployment,service,pods
