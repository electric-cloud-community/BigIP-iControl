$[/myProject/groovy/scripts/preamble.groovy.ignore]

BigIPiControl plugin = new BigIPiControl()
plugin.runStep('Get Balancing Pool List', 'Get Balancing Pool List', 'getBalancingPoolList')