all:
	javac -cp src:lib/loewis.jar:lib/theo.jar src/cyclesofwar/*.java src/cyclesofwar/*/*.java src/cyclesofwar/*/*/*.java src/cyclesofwar/*/*/*/*.java 

run:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.window.MainWindow

nogui:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.Console


ranking.update:
	svn --config-option config:tunnels:ssh='ssh -i cyclesofwar' up -q

ranking:	ranking.update all ranking.new
	if cmp -s ranking ranking.new; \
	then \
		rm ranking.new; \
	else \
		mv ranking.new ranking; \
		src/mailer ranking; \
	fi

ranking.new:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.Console > ranking.new

