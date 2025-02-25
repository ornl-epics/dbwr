# Display Runtime Default Files
#export DBWR1=file:/bob/rules.bob
#export DBWR2=file:/bob/macros.bob
#export DBWR3=file:/bob/monitors_textupdate.bob
#export DBWR4=file:/bob/DTDInsertion.bob

# Configure PVWS URL 
#export PVWS_HOST=localhost:8081
#export PVWS_WS_PROTOCOL=ws
#export PVWS_HTTP_PROTOCOL=http
#

export EPICS_CA_AUTO_ADDR_LIST= NO
export EPICS_CA_ADDR_LIST=`cat /usr/local/epics/Config/EPICS_ADDR_LIST | xargs echo`
export EPICS_PVA_AUTO_ADDR_LIST=NO
export EPICS_PVA_ADDR_LIST=`cat /usr/local/epics/Config/EPICS_ADDR_LIST | xargs echo`
export WHITELIST1=file:/displays/CSS/.*
#export PV_DEFAULT_TYPE=pva
