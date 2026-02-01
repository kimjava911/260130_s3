# S3 연동 애플리케이션 배포 가이드

이 문서는 로컬 환경에서 Docker 이미지를 빌드하고, GitHub Container Registry(GHCR)에 푸시한 뒤, EC2 서버에 배포하는 전체 과정을 설명합니다.

## 1. Docker 이미지 빌드 (Local)

GitHub 사용자 이름을 환경 변수로 설정하고, OS에 맞는 명령어로 Docker 이미지를 빌드합니다.

```sh
# GitHub 사용자 이름 설정 (본인의 ID로 변경)
export GH_USER=<github username>
# 예시: export GH_USER=kimjava911

# Windows 사용자 (일반 빌드)
docker build -t ghcr.io/${GH_USER}/s3:latest .

# Mac 사용자 (Apple Silicon 등 멀티 아키텍처 지원 빌드)
# linux/amd64와 linux/arm64 플랫폼을 모두 지원하도록 빌드합니다.
docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/${GH_USER}/s3:latest .
```

## 2. Docker 이미지 푸시 (Local)

GitHub Container Registry에 이미지를 업로드하기 위해 로그인하고 푸시합니다.
*   GitHub Settings > Developer settings > Personal access tokens에서 `write:packages` 권한이 있는 토큰(Classic)이 필요합니다.

```sh
# GitHub Personal Access Token (PAT) 설정
# https://github.com/settings/tokens/new 에서 토큰 생성
export CR_TOKEN=<pat token>

# GHCR 로그인 (비밀번호 대신 토큰 사용)
echo $CR_TOKEN | docker login ghcr.io -u $GH_USER --password-stdin

# 이미지 푸시
docker push ghcr.io/${GH_USER}/s3:latest
```

## 3. EC2 서버 접속 (Local -> Server)

AWS EC2 인스턴스에 SSH로 접속합니다. `.pem` 키 파일이 있는 디렉토리에서 실행하세요.

```sh
# EC2 인스턴스의 퍼블릭 DNS 주소 설정
export PUBLIC_DNS=<public dns>

# 키 파일 권한 변경 (보안상 필수, 읽기 권한만 부여)
chmod 400 *.pem

# SSH 접속
ssh -i $(ls *.pem) ubuntu@${PUBLIC_DNS}
```

## 4. Docker 설치 (Server)

서버에 접속한 후 Docker를 설치하고 권한을 설정합니다.

```sh
# 패키지 목록 업데이트
sudo apt update

# Docker 설치 스크립트 다운로드 및 실행
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 현재 사용자(ubuntu)를 docker 그룹에 추가 (sudo 없이 docker 명령어 사용 위함)
sudo usermod -aG docker $USER

# 그룹 변경 사항 적용을 위해 로그아웃 후 재접속 필요 (또는 exit 후 다시 ssh 접속)
exit
```

## 5. 서버에서 Docker 로그인 (Server)

이미지를 받아오기 위해 서버에서도 GHCR에 로그인합니다.

```sh
# Docker 설치 확인
docker -v
docker compose version

# 환경 변수 재설정 (서버 세션이므로 다시 설정 필요)
export CR_TOKEN=<pat token>
export GH_USER=<github username>

# GHCR 로그인
echo $CR_TOKEN | docker login ghcr.io -u $GH_USER --password-stdin
```

## 6. 환경 변수 파일 준비 (Server)

애플리케이션 실행에 필요한 AWS 자격 증명 정보를 담을 `.env` 파일을 준비합니다.

```dotenv
AWS_BUCKET_NAME=your-bucket-name
AWS_REGION=ap-northeast-2
AWS_ACCESS_KEY=your-access-key
AWS_SECRET_KEY=your-secret-key
```

## 7. 애플리케이션 실행 (Server)

`.env` 파일을 생성하고 Docker 컨테이너를 실행합니다.

```sh
# .env 파일 생성 및 편집
# i 키를 눌러 입력 모드로 전환 -> 내용 붙여넣기 -> ESC -> :wq 입력하여 저장 및 종료
vi .env

# Docker 컨테이너 실행
# -d: 백그라운드 실행
# -p 80:8080: 호스트의 80 포트를 컨테이너의 8080 포트로 연결
# --env-file=.env: 작성한 환경 변수 파일 로드
docker run -d -p 80:8080 --env-file=.env ghcr.io/${GH_USER}/s3:latest

# 브라우저에서 퍼블릭 DNS 또는 IP로 접속하여 확인
```
