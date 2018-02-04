#!/usr/bin/env bash
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
sudo iptables -t nat -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-port 8443

sudo iptables -t nat -I OUTPUT -p tcp -d 127.0.0.1 --dport 80 -j REDIRECT --to-ports 8080
sudo iptables -t nat -I OUTPUT -p tcp -d 127.0.0.1 --dport 443 -j REDIRECT --to-ports 8443
#Firewall rules, for redirecting when we run on dev machine and use android emulation (needed due to Android Emulator, virtual router)
sudo iptables -t nat -I OUTPUT -p tcp -d 192.168.20.45 --dport 80 -j REDIRECT --to-ports 8080
sudo iptables -t nat -I OUTPUT -p tcp -d 192.168.20.45 --dport 443 -j REDIRECT --to-ports 8443

#Copy keystore
sudo mkdir -p /opt/keepassa/etc
sudo cp ../certs/keystore.jks /opt/keepassa/etc/keystore.jks