pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        timeout(time: 10, unit: 'MINUTES')
        timestamps()
    }

    environment {
        IMAGE_NAME = 'jenkins-practice-app'
    }

    stages {
        stage('Test in Docker agent') {
            agent {
                docker {
                    image 'python:3.12-alpine'
                    reuseNode true
                }
            }
            steps {
                sh 'python -m unittest discover -s tests -v'
            }
        }

        stage('Build image') {
            steps {
                sh 'docker build -t "$IMAGE_NAME:$BUILD_NUMBER" .'
            }
        }

        stage('Smoke test') {
            steps {
                sh '''
                    set -eu
                    container_id=$(docker run -d --rm "$IMAGE_NAME:$BUILD_NUMBER")
                    trap 'docker stop "$container_id" >/dev/null' EXIT

                    for attempt in 1 2 3 4 5; do
                        if docker exec "$container_id" python -c 'import json, urllib.request; data = json.load(urllib.request.urlopen("http://127.0.0.1:8080/health")); assert data == {"status": "ok"}'; then
                            exit 0
                        fi
                        sleep 1
                    done
                    exit 1
                '''
            }
        }
    }
}
