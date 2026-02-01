```sh
export GH_USER=<github username>
# export GH_USER=kimjava911
# WinOS
docker build -t ghcr.io/${GH_USER}/s3:latest .
# MacOS
docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/${GH_USER}/s3:latest .
```

```sh
# https://github.com/settings/tokens/new
# write:packages
export CR_TOKEN=<pat token>
echo $CR_TOKEN | docker login ghcr.io -u $GH_USER --password-stdin
docker push ghcr.io/${GH_USER}/s3:latest
```

```sh
expert PUBLIC_DNS=<public dns>
chmod 400 *.pem
ssh -i $(ls *.pem) ubuntu@${PUBLIC_DNS}
```

```sh
sudo apt update
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

sudo usermod -aG docker $USER
exit
```

```sh
docker -v
docker compose version
export CR_TOKEN=<pat token>
export GH_USER=<github username>
echo $CR_TOKEN | docker login ghcr.io -u $GH_USER --password-stdin
```

```dotenv
AWS_BUCKET_NAME=
AWS_REGION=
AWS_ACCESS_KEY=
AWS_SECRET_KEY=
```

```sh
vi .env # i -> (편집 후) -> esc -> :wq
docker run -d -p 80:8080 --env-file=.env ghcr.io/${GH_USER}/s3:latest
# public dns로 접근 또는 ip로 액세스
```