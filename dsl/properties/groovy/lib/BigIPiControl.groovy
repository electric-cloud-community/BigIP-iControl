import com.cloudbees.flowpdf.*
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

    /**
     * enableNode - Enable Node/Enable Node
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param partitionName (required: true)
     * @param nodeName (required: true)

     */
    def enableNode(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        EnableNodeParameters sp = EnableNodeParameters.initParameters(p)

        def nodeStub = interfaces.getLocalLBNodeAddressV2()
        def nodes = nodeStub.get_list()
        boolean found = false
        String fullNodeName = "/$sp.partitionName/$sp.nodeName"
        nodes.each {
            log.info("Found node: $it")
            if (it == fullNodeName) {
                found = true
                log.info "Node $fullNodeName exists"
            }
        }

        if (!found) {
            context.bailOut("Cannot find node $fullNodeName")
        }

        def status = nodeStub.get_session_status([fullNodeName] as String[])
        log.info "Node status: $status"

        def newState = CommonEnabledState.STATE_ENABLED as CommonEnabledState[]
        nodeStub.set_session_enabled_state([fullNodeName] as String[], newState)

        log.info "Enabled node $fullNodeName"
    }


    /**
    * disableNode - Disable Node/Disable Node
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param partitionName (required: true)
    * @param nodeName (required: true)
    
    */
    def disableNode(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        DisableNodeParameters sp = DisableNodeParameters.initParameters(p)

        def nodeStub = interfaces.getLocalLBNodeAddressV2()
        def nodes = nodeStub.get_list()
        boolean found = false
        String fullNodeName = "/$sp.partitionName/$sp.nodeName"
        nodes.each {
            log.info("Found node: $it")
            if (it == fullNodeName) {
                found = true
                log.info "Node $fullNodeName exists"
            }
        }

        if (!found) {
            context.bailOut("Cannot find node $fullNodeName")
        }

        def status = nodeStub.get_session_status([fullNodeName] as String[])
        log.info "Node status: $status"

        def newState = CommonEnabledState.STATE_DISABLED as CommonEnabledState[]
        nodeStub.set_session_enabled_state([fullNodeName] as String[], newState)

        log.info "Disabled node $fullNodeName"
    }

/**
    * listPools - List Pools/List Pools
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param partitionName (required: true)
    
    */
    def listPools(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        ListPoolsParameters sp = ListPoolsParameters.initParameters(p)
        def poolStub = interfaces.localLBPool
        poolStub.get_list().each {
            log.info "Found pool: $it"
        }
    }

/**
    * getBalancingPoolList - Get Balancing Pool List/Get Balancing Pool List
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param partitionName (required: true)
    * @param resultPropertySheet (required: true)
    
    */
    def getBalancingPoolList(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        GetBalancingPoolListParameters sp = GetBalancingPoolListParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('partitionName')
        log.info p.asMap.get('resultPropertySheet')
        

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Get Balancing Pool List has been finished")
    }

/**
    * getBalancingPool - Get Balancing Pool/Get Balancing Pool
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param partitionName (required: true)
    * @param poolName (required: true)
    * @param resultPropertySheet (required: true)
    
    */
    def getBalancingPool(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        GetBalancingPoolParameters sp = GetBalancingPoolParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('partitionName')
        log.info p.asMap.get('poolName')
        log.info p.asMap.get('resultPropertySheet')
        

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Get Balancing Pool has been finished")
    }

/**
    * deleteBalancingPool - Delete Balancing Pool/Delete Balancing Pool
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param partitionName (required: true)
    * @param poolName (required: true)
    * @param resultPropertySheet (required: true)
    
    */
    def deleteBalancingPool(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        DeleteBalancingPoolParameters sp = DeleteBalancingPoolParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('partitionName')
        log.info p.asMap.get('poolName')
        log.info p.asMap.get('resultPropertySheet')
        

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Delete Balancing Pool has been finished")
    }

