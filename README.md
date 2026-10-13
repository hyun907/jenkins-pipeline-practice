# Jenkins 파이프라인 연습

작은 Python 웹 서버를 Jenkins로 테스트하고 Docker 이미지를 빌드하는 개인 연습용 저장소입니다. 데이터베이스나 외부 계정 없이 로컬에서 실행할 수 있습니다.

## 로컬에서 확인하기

Python 3.12와 Docker가 필요합니다.

```bash
python3 -m unittest discover -s tests -v
docker build -t jenkins-practice-app:local .
docker run --rm -p 127.0.0.1:8080:8080 jenkins-practice-app:local
```

다른 터미널에서 `curl http://127.0.0.1:8080/health`를 실행하면 `{"status": "ok"}`가 나옵니다.

## Jenkins에서 실행하기

Jenkins 실행 환경에서 Docker 명령을 사용할 수 있어야 하며, Docker Pipeline 플러그인이 필요합니다.

1. **New Item → Pipeline**으로 작업을 만듭니다.
2. **Pipeline script from SCM**을 선택하고 Git 저장소 주소를 입력합니다.
3. **Script Path**를 `Jenkinsfile`로 두고 **Build Now**를 누릅니다.

파이프라인은 Docker agent에서 HTTP 테스트를 실행하고 이미지를 만든 뒤 컨테이너의 `/health` 응답을 확인합니다. 이미지는 로컬 Docker 엔진에만 남으며 외부 레지스트리로 전송하지 않습니다.

이 저장소의 예제 코드는 새로 작성했으며 [Unlicense](UNLICENSE)에 따라 공개합니다.
