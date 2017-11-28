/usr/share/logstash/bin/logstash -e 'input { \
  beats { \
    port => 5443 \
    ssl => true \
    ssl_certificate => "/etc/pki/tls/certs/logstash-forwarder.crt" \
    ssl_key => "/etc/pki/tls/private/logstash-forwarder.key" \
  } \
} \
 output { stdout {}}'