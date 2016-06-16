// Automatically generated. Make future change here.

definition(
name: "Bobs Thermostat Syncer",
namespace: "smartthings",
author: "Tim Slagle Modified by: Duane Helmuth/Bob Snyder",
description: "Adjust a thermostat based on the setting of another thermostat",
category: "Green Living",
iconUrl: "http://icons.iconarchive.com/icons/icons8/windows-8/512/Science-Temperature-icon.png",
iconX2Url: "http://icons.iconarchive.com/icons/icons8/windows-8/512/Science-Temperature-icon.png"
)

section
{
input "thermostat1", "capability.thermostat", title: "Which Master Thermostat?", multi: false, required: true
input "thermostat2", "capability.thermostat", title: "Which Slave Thermostat?", multi: false, required: true
input "tempDiff", "number", title: "Temperature Difference Between Master and Slave?", required: true, defaultValue: 2
input "sendPushMessage", "bool", title: "Send a push notification?", required: false, defaultValue: true
input "sendSMS", "phone", title: "Send as SMS?", required: false, defaultValue: false

}

def installed(){
log.debug "Installed called with ${settings}"
init()
}

def updated(){
log.debug "Updated called with ${settings}"
unsubscribe()
init()
}

def init(){
//nIn(60, "temperatureHandler")
subscribe(thermostat1, "thermostatSetpoint", temperatureHandler)
}

def temperatureHandler(evt) {

log.debug "Temperature Handler Begin"
//get the latest temp readings and compare
def MThermostatTemp = thermostat1.latestValue("thermostatSetpoint")
def SThermostatTemp = thermostat2.latestValue("thermostatSetpoint")
def difference = (SThermostatTemp - MThermostatTemp)

log.debug "Thermostat(M): ${MThermostatTemp}"
log.debug "Thermostat(S): ${SThermostatTemp}"
log.debug "Temp Diff: ${tempDiff}"
log.debug "Current Temp Difference: ${difference}"

if( difference != tempDiff ){
        def NewTemp = (MThermostatTemp + tempDiff)
		def msg = "${thermostat2} sync'ed with ${thermostat1} with of offset of ${tempDiff} degrees.  Now at ${NewTemp}."
		thermostat2.setCoolingSetpoint(NewTemp)
        thermostat2.setHeatingSetpoint(NewTemp)
        thermostat2.poll()
		log.debug msg
		sendMessage(msg)
} 
}

private sendMessage(msg){
if (sendPushMessage == true) {
sendPush(msg)
}
if (sendSMS != null) {
sendSms(sendSMS, msg) 
}

}
