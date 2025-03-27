# Wdrożenie paczki **CAS-SSO-eAgent-v7.12.4.LP-0015**:


## (i) Wymagania:

1. Środowisko **Java 21**
2. Serwer aplikacji **Apache Tomcat 10.0.33**
3. Serwer BazoDanowy **MariaDB 10.11** + **rsync**
4. Zasób sieciowy **NFS** zamontowany w katalogu  ``/etc/cas``  w celu ujednolicenia konfiguracji na wszystkich węzłach klastra
	- Uprawnienia **R/W**
	- Właściciel/Grupa  ``tomcat:tomcat``



## (ii) Przygotowanie środowiska


#### Ustawienia Apache Tomcat
1. Konfiguracja środowiskowa. Plik [setenv.sh](src/tomcat/setenv.sh) należy zapisać w  ``<apache-tomcat-dir>/bin`` oraz zweryfikować i zmodyfikować ustawienia.


#### Ustawienia zasobu sieciowego NFS
1. Stworzyć poniższe katalogi na zasobie sieciowym:
	```bash
	/etc/cas/config
	/etc/cas/saml-idp
	/etc/cas/saml-idp/metadata-backups
	/etc/cas/scripts
	/etc/cas/services
	```

#### Ustawienia CAS-SSO: config
1. Dodatkowe komunikaty i ustawienia. Plik [warta_messages.properties](src/etc/cas/config/warta_messages.properties) należy zapisać w  ``/etc/cas/config``.
2. Konfiguracja dzienników zdarzeń. Plik [log4j2.xml](src/etc/cas/config/log4j2.xml) należy zapisać w   ``/etc/cas/config``.


#### Ustawienia CAS-SSO: scripts
1. Skrypt zarządzający wysyłką tokenu MFA za pośrednictwem zdefiniowanych kanałów komunikacji, takich jak wiadomości e-mail i wiadomości tekstow. Plik [mfa-simple--bypass.groovy](src/etc/cas/scripts/mfa-simple--bypass.groovy) należy zapisać w  ``/etc/cas/scripts``.


#### Ustawienia CAS-SSO: services
1. Przekopiować definicje serwisów zarejestrowanych w **CAS** do katalogu   ``/etc/cas/services``.
2. Uruchomić poniższe polecenia, w celu wymaganej aktualizacji definicji serwisów:
	```bash
	grep 'RegExRegisteredService"' *
	sed -i 's/org.apereo.cas.services.RegexRegisteredService"/org.apereo.cas.services.CasRegisteredService"/g' *.json
	grep 'RegExRegisteredService"' *
	```


#### Ustawienia Serwera MariaDB
1. Konfiguracja zapewniająca inicjalizację baz danych w kodowaniu znaków UTF-8. Plik [utf8.cnf](src/etc/my.cnf.d/utf8.cnf) należy zapisać w katalogu konfiguracji **mariadb**   ``/etc/.../my.cnf.d``.
2. Konfiguracja zapewniająca możliwość otwierania większej liczby plików niż standardowo narzucane ograniczenia (brak tych ustawień może zaburzyć mechanizm replikacji danych). Plik [10-limits.conf](src/etc/my.cnf.d/10-limits.conf)  należy zapisać w katalogu usługi **mariadb**   ``/etc/...-mariadb.service.d``.
3. Konfiguracja węzła klastra **galera**. Plik [galera.cnf](src/etc/my.cnf.d/galera.cnf) należy zapisać w katalogu konfiguracji mariadb  ``/etc/.../my.cnf.d``.
	- W powyższym pliku wpisy **ip_node_1, ip_node_2, ip_node_3** trzeba zastąpić adresami IP poszczególnych węzłów klastra serwerów CAS a wpis **node_ip** adresem IP a **node_hostname** nazwą hosta węzła, na którym tworzony jest dany plik.
