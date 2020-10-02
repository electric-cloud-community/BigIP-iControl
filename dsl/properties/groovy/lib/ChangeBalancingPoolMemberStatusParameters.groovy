
// DO NOT EDIT THIS BLOCK BELOW=== Parameters starts ===
// PLEASE DO NOT EDIT THIS FILE

import com.cloudbees.flowpdf.StepParameters

class ChangeBalancingPoolMemberStatusParameters {
    /**
    * Label: , type: entry
    */
    String partitionName
    /**
    * Label: Pool Name, type: entry
    */
    String poolName
    /**
    * Label: Members Names, type: textarea
    */
    String membersNames
    /**
    * Label: Status, type: select
    */
    String setStatus
    /**
    * Label: Wait For Existing Connections, type: checkbox
    */
    boolean doWait
    /**
    * Label: Sleep Interval, type: entry
    */
    String sleepInterval
    /**
    * Label: Result Property Sheet, type: entry
    */
    String resultPropertySheet

    static ChangeBalancingPoolMemberStatusParameters initParameters(StepParameters sp) {
        ChangeBalancingPoolMemberStatusParameters parameters = new ChangeBalancingPoolMemberStatusParameters()

        def partitionName = sp.getRequiredParameter('partitionName').value
        parameters.partitionName = partitionName
        def poolName = sp.getRequiredParameter('poolName').value
        parameters.poolName = poolName
        def membersNames = sp.getRequiredParameter('membersNames').value
        parameters.membersNames = membersNames
        def setStatus = sp.getRequiredParameter('setStatus').value
        parameters.setStatus = setStatus
        def doWait = sp.getParameter('doWait').value == "true"
        parameters.doWait = doWait
        def sleepInterval = sp.getParameter('sleepInterval').value
        parameters.sleepInterval = sleepInterval
        def resultPropertySheet = sp.getRequiredParameter('resultPropertySheet').value
        parameters.resultPropertySheet = resultPropertySheet

        return parameters
    }
}
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== Parameters ends, checksum: 20212e632fe5374fb4e1f6a482c550b0 ===
