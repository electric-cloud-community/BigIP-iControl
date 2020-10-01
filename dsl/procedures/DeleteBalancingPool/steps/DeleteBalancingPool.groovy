$[/myProject/groovy/scripts/preamble.groovy.ignore]

BigIPiControl plugin = new BigIPiControl()
plugin.runStep('Delete Balancing Pool', 'Delete Balancing Pool', 'deleteBalancingPool')