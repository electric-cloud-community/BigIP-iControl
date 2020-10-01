$[/myProject/groovy/scripts/preamble.groovy.ignore]

BigIPiControl plugin = new BigIPiControl()
plugin.runStep('Get Balancing Pool Member List', 'Get Balancing Pool Member List', 'getBalancingPoolMemberList')