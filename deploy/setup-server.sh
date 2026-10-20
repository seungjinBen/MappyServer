#!/bin/bash
# ============================================================
# MappyEnglish — Ubuntu 서버(EC2·Oracle 공통) 초기 세팅
# VM 생성 후 딱 1회 실행: bash setup-server.sh
# ============================================================
set -euo pipefail

echo "=== 1. 타임존 Asia/Seoul ==="
sudo timedatectl set-timezone Asia/Seoul
timedatectl | grep "Time zone"

echo "=== 2. 필수 패키지 (Java 17) ==="
sudo apt-get update
sudo apt-get install -y openjdk-17-jre-headless curl

echo "=== 3. Caddy 설치 (자동 HTTPS 리버스 프록시) ==="
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' \
  | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' \
  | sudo tee /etc/apt/sources.list.d/caddy-stable.list
sudo apt-get update && sudo apt-get install -y caddy

echo "=== 4. VM 내부 방화벽 개방 (Oracle Cloud는 Security List 외에 iptables도 막혀 있음, EC2는 무해) ==="
if command -v iptables >/dev/null; then
  sudo iptables -I INPUT 5 -p tcp --dport 80 -j ACCEPT
  sudo iptables -I INPUT 6 -p tcp --dport 443 -j ACCEPT
  sudo apt-get install -y iptables-persistent && sudo netfilter-persistent save || true
fi

echo "=== 5. 스왑 2G (소형 인스턴스 OOM 보험) ==="
if ! swapon --show | grep -q swapfile; then
  sudo fallocate -l 2G /swapfile
  sudo chmod 600 /swapfile
  sudo mkswap /swapfile
  sudo swapon /swapfile
  echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
fi
free -h

echo "=== 6. 앱 디렉토리 + 전용 실행 계정 ==="
mkdir -p /home/ubuntu/app/logs

# 서비스는 sudo 없는 전용 계정으로 구동 (systemd User=mappyenglish)
if ! id mappyenglish &>/dev/null; then
  sudo useradd --system --no-create-home --shell /usr/sbin/nologin mappyenglish
fi
# /home/ubuntu(750) 통과를 위해 ubuntu 그룹에 추가 — 배포(scp/mv)는 계속 ubuntu가 수행
sudo usermod -aG ubuntu mappyenglish
sudo chown -R ubuntu:mappyenglish /home/ubuntu/app
sudo chmod -R g+rX /home/ubuntu/app
sudo chmod g+w /home/ubuntu/app/logs

echo ""
echo "✅ 초기 세팅 완료. 다음 순서:"
echo "  1) /home/ubuntu/app/.env 작성 (deploy/.env.prod.example 참고)"
echo "     작성 후 반드시: chmod 600 /home/ubuntu/app/.env"
echo "  2) deploy/mappyenglish.service → /etc/systemd/system/ 복사 후 enable"
echo "  3) deploy/Caddyfile → /etc/caddy/Caddyfile 교체 후 caddy reload"
echo "  4) GitHub 리포 Secrets 에 SERVER_HOST, SERVER_SSH_KEY 등록 → main 푸시하면 자동 배포"
