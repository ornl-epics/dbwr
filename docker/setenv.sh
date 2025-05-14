export WHITELIST1=file:/displays/CSS/.*
export WHITELIST2=http://vclx4.fnal.gov/.*
export PV_DEFAULT_TYPE=pva

# ========== End old settings (unicast) =========== #
export EPICS_HOST_INTERFACE='enp65s0f0'
export EPICS_PVA_ADDR_LIST="239.128.1.6,8@$EPICS_HOST_INTERFACE 239.128.1.6"

# Uncomment to restore NFS/Git repo epicsENV source
# This cannot be done until https://ghe-pip2.fnal.gov/epics-controls/Config/issues/508 is solved
#source /usr/local/epics/Config/epicsENV

# Uncomment to use local, modified epicsENV source from configMap
source /usr/local/dbwr/epicsENV
