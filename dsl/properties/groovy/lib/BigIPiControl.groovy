import com.cloudbees.flowpdf.*
import iControl.CommonAddressPort
import iControl.CommonEnabledState
import iControl.Interfaces


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
        try {
            String version = interfaces.getSystemSystemInfo().get_version()
            println version
        } catch (Throwable e) {
            def errMsg = "Connection check for Big-IP failed: " + e.message
            def suggestions = '''Reasons could be due to one or more of the following. Please ensure they are correct and try again:
1. Are your 'Hostname' and 'Port' correct?
2. Are your credentials correct?
'''

            sr.setOutcomeProperty('/myJobStep/outcome', 'error')
            sr.setOutcomeProperty('/myJobStep/summary', 'Error: ' + suggestions + "\n\n" + errMsg)
            sr.setOutcomeProperty('/myJob/configError', suggestions + "\n\n" + errMsg)
            sr.apply()

            log.logErrorDiag("Create Configuration failed.\n\n" + errMsg)
            log.logInfoDiag(suggestions)

            System.exit(-1)
        }

        log.info("Connection successfully checked")
    }

    // === check connection ends ===

    private static void processResult(StepResult sr, String outcome, String summary, String resultPropertySheet, String value = "") {
        sr.setJobSummary(summary)

//        if ((outcome != "error") && (value != "")) {
//            sr.setOutcomeProperty(resultPropertySheet, value)
//        }

        sr.setJobStepOutcome(outcome)
        sr.setJobStepSummary(summary)

        sr.apply()
    }

    private static void processResult(StepResult sr, String outcome, String summary) {
        sr.setJobSummary(summary)

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

/**
 * changeBalancingPoolMemberStatus - Change Balancing Pool Member Status/Change Balancing Pool Member Status
 * Add your code into this method and it will be called when the step runs
 * @param config (required: true)
 * @param partitionName (required: true)
 * @param poolName (required: true)
 * @param membersNames (required: true)
 * @param setStatus (required: true)

 */
    def changeBalancingPoolMemberStatus(StepParameters p, StepResult sr) {
        ChangeBalancingPoolMemberStatusParameters sp = ChangeBalancingPoolMemberStatusParameters.initParameters(p)

        String partitionName = sp.partitionName.replaceAll('\\\\', '/')
        if (!partitionName) {
            throw new RuntimeException("Partition Name is empty")
        }

        if (!partitionName.startsWith('/')) {
            partitionName = '/' + partitionName
        } else {
            partitionName.replaceFirst('^[/]{2,}', '/')
        }

        if (!partitionName.matches('/$')) {
            partitionName = partitionName + '/'
        } else {
            partitionName.replaceFirst('[/]{2,}$', '/')
        }

        log.info("partitionName:"+partitionName)

        String poolName = partitionName + sp.poolName.replaceAll("\\s+", "")
        if (!poolName) {
            throw new RuntimeException("Pool Name is empty")
        }

        log.info("poolName:"+poolName)

        String[] membersNames = sp.membersNames
            .split(',')
            .join("\n")
            .split(';')
            .join("\n")
            .split(/[\r\n]+/)

        def plainAddressPort = []
        for (member in membersNames) {
            def mem = member.trim()
            if (mem != "") {
                String[] addressPort = mem.split(":")
                log.info("addressPort:"+addressPort)
                plainAddressPort.add(new CommonAddressPort(
                    partitionName + addressPort[0],
                    Long.parseLong(addressPort[1])
                ))
            }
        }

        if (plainAddressPort.size() == 0) {
            throw new RuntimeException("Members list is empty")
        }

        def pool = interfaces.getLocalLBPool()

        CommonEnabledState state = (sp.setStatus == "enabled") ? CommonEnabledState.STATE_ENABLED : CommonEnabledState.STATE_DISABLED
        def plainEnabledState = []
        membersNames.each {
            plainEnabledState.add(state)
        }

        String outcome = "success"
        String summary
        String data = ""
        try {
            if (sp.setStatus == "enabled") {
                log.info("Enable members...")

                pool.set_member_session_enabled_state(
                    [poolName] as String[],
                    [plainAddressPort] as CommonAddressPort[][],
                    [plainEnabledState] as CommonEnabledState[][]
                )

                summary = "Successfully enabled"
            } else if ((sp.setStatus == "disabled") || (sp.setStatus == "force_off")) {
                log.info("Disable members...")

                pool.set_member_session_enabled_state(
                    [poolName] as String[],
                    [plainAddressPort] as CommonAddressPort[][],
                    [plainEnabledState] as CommonEnabledState[][]
                )

                if (sp.setStatus == "force_off") {
                    log.info("Force off members...")

                    pool.set_member_monitor_state(
                        [poolName] as String[],
                        [plainAddressPort] as CommonAddressPort[][],
                        [plainEnabledState] as CommonEnabledState[][]
                    )

                    summary = "Successfully forced offline"
                } else {
                    summary = "Successfully disabled"
                }
                log.info("Done")
            } else {
                throw new RuntimeException("Unknown desired status")
            }
        } catch (Throwable e) {
            e.printStackTrace()
            log.error(e.getClass())
            log.error(e.stackTrace)
            outcome = "error"
            summary = "Failed to set status: " + e.message
            log.error(summary)
        }


//        String resultPropertySheet = sp.resultPropertySheet
//        if (resultPropertySheet == "") {
//            resultPropertySheet = "/myJob/poolMemberStatus"
//            log.info("Assumed result property sheet: " + resultPropertySheet)
//        }

//        processResult(sr, outcome, summary, resultPropertySheet, data)
        processResult(sr, outcome, summary)
    }

// === step ends ===

}
