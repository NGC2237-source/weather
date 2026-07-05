#!/bin/bash
set -e

echo '============================================'
echo '  Weather System Deployment Script'
echo '============================================'

if [ ! -f 'app.jar' ]; then
    echo '[ERROR] app.jar not found!'
    exit 1
fi

mkdir -p nginx/ssl nginx/certbot mysql/init

echo '=== Configuring Docker mirrors ==='
mkdir -p /etc/docker
cat > /etc/docker/daemon.json << 'DEOF'
{
  "registry-mirrors": [
    "https://docker.1ms.run",
    "https://docker.xuanyuan.me",
    "https://docker.m.daocloud.io"
  ]
}
DEOF
systemctl daemon-reload
systemctl restart docker

if [ ! -f 'mysql/init/init.sql' ]; then
    echo '[WARNING] mysql/init/init.sql not found!'
    read -p 'Continue without init data? (y/n): ' answer
    if [ "`$answer" != "y" ]; then
        exit 1
    fi
fi

echo '=== Starting Docker Compose ==='
docker compose down 2>/dev/null || true
docker compose up -d --build

echo '=== Waiting for services to start ==='
sleep 15

echo '=== Checking container status ==='
docker compose ps

echo ''
echo '============================================'
echo '  Deployment Complete!'
echo '============================================'
echo '  HTTP:  http://8.156.95.208'
echo ''
echo '  To setup HTTPS, run: bash setup-ssl.sh'
echo '============================================'
