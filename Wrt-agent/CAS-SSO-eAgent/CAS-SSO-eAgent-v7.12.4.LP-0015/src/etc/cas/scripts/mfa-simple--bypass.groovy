import java.util.*

def boolean run(final Object... args) {
    def authentication = args[0]    // The object representing the established authentication event.
    def principal = args[1]         // The object representing the authenticated principal.
    def service = args[2]           // The object representing the corresponding service definition in the registry.
    def provider = args[3]          // The object representing the requested multifactor authentication provider.
    def logger = args[4]            // The object responsible for issuing log messages such as logger.info(...).
    def httpRequest = args[5]       // The object responsible for capturing the http request.

    //  defined LDAP groups for the users, who have to use MFA.
    def mfaAllowedGroups = [
        "cn=MFA,ou=Groups,dc=warta,dc=pl",
        "cn=mfa,ou=groups,dc=warta,dc=pl",
        "cn=MFA,ou=groups,dc=warta,dc=pl"
    ]
    // logger.info("mfaAllowedGroups dump: " + mfaAllowedGroups.dump()) // debug
    def mfaAllowedGroupsFailedInfo = String.join("', '", mfaAllowedGroups)
    // Stuff happens...
    def principalAttributes = authentication.principal.attributes
    def memberOf = principalAttributes['memberOf']
    // logger.info("memberOf dump: " + memberOf.dump()) // debug

    def gn = principalAttributes.givenName.get(0).toString()
    def sn = principalAttributes.sn.get(0).toString()
    def ml = principalAttributes.mail.get(0).toString()
    logger.info("Evaluating principal against MFA: '{} {}' {}", gn, sn, ml)

    for (String group : memberOf) {
        if (mfaAllowedGroups.contains(group)) {
            logger.info("-- Principal '${gn} ${sn}' Is a member of the Mfa-group: '${group}', and multifactor authentication for this principal has to be processed.")
            return true // procced MFA
        }
    }
    // default behavior
    logger.info("-- Principal '${gn} ${sn}' Isn't a member of any Mfa-group: '${mfaAllowedGroupsFailedInfo}' multifactor authentication for this principal will be skipped and bypassed.")
    return false // skip MFA and bypass
}
