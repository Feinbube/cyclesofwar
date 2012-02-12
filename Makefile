all:
	javac -cp src:lib/loewis.jar:lib/theo.jar src/cyclesofwar/*.java src/cyclesofwar/*/*.java src/cyclesofwar/*/*/*.java src/cyclesofwar/*/*/*/*.java 

run:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.window.MainWindow

nogui:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.Console


clean:
	rm -f src/cyclesofwar/*.class src/cyclesofwar/*/*.class src/cyclesofwar/*/*/*.class src/cyclesofwar/*/*/*/*.class


ranking.update:
	svn --config-option config:tunnels:ssh='ssh -i cyclesofwar' up -q

ranking:	ranking.update clean all ranking.new
	if cmp -s ranking ranking.new; \
	then \
		rm ranking.new; \
	else \
		mv ranking.new ranking; \
		src/mailer; \
	fi

ranking.new:	ranking.lms ranking.ooo
	cat ranking.lms ranking.ooo > ranking.new

.PHONY:	ranking.lms ranking.ooo

ranking.lms:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.LastManStandingTournamentConsole > ranking.lms

ranking.ooo:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.OneOnOneTournamentConsole > ranking.ooo

