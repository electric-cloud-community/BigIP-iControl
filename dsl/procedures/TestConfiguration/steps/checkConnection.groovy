$[/myProject/groovy/scripts/preamble.groovy.ignore]

BigIPiControl plugin = new BigIPiControl()
plugin.runStep('$[/myProcedure/name]', 'checkConnection', 'checkConnection')