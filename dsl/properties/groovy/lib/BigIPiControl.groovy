import com.cloudbees.flowpdf.*
import iControl.CommonAddressPort
import iControl.CommonEnabledState
import iControl.CommonStatisticType
import iControl.Interfaces
import iControl.LocalLBPoolBindingStub

/**
 * BigIPiControl
 */
class BigIPiControl extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
            pluginName         : '@PLUGIN_KEY@',
            pluginVersion      : '@PLUGIN_VERSION@',
            configFields       : ['config'],
            configLocations    : ['ec_plugin_cfgs'],
            defaultConfigValues: [:]
        ]
    }

    // Static variables that explain the n^2 complex relationship
    // between nodes, pools, and pool members. General names were
    // given to apply to enabled/disabled, available/unavailable, existant/nonexistant
    public static final def NEGATIVE = 0              // Disabled/Unavailable/Nonexistant Results
    public static final def POSITIVE_AND_NEGATIVE = 1 // Mixed Results
    public static final def POSITIVE = 2              // Enabled/Available/Existant Results

    @Lazy
    Interfaces interfaces = {
        def config = context.getConfigValues()
        String hostname = config.getRequiredParameter('hostname').value
        def port = config.getRequiredParameter('port').value as int

        def credential = config.getRequiredCredential('credential')

        new Interfaces(hostname, port, credential.userName, credential.secretValue)
    }()

    /** This is a special method for checking connection during configuration creation
     */
    def checkConnection(StepParameters p, StepResult sr) {
        // Use this pre-defined method to check connection parameters
        try {
            // Put some checks here
            String version = interfaces.getSystemSystemInfo().get_version()
            println version
        } catch (Throwable e) {
            // Set this property to show the error in the UI
            sr.setOutcomeProperty("/myJob/configError", e.message)
            sr.apply()
            throw e
        }
    }

    // === check connection ends ===

    private static void processResult(StepResult sr, String outcome, String summary, String resultPropertySheet, String value = "") {
        sr.setJobSummary(summary)

        if ((outcome != "error") && (value != "")) {
            sr.setOutcomeProperty(resultPropertySheet, value)
        }

        sr.setJobStepOutcome(outcome)
        sr.setJobStepSummary(summary)

        sr.apply()
    }

    //~ /**
    //~ * enableNode - Enable Node/Enable Node
    //~ * https://clouddocs.f5.com/api/icontrol-rest/APIRef_tm_ltm_node.html
    //~ * @param config (required: true)
    //~ * @param partitionName (required: true)
    //~ * @param nodeName (required: true)

    //~ */
    //~ def enableNode(StepParameters p, StepResult sr) {
    //~ // Use this parameters wrapper for convenient access to your parameters
    //~ EnableNodeParameters sp = EnableNodeParameters.initParameters(p)

    //~ def nodeStub = interfaces.getLocalLBNodeAddressV2()
    //~ def nodes = nodeStub.get_list()
    //~ boolean found = false
    //~ String fullNodeName = "/$sp.partitionName/$sp.nodeName"
    //~ nodes.each {
    //~ log.info("Found node: $it")
    //~ if (it == fullNodeName) {
    //~ found = true
    //~ log.info "Node $fullNodeName exists"
    //~ }
    //~ }

    //~ if (!found) {
    //~ context.bailOut("Cannot find node $fullNodeName")
    //~ }

    //~ def status = nodeStub.get_session_status([fullNodeName] as String[])
    //~ log.info "Node status: $status"

    //~ def newState = CommonEnabledState.STATE_ENABLED as CommonEnabledState[]
    //~ nodeStub.set_session_enabled_state([fullNodeName] as String[], newState)

    //~ log.info "Enabled node $fullNodeName"
    //~ }


    //~ /**
    //~ * disableNode - Disable Node/Disable Node
    //~ * Add your code into this method and it will be called when the step runs
    //~ * @param config (required: true)
    //~ * @param partitionName (required: true)
    //~ * @param nodeName (required: true)

    //~ */
    //~ def disableNode(StepParameters p, StepResult sr) {
    //~ // Use this parameters wrapper for convenient access to your parameters
    //~ DisableNodeParameters sp = DisableNodeParameters.initParameters(p)

    //~ def nodeStub = interfaces.getLocalLBNodeAddressV2()
    //~ def nodes = nodeStub.get_list()
    //~ boolean found = false
    //~ String fullNodeName = "/$sp.partitionName/$sp.nodeName"
    //~ nodes.each {
    //~ log.info("Found node: $it")
    //~ if (it == fullNodeName) {
    //~ found = true
    //~ log.info "Node $fullNodeName exists"
    //~ }
    //~ }

    //~ if (!found) {
    //~ context.bailOut("Cannot find node $fullNodeName")
    //~ }

    //~ def status = nodeStub.get_session_status([fullNodeName] as String[])
    //~ log.info "Node status: $status"

    //~ def newState = CommonEnabledState.STATE_DISABLED as CommonEnabledState[]
    //~ nodeStub.set_session_enabled_state([fullNodeName] as String[], newState)

    //~ log.info "Disabled node $fullNodeName"
    //~ }

//~ /**
    //~ * listPools - List Pools/List Pools
    //~ * Add your code into this method and it will be called when the step runs
    //~ * @param config (required: true)
    //~ * @param partitionName (required: true)

    //~ */
    //~ def listPools(StepParameters p, StepResult sr) {
    //~ // Use this parameters wrapper for convenient access to your parameters
    //~ ListPoolsParameters sp = ListPoolsParameters.initParameters(p)
    //~ def poolStub = interfaces.localLBPool
    //~ poolStub.get_list().each {
    //~ log.info "Found pool: $it"
    //~ }
    //~ }


    // Checks the number of connections to a pool member
    // Used to confirm that a pool member is disabled
    private int getPoolMemberActiveConnections(String[] poolNames, CommonAddressPort[][] commonAddressPort) {
        def Long result = -1

        // Retrieve the statistics for each pool

        def pools = interfaces
            .getLocalLBPool()
            .get_member_statistics(poolNames, commonAddressPort)

        for (pool in pools) {
            // Retrieve the statistics for each pool member
            for (memberStatistics in pool.statistics) {
                // Iterate through each pool member's statistic
                for (statistic in memberStatistics.statistics) {
                    if (statistic.type == CommonStatisticType.STATISTIC_SERVER_SIDE_CURRENT_CONNECTIONS) {
                        if (result == -1) {
                            result = statistic.value.low
                        } else {
                            result = statistic.value.low + result
                        }
                        break
                    }
                }
            }
        }

        if (result < 0) {
            throw new Exception("Could not locate node(s)")
        }

        println "Active connection count is ${result}"

        return result
    }

/**
 * changeBalancingPoolMemberStatus - Change Balancing Pool Member Status/Change Balancing Pool Member Status
 * Add your code into this method and it will be called when the step runs
 * @param config (required: true)
 * @param partitionName (required: true)
 * @param poolName (required: true)
 * @param membersNames (required: true)
 * @param setStatus (required: true)
 * @param resultPropertySheet (required: true)

 */
    def changeBalancingPoolMemberStatus(StepParameters p, StepResult sr) {
        ChangeBalancingPoolMemberStatusParameters sp = ChangeBalancingPoolMemberStatusParameters.initParameters(p)

        Long sleepInterval = Long.parseLong(sp.sleepInterval)
        boolean doWait = sp.doWait

        String partitionName = sp.partitionName.replaceAll('\\\\', '/')
        if (!partitionName) {
            throw new Exception("Partition Name is empty")
        }

        String commonPrefix = partitionName
        if (!commonPrefix.startsWith('/')) {
            commonPrefix = '/' + commonPrefix
        } else {
            commonPrefix.replaceFirst('^[/]{2,}', '/')
        }

        if (commonPrefix[-1] != '/') {
            commonPrefix = commonPrefix + '/'
        } else {
            commonPrefix.replaceFirst('[/]{2,}$', '/')
        }

        String poolName = commonPrefix + sp.poolName.replaceAll("\\s+", "")
        if (!poolName) {
            throw new Exception("Partition Name is empty")
        }

        String[] poolNames = [poolName]

        String[] membersNames = sp.membersNames
            .split(',')
            .join("\n")
            .split(/[\r\n]+/)

        if (membersNames.size() == 0) {
            throw new Exception("Members list is empty")
        }

        def commonAddressPort = [][]
        def plainAddressPort = []
        for (member in membersNames) {
            def mem = member.trim()
            if (mem != "") {
                String[] addressPort = mem.split(":")
                plainAddressPort.add(new CommonAddressPort(
                    commonPrefix + addressPort[0],
                    Long.parseLong(addressPort[1])
                ))
            }
        }

        commonAddressPort.add(plainAddressPort)

        def pool = interfaces.getLocalLBPool()

        def plainEnabledState = []
        def commonEnabledState = [][]
        commonEnabledState.add(plainEnabledState)

        String outcome = "success"
        String summary = ""
        String data = ""
        try {
            if ((sp.setStatus == "enabled") || (sp.setStatus == "force_on")) {
                membersNames.each {
                    plainEnabledState.add(CommonEnabledState.STATE_ENABLED)
                }

                pool.set_member_session_enabled_state(poolNames, commonAddressPort as CommonAddressPort[][], commonEnabledState as CommonEnabledState[][])
                if (sp.setStatus == "force_on") {
                    pool.set_member_monitor_state(poolNames, commonAddressPort as CommonAddressPort[][], commonEnabledState as CommonEnabledState[][])

                    summary = "Successfully forced offline"
                } else {
                    summary = "Successfully enabled"
                }
            } else if ((sp.setStatus == "disabled") || (sp.setStatus == "force_off")) {
                membersNames.each {
                    plainEnabledState.add(CommonEnabledState.STATE_DISABLED)
                }

                pool.set_member_session_enabled_state(poolNames, commonAddressPort as CommonAddressPort[][], commonEnabledState as CommonEnabledState[][])
                if (sp.setStatus == "force_off") {
                    pool.set_member_monitor_state(poolNames, commonAddressPort as CommonAddressPort[][], commonEnabledState as CommonEnabledState[][])

                    summary = "Successfully forced offline"
                } else {
                    if (doWait) {
                        def currentConnections = getPoolMemberActiveConnections(poolNames, commonAddressPort as CommonAddressPort[][])
                        while (currentConnections > 0) {
                            println("Waiting for ${currentConnections} to close, sleep for ${sleepInterval} second(s)")
                            sleep(sleepInterval * 1000L, { println("Waiting aborted!"); return true })
                            currentConnections = getPoolMemberActiveConnections(poolNames, commonAddressPort as CommonAddressPort[][])
                        }
                        println("All connections closed, done waiting.")
                    }

                    summary = "Successfully disabled"
                }
            } else {
                throw new Exception("Unknown desired status")
            }
        } catch (Throwable e) {
            e.printStackTrace()
            log.error(e.getClass())
            log.error(e.stackTrace)
            outcome = "error"
            summary = "Failed to set status: " + e.message
        }


        String resultPropertySheet = sp.resultPropertySheet;
        if (resultPropertySheet == "") {
            resultPropertySheet = "/myJob/poolMemberStatus"
            log.info("Assumed result property sheet: " + resultPropertySheet)
        }

        processResult(sr, outcome, summary, resultPropertySheet, data)
    }

// === step ends ===

}