4. Po poprawnym uruchomieniu klastra na wybranym jednym węźle należy wydać polecenia zakładające struktury danych dla aplikacji CAS. Poniższy listing komend nie prezentuje realnie ustawionego hasła:
	```bash
	mysql -u root -pHASLO_ADMIN -e "CREATE USER IF NOT EXISTS 'cas_db_user'@'localhost' IDENTIFIED BY 'HASLO_UZYTKOWNIA'"

	mysql -u root -pHASLO_ADMIN -e "CREATE DATABASE cas_audit"
	mysql -u root -pHASLO_ADMIN -e "GRANT ALL PRIVILEGES ON cas_audit.* TO 'cas_db_user'@'localhost'"
	mysql -u root -pHASLO_ADMIN -e "FLUSH PRIVILEGES"

	mysql -u root -pHASLO_ADMIN -e "CREATE DATABASE cas_events"
	mysql -u root -pHASLO_ADMIN -e "GRANT ALL PRIVILEGES ON cas_events.* TO 'cas_db_user'@'localhost'"
	mysql -u root -pHASLO_ADMIN -e "FLUSH PRIVILEGES"

	mysql -u root -pHASLO_ADMIN -e "CREATE DATABASE cas_trusted CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci'"
	mysql -u root -pHASLO_ADMIN -e "GRANT ALL PRIVILEGES ON cas_trusted.* TO 'cas_db_user'@'localhost'"
	mysql -u root -pHASLO_ADMIN -e "FLUSH PRIVILEGES"
	```



#### Ustawienia Konfiguracji CAS-SSO w CAS-ConfigServer
1. Klucze do Szyfrowania lub Podpisu istotnych informacji.
	- Dostępny w repozytorium skrypt [jwk-gen.sh](crypto-keys-genertor/jwk-gen.sh), wygeneruje wszystkie potrzebne klucze bez potrzeby uruchamiania **cas.war**.
	- Dla wygenerowania pojedynczego  klucza o określonym rozmiarze można użyć poniższego polecenia, z katalogu ``crypto-keys-genertor``:
		```bash
		java -jar jwk-gen.jar -t oct -s [size]
		```
		W otrzymanym wyniku oczekiwany klucz jest wartością elementu **'k'**:
		```json
		{
			"kty": "oct",
			"kid": "...",
			"k": "..."
		}
		```
2. Informacje o ważnych i wymaganych do uzupełnienia ustawieniach w konfiguracji dostępne są w pliku  [cas.properties.info](src/repo-casconfigserver/cas-agent.properties.prod.combined.deploy-info.properties).
3. Przygotowaną konfigurację w pliku [cas.properties](src/repo-casconfigserver/cas-agent.properties.prod.combined.properties) należy uzupełnić o wygenerowane klucze (pkt. 1) oraz zweryfikować i zmodyfikować ustawienia na bazie istniejącej konfiguracji dla CAS v6.1.7 na podstawie pliku z pkt. 2.


## (iii) Pierwsze uruchomienie

#### Aktualizacja Baz Danych

1. Przy pierwszym uruchomieniu CAS mając dostęp do baz danych, wypełni je odpowiednimi tabelami:
	- **cas_audit**  ➔ *COM_AUDIT_TRAIL*
	- **cas_events** ➔ *Cas_Event* **+** *events_properties*
	- **cas_trusted** ➔ *Jpa_Multifactor_Authentication_Trust_Record*
2. Sprawdzić status wypełnienia tabelami wszystkich baz danych.

#### Aktualizacja ustawień konfiguracji

1. Zatrzymać CAS-a.
2. Wykonać zmianę ustawień CAS w konfiguracji na CAS-ConfigServer dla połączeń JDBC do baz danych na poniższą:
	```properties
	cas.audit.jdbc.ddl-auto=validate

	cas.events.jpa.ddl-auto=validate

	cas.authn.throttle.jdbc.ddl-auto=validate

	cas.authn.mfa.trusted.jpa.ddl-auto=validate
	```
