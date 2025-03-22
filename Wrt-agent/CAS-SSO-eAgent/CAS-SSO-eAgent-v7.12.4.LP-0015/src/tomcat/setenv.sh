### setenv.sh

CATALINA_OPTS=""

##********************************************
##******* JAVA Setup for cas *****************
##*
JAVA_HOME=/opt/java
##*
##*    /!\  Memory setup have to be configured on WARTA Environments
CATALINA_OPTS+=" -Xms1G -Xmx1536M"


##********************************************
##******* CAS main host address **************
##*
#@      variable-name:                      default:            property-key:
#?      ${casHostName}                      cas.warta.pl        cas.host.name
#@
CATALINA_OPTS+=" -DcasHostName=casdev.warta.pl"


##********************************************
##******* CAS additinal address **************
##*
#@      variable-name:                      default:            property-key:
#?      ${casLogoutRedirectUrl}             <empty>             cas.logout.redirect-url
#@
CATALINA_OPTS+=" -DcasLogoutRedirectUrl="


##********************************************
##******* CAS ConfigServer *******************
##*
##*    /!\  Opcja konfigutująca wybraną gałąź konfiguracji w ConfigServer, przykładowe wartości: dev, tst, acc, prd
#@
CATALINA_OPTS+=" -DcasEnvironment=dev"
##*
##*    /!\  Opcja konfigutująca odpowiedni protokół dla ConfigServer: https,http
##*    /!\  Opcja konfigutująca odpowiedni adres hosta ConfigServer: domena lub adres IP z opcjonalnym numerem portu
##*
#@      variable-name:                      default:            property-key:
#?      ${DcasConfigServerProto}            'null'              spring.cloud.config.uri
#?      ${casConfigServerHost}              'null'              spring.cloud.config.uri
#@
CATALINA_OPTS+=" -DcasConfigServerProto=https"
CATALINA_OPTS+=" -DcasConfigServerHost=casconfdev.warta.pl:8443"


##********************************************
##******* Actuators and Metrics **************
##*
#@      variable-name:                      default:            property-key:
#?      ${casAuthnAcceptEnabled}            false               cas.authn.accept.enabled
#?      ${casSpringSecurityUserName}        'Uzupelnic-Setenv'  spring.security.user.name
#?      ${casSpringSecurityUserPassword}    'Uzupelnic-Setenv'  spring.security.user.password
#@
CATALINA_OPTS+=" -DcasAuthnAcceptEnabled=false"
CATALINA_OPTS+=" -DcasSpringSecurityUserName=casuser"
CATALINA_OPTS+=" -DcasSpringSecurityUserPassword=Mellon"


##********************************************
##******* HazelCast **************************
##*
#@      variable-name:                      default:            property-key:
#?      ${casClusterInstanceName}           'Uzupelnic-Setenv'  cas.ticket.registry.hazelcast.cluster.core.instance-name
#?      ${casClusterMembers}                'Uzupelnic-Setenv'  cas.ticket.registry.hazelcast.cluster.network.members
#?      ${casClusterCPMemberCount}          0                   cas.ticket.registry.hazelcast.cluster.core.cp-member-count
#@
CATALINA_OPTS+=" -DcasClusterInstanceName=192-168-35-${HOSTNAME%.*.*}"
CATALINA_OPTS+=" -DcasClusterMembers=192.168.35.11,192.168.35.12,192.168.35.13"
CATALINA_OPTS+=" -DcasClusterCPMemberCount=0"


##********************************************
##******* Mfa Trusted Device-Fingerprint *****
##*
#@      variable-name:                      default:            property-key:
#?      ${casMfaTrustedDeviceCookieSuffix}  TRUSTED             cas.authn.mfa.trusted.device-fingerprint.cookie.name
#@
CATALINA_OPTS+=" -DcasMfaTrustedDeviceCookieSuffix=trust5H1"


