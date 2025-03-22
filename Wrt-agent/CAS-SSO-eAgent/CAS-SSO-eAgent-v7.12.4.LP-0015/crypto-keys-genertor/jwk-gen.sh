#!/bin/bash -l

#? Order of properties keys are from  'combined properties'
#?
for keyname in \
    cas.tgc.crypto.signing.key \
    cas.tgc.crypto.encryption.key \
    cas.webflow.crypto.encryption.key \
    cas.webflow.crypto.signing.key \
    cas.ticket.registry.hazelcast.crypto.encryption.key \
    cas.ticket.registry.hazelcast.crypto.signing.key \
    cas.authn.mfa.trusted.crypto.encryption.key \
    cas.authn.mfa.trusted.crypto.signing.key \
    cas.authn.mfa.trusted.device-fingerprint.cookie.crypto.encryption.key \
    cas.authn.mfa.trusted.device-fingerprint.cookie.crypto.signing.key \
    cas.authn.saml-idp.core.session-replication.cookie.crypto.encryption.key \
    cas.authn.saml-idp.core.session-replication.cookie.crypto.signing.key
do
    if [[ $keyname == 'cas.webflow.crypto.encryption.key' ]]; then
        size=128; psize=16
    elif [[ $keyname == 'cas.ticket.registry.hazelcast.crypto.encryption.key' ]]; then
        size=256; psize=256
    else
        size=512; psize=512
    fi
    key=$( java -jar jwk-gen.jar -t oct -s $size | grep '"k"'| cut -d: -f2 | tr -d '" ')

    echo -en "\e[1;33m"
    echo -en "$psize"
    echo -en "\t\e[1;36m"
    echo -en "$keyname"
    echo -en "\e[0m"
    echo -en "="
    echo -en "\e[1;32m"
    echo -en "$key"
    echo -e "\e[0m"

done
