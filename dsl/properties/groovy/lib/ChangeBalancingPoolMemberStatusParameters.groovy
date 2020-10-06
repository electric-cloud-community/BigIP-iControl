
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
    * Label: Force enable/disable, type: checkbox
    */
    boolean force
    /**
    * Label: Wait For Existing Connections, type: checkbox
    */
    boolean doWait
    /**
    * Label: Sleep Interval, type: entry
    */
    String sleepInterval

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
        def force = sp.getParameter('force').value == "true"
        parameters.force = force
        def doWait = sp.getParameter('doWait').value == "true"
        parameters.doWait = doWait
        def sleepInterval = sp.getParameter('sleepInterval').value
        parameters.sleepInterval = sleepInterval

        return parameters
    }
}
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== Parameters ends, checksum: d48d43e3162f5841a481b1b36bb6389f ===