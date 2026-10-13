# Java Jenkins 파이프라인과 로컬 Kubernetes 배포

Java 21 웹 서버를 Maven으로 테스트하고 JAR로 묶은 뒤 Jenkins에서 Docker 이미지를 만들어 로컬 `kind` 클러스터에 배포하는 연습입니다. `/health`가 `{"status":"ok"}`를 반환하면 배포 확인을 마칩니다.

## 로컬에서 실행하기

JDK 21, Maven, Docker, `kind`, `kubectl`, `curl`이 필요합니다. 아래 명령은 저장소 루트에서 실행합니다.

```bash
kind create cluster --name jenkins-practice
mvn -f java-app/pom.xml clean verify
docker build -t jenkins-java-practice:local java-app
sh java-app/scripts/deploy.sh jenkins-java-practice:local
sh java-app/scripts/verify.sh
```

배포 스크립트는 이미지를 `kind` 노드에 넣고 Namespace, Service, Deployment를 적용한 뒤 롤아웃 완료를 기다립니다. 확인 스크립트는 Service를 포트 포워딩해 실제 HTTP 응답을 검사합니다. 이미지는 외부 레지스트리에 올리지 않습니다.

## Jenkins에서 실행하기

Jenkins 실행 노드에 Docker 명령과 `kind`, `kubectl`, `curl`이 있어야 합니다. Docker는 `kind` 클러스터를 만든 엔진과 연결되어야 합니다. Jenkins가 컨테이너에서 실행된다면 그 컨테이너를 Docker의 `kind` 네트워크에 연결하고 다음 명령으로 만든 내부 접속용 kubeconfig를 Jenkins의 **Secret file** 자격 증명으로 등록합니다. ID는 `practice-kind-kubeconfig`로 지정합니다.

```bash
docker network connect kind <jenkins-container>
kind get kubeconfig --internal --name jenkins-practice > /path/outside-repository/kubeconfig
```

Kubeconfig에는 클러스터 접속 정보가 들어 있으므로 저장소에 추가하지 않습니다. Jenkins에서 **Pipeline script from SCM**을 선택하고 **Script Path**를 `java-app/Jenkinsfile`로 설정합니다.

파이프라인은 Maven 테스트 및 패키징 → Docker 이미지 빌드 → `kind` 배포 → Service 응답 확인 순서로 실행됩니다. Jenkins 작업 이름과 빌드 번호를 이미지 태그에 넣으며 배포 대상은 로컬 `kind-jenkins-practice` 컨텍스트의 `jenkins-practice` 네임스페이스입니다.
