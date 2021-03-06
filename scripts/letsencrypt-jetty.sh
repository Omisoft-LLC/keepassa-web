#!/usr/bin/env bash
# input: fullchain.pem and privkey.pem as generated by the "letsencrypt-auto" script when run with
# the "auth" aka "certonly" subcommand

# convert certificate chain + private key to the PKCS#12 file format
#openssl pkcs12 -export -out ../certs/production/keepassa.eu/keystore.pkcs12 -in ../certs/production/keepassa.eu/fullchain.pem -inkey ../certs/production/keepassa.eu/privkey.pem
openssl pkcs12 -export -out ../certs/development/keepassa.omisoft.eu/keystore.pkcs12 -in ../certs/development/keepassa.omisoft.eu/fullchain.pem -inkey ../certs/development/keepassa.omisoft.eu/privkey.pem

# convert PKCS#12 file into Java keystore format
#keytool -importkeystore -srckeystore ../certs/production/keepassa.eu/keystore.pkcs12 -srcstoretype PKCS12 -destkeystore ../certs/production/keepassa.eu/keystore.jks
keytool -importkeystore -srckeystore ../certs/development/keepassa.omisoft.eu/keystore.pkcs12 -srcstoretype PKCS12 -destkeystore ..//certs/development/keepassa.omisoft.eu/keystore.jks

# don't need the PKCS#12 file anymore
#rm keystore.pkcs12

# Now use "keystore.jks" as keystore in jetty with the keystore password you specfied when you ran
# the "keytool" command