/**
    * createOrUpdateBalancingPool - Create Or Update Balancing Pool/Create Or Update Balancing Pool
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param updateAction (required: true)
    * @param partitionName (required: true)
    * @param poolName (required: true)
    * @param optionalParameters (required: false)
    * @param resultPropertySheet (required: true)
    
    */
    def createOrUpdateBalancingPool(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        CreateOrUpdateBalancingPoolParameters sp = CreateOrUpdateBalancingPoolParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('updateAction')
        log.info p.asMap.get('partitionName')
        log.info p.asMap.get('poolName')
        log.info p.asMap.get('optionalParameters')
        log.info p.asMap.get('resultPropertySheet')
        

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Create Or Update Balancing Pool has been finished")
    }

/**
    * getBalancingPoolMemberList - Get Balancing Pool Member List/Get Balancing Pool Member List
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param partitionName (required: true)
    * @param poolName (required: true)
    * @param resultPropertySheet (required: true)
    
    */
    def getBalancingPoolMemberList(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        GetBalancingPoolMemberListParameters sp = GetBalancingPoolMemberListParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('partitionName')
        log.info p.asMap.get('poolName')
        log.info p.asMap.get('resultPropertySheet')
        

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Get Balancing Pool Member List has been finished")
    }

/**
    * getBalancingPoolMember - Get Balancing Pool Member/Get Balancing Pool Member
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param partitionName (required: true)
    * @param poolName (required: true)
    * @param memberName (required: true)
    * @param resultPropertySheet (required: true)
    
    */
    def getBalancingPoolMember(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        GetBalancingPoolMemberParameters sp = GetBalancingPoolMemberParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('partitionName')
        log.info p.asMap.get('poolName')
        log.info p.asMap.get('memberName')
        log.info p.asMap.get('resultPropertySheet')
        

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Get Balancing Pool Member has been finished")
    }

/**
    * changeBalancingPoolMemberStatus - Change Balancing Pool Member Status/Change Balancing Pool Member Status
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param partitionName (required: true)
    * @param poolName (required: true)
    * @param memberName (required: true)
    * @param setStatus (required: true)
    * @param resultPropertySheet (required: true)
    
    */
    def changeBalancingPoolMemberStatus(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        ChangeBalancingPoolMemberStatusParameters sp = ChangeBalancingPoolMemberStatusParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('partitionName')
        log.info p.asMap.get('poolName')
        log.info p.asMap.get('memberName')
        log.info p.asMap.get('setStatus')
        log.info p.asMap.get('resultPropertySheet')
        

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Change Balancing Pool Member Status has been finished")
    }

/**
    * deleteBalancingPoolMember - Delete Balancing Pool Member/Delete Balancing Pool Member
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param partitionName (required: true)
    * @param poolName (required: true)
    * @param memberName (required: true)
    * @param resultPropertySheet (required: true)
    
    */
    def deleteBalancingPoolMember(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        DeleteBalancingPoolMemberParameters sp = DeleteBalancingPoolMemberParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('partitionName')
        log.info p.asMap.get('poolName')
        log.info p.asMap.get('memberName')
        log.info p.asMap.get('resultPropertySheet')
        

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Delete Balancing Pool Member has been finished")
    }

/**
    * createOrUpdatePoolMember - Create Or Update Pool Member/Create Or Update Pool Member
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param updateAction (required: true)
    * @param member (required: true)
    * @param poolName (required: true)
    * @param optionalParameters (required: false)
    * @param resultPropertySheet (required: true)
    
    */
    def createOrUpdatePoolMember(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        CreateOrUpdatePoolMemberParameters sp = CreateOrUpdatePoolMemberParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('updateAction')
        log.info p.asMap.get('member')
        log.info p.asMap.get('poolName')
        log.info p.asMap.get('optionalParameters')
        log.info p.asMap.get('resultPropertySheet')
        

        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Create Or Update Pool Member has been finished")
    }

// === step ends ===

}