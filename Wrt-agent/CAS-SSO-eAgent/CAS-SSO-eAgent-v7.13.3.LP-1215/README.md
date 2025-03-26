# Wdrożenie paczki **CAS-SSO-eAgent-v7.13.3.LP-1215**:


## (i) Wymagania CAS


1. Skonfigurowane środowisko wg wdrożenia [CAS-SSO-eAgent-v7.12.4.LP-0015](../CAS-SSO-eAgent-v7.12.4.LP-0015/README.md)
2. Wymagane udostępnienie atrybutu użytkownika zawierającego numer telefonu.


## (ii) Wymagania LDAP


W usłudze katalogowej LDAP należy dodać grupy użytkowników, które są wymagane do obsługi mechanizmu wysyłki wiadomości z Tokenem MFA poprzez kanał/y komunikacji. Wysyłka Tokenu MFA  realizowana jest na podstawie przynależności użytkowników do grup przechowywanych w usłudze katalogowej LDAP.

|CN   | pełna definicja |
| --- | --- |
|**MFA-MAIL**|cn=MFA-MAIL,ou=groups,dc=warta,dc=pl|
|**MFA-MAIL-SMS**|cn=MFA-MAIL-SMS,ou=groups,dc=warta,dc=pl|
|**MFA-SMS**|cn=MFA-SMS,ou=groups,dc=warta,dc=pl|
|**MFA-PANEL-ACCESS**|cn=MFA-PANEL-ACCESS,ou=groups,dc=warta,dc=pl|


## (iii) Przygotowanie środowiska CAS


#### Ustawienia CAS-SSO: scripts
1. Skrypt obsługujący Transport wiadomości SMS do wewnętrznej usługi obsługującej bramkę SMS. Plik [sms-provider--dev.cnf.groovy](src/etc/cas/scripts/sms-provider--dev.cnf.groovy) należy zapisać w  ``/etc/cas/scripts``.


#### Ustawienia CAS-SSO: services
1. Uruchomić poniższe polecenia w katalogu ``/etc/cas/services``, w celu wymaganej aktualizacji definicji serwisów:


	```bash
	grep 'allowedAttributes.*memberOf' *.json
	sed -i -re 's/(allowedAttributes.*memberOf")(.*)/\1, "telephoneNumber"\2/g' *.json
	grep 'allowedAttributes.*memberOf' *.json
	```

#### Ustawienia Konfiguracji CAS-SSO w CAS-ConfigServer

1. Informacje o ważnych i wymaganych do uzupełnienia ustawieniach w konfiguracji dostępne są w pliku  [cas.properties.info](src/repo-casconfigserver/cas-agent.properties.prod.combined.deploy-info.properties).
2. Poglądowa konfiguracja pliku [cas.properties](src/repo-casconfigserver/cas-agent.properties.prod.combined.properties) wskazuje miejsca, w których należy uzupełnić wpisy z pkt 1.


