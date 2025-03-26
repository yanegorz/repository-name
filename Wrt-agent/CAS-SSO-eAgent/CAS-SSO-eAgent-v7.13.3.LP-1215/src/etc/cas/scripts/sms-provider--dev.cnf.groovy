import java.util.*
import wslite.soap.*
RunRun
def run(Object[] args) {
    def(smsFrom, smsTo, smsMessage, logger) = args
    logger.debug("sms-provider-script:: Run [{}, {}, {}]", smsFrom, smsTo, smsMessage)

    /*
        @+-----+------+--------------------------------------+------------+------------+
        @| env | user | password (Basic Auth)                | valid-From | valid-To   |
        @+-----+------+--------------------------------------+------------+------------+
        @| DEV | case | HzyKisN4hIP1G67_uwN-443q7utr9QXF4Lj4 | 2025-03-20 | 2025-05-19 |
        @+-----+------+--------------------------------------+------------+------------+
    */

    def soapClient = new SOAPClient('https://soaesbdev.warta.pl/cxf/cas/nonlife/sms/v1?wsdl')

    logger.debug("sms-provider-groovy:: soapClient [{}]", soapClient)

    def smsHeaderConsumerSystem  = 'CAS_DEV'
    def smsHeaderConsumerId      = 'CAS'
    def smsHeaderConsumerUnitId  = 1             //? Optional
    def smsHeaderExternalId      = 1             //? Optional
    def smsHeaderExpectedTimeout = 7000          //? Optional milliseconds
    def smsPriority              = 'HIGH'
    def smsCasMessageId          = 1             //? Optional
    def smsBrand                 = 'WARTA Auth'  //? Optional

    logger.debug("sms-provider-groovy:: Run [{}, {}, {}]", smsFrom, smsTo, smsMessage)

    def response = soapClient.send(SOAPVersion.V1_1,
        """<?xml version="1.0" encoding="UTF-8"?>
        <soapenv:Envelope
            xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:v1="http://soa.warta.pl/model/contract/channel/cas/nonlife/sms/v1"
            xmlns:v2="http://soa.warta.pl/model/object/canonical/cmm/service/v2"
            xmlns:v11="http://soa.warta.pl/model/object/channel/cas/nonlife/sms/common/v1">
            <soapenv:Header/>
            <soapenv:Body>
                <v1:sendMessagesRequest>
                    <v2:header>
                        <v2:consumerSystem>""" + smsHeaderConsumerSystem + """</v2:consumerSystem>
                        <v2:consumerId>""" + smsHeaderConsumerId + """</v2:consumerId>
                        <!--Optional:-->
                        <v2:consumerUnitId>""" + smsHeaderConsumerUnitId + """</v2:consumerUnitId>
                        <!--Optional:-->
                        <v2:externalId>""" + smsHeaderExternalId + """</v2:externalId>
                        <!--Optional:-->
                        <v2:expectedTimeout>""" + smsHeaderExpectedTimeout + """</v2:expectedTimeout>
                    </v2:header>
                    <v1:from>""" + smsFrom + """</v1:from>
                    <v1:priority>""" + smsPriority + """</v1:priority>
                    <!--1 or more repetitions:-->
                    <v1:message>
                        <!--Optional:-->
                        <v11:id>""" + smsCasMessageId + """</v11:id>
                        <v11:to>""" + smsTo + """</v11:to>
                        <!--Optional:-->
                        <v11:brand>""" + smsBrand + """</v11:brand>
                        <v11:text>""" + smsMessage + """</v11:text>
                    </v1:message>
                </v1:sendMessagesRequest>
            </soapenv:Body>
        </soapenv:Envelope>
        """
    )
    logger.debug("sms-provider-groovy:: response [{}]", response)

    def responseCode = response.httpResponse.statusCode
    logger.debug("sms-provider-groovy:: response code", responseCode)

    return true
}
