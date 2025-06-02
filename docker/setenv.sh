export WHITELIST1=file:/displays/CSS/.*
export WHITELIST2=http://vclx4.fnal.gov/.*
export PV_DEFAULT_TYPE=pva

# ========== End old settings (unicast) =========== #
export EPICS_HOST_INTERFACE='enp65s0f0'
export EPICS_PVA_ADDR_LIST="239.128.1.6,8@$EPICS_HOST_INTERFACE 239.128.1.6"

source /usr/local/epics/Config/epicsENV
