all:
	javac -cp src:lib/loewis.jar:lib/theo.jar:lib/bcel-5.2.jar src/cyclesofwar/*.java src/cyclesofwar/*/*.java src/cyclesofwar/*/*/*.java src/cyclesofwar/*/*/*/*.java 

run:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.window.MainWindow -Dsun.java2d.opengl=true

nogui:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.Console


clean:
	rm -f src/cyclesofwar/*.class src/cyclesofwar/*/*.class src/cyclesofwar/*/*/*.class src/cyclesofwar/*/*/*/*.class *.time *.ooo *.lms ranking.new


demo: updatereadonly clean all run

updatereadonly:
	svn up

ranking.update:
	svn --config-option config:tunnels:ssh='ssh -i cyclesofwar' up -q; \
        svn info > revision.new

ranking:	ranking.update clean all
	if cmp -s revision revision.new; \
	then \
		rm revision.new; \
	else \
		mv revision.new revision; \
                make ranking.new; \
                if cmp -s ranking ranking.new; \
                then \
                   rm ranking.new; \
                else \
                   mv ranking.new ranking; \
		   src/mailer; \
                fi \
	fi

ranking.new:	ranking.lms ranking.ooo
	cat ranking.lms ranking.ooo > ranking.new

.PHONY:	ranking.lms ranking.ooo

ranking.lms:
	/usr/bin/time -f "%E" -o ranking.lms.time java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.LastManStandingTournamentConsole > ranking.lms

ranking.ooo:
	/usr/bin/time -f "%E" -o ranking.ooo.time java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.OneOnOneTournamentConsole > ranking.ooo

