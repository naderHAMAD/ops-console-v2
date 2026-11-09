#!/bin/bash
# Prepare un conteneur comme un vrai serveur : SSH + faux JBoss en service systemd (port 9990).
# Usage : bash /tmp/setup.sh <version_de_depart>      (defaut : 7.2.1)
set -e
INITIAL_VERSION="${1:-7.2.1}"

echo "[1/6] Installation des paquets (ssh, zip, unzip)..."
apt-get update -qq
DEBIAN_FRONTEND=noninteractive apt-get install -y -qq openssh-server zip unzip

echo "[2/6] Configuration SSH (root / testpass123)..."
mkdir -p /run/sshd
echo 'root:testpass123' | chpasswd
sed -i 's/^#\?PermitRootLogin.*/PermitRootLogin yes/' /etc/ssh/sshd_config
sed -i 's/^#\?PasswordAuthentication.*/PasswordAuthentication yes/' /etc/ssh/sshd_config
systemctl enable --now ssh

echo "[3/6] Utilisateur jboss et dossier d'installation (version ${INITIAL_VERSION})..."
id jboss >/dev/null 2>&1 || useradd -m jboss
mkdir -p /opt/jboss-eap
echo "${INITIAL_VERSION}" > /opt/jboss-eap/version.txt
chown -R jboss:jboss /opt/jboss-eap

echo "[4/6] Faux serveur JBoss (repond sur le port 9990)..."
cat > /usr/local/bin/fake-jboss.py << 'PYEOF'
from http.server import HTTPServer, BaseHTTPRequestHandler
class H(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.end_headers()
        self.wfile.write(b'{"status":"UP"}')
HTTPServer(('0.0.0.0', 9990), H).serve_forever()
PYEOF

echo "[5/6] Service systemd jboss-eap..."
cat > /etc/systemd/system/jboss-eap.service << 'SVCEOF'
[Unit]
Description=Fake JBoss EAP
[Service]
ExecStart=/usr/bin/python3 /usr/local/bin/fake-jboss.py
Restart=always
[Install]
WantedBy=multi-user.target
SVCEOF
systemctl daemon-reload
systemctl enable --now jboss-eap

echo "[6/6] Verification..."
sleep 2
echo -n "ssh        : "; systemctl is-active ssh
echo -n "jboss-eap  : "; systemctl is-active jboss-eap
echo -n "port 9990  : "; python3 -c "import urllib.request; print(urllib.request.urlopen('http://localhost:9990').read().decode())"
echo -n "version    : "; cat /opt/jboss-eap/version.txt
echo "OK - serveur de test pret"
