#!/bin/bash
echo '=== Creating required directories ==='
mkdir -p /var/www/certbot
mkdir -p ./nginx/ssl
mkdir -p ./nginx/certbot

echo '=== Installing Certbot ==='
yum install -y epel-release
yum install -y certbot

echo '=== Obtaining SSL Certificate ==='
certbot certonly --webroot -w /var/www/certbot -d zesthub.xyz -d www.zesthub.xyz --non-interactive --agree-tos --email admin@zesthub.xyz

if [ -f '/etc/letsencrypt/live/zesthub.xyz/fullchain.pem' ]; then
    echo '=== Certificate obtained successfully ==='
    cp /etc/letsencrypt/live/zesthub.xyz/fullchain.pem ./nginx/ssl/fullchain.pem
    cp /etc/letsencrypt/live/zesthub.xyz/privkey.pem ./nginx/ssl/privkey.pem

    echo '=== Restarting Nginx with HTTPS ==='
    docker compose restart nginx

    echo '=== Setting up auto-renewal ==='
    echo '0 3 * * * certbot renew --quiet && docker exec weather-nginx nginx -s reload' | crontab -

    echo '=== HTTPS setup complete! ==='
else
    echo '=== Failed to obtain certificate. HTTP mode will be used. ==='
    echo '=== This may be because domain is not filed (ICP) or DNS not propagated ==='
fi
