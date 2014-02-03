
.SUFFIXES: .java .class 

JCLASSPATH=-cp src:lib/loewis.jar:lib/theo.jar:lib/bcel-5.2.jar 

JSOURCES=$(shell find src/ -type f -name '*.java')
JOBJ=$(patsubst %.java,%.class,$(wildcard $(JSOURCES)))

JFLAGS=$(JCLASSPATH) -Djava.security.manager -Djava.security.policy=cow.policy -Dsun.java2d.opengl=true -server -Xincgc -Xmx3072M -Xms1024M -Xmn512M -XX:NewRatio=2 -XX:CMSFullGCsBeforeCompaction=1 -XX:SoftRefLRUPolicyMSPerMB=2048 -XX:+CMSParallelRemarkEnabled -XX:+UseParNewGC -XX:+UseAdaptiveSizePolicy -XX:+DisableExplicitGC -Xnoclassgc -oss4M -ss4M -XX:+UseFastAccessorMethods -XX:CMSInitiatingOccupancyFraction=90 -XX:+UseConcMarkSweepGC -XX:UseSSE=4 -XX:+UseCMSCompactAtFullCollection -XX:ParallelGCThreads=4 -XX:+AggressiveOpts
JCFLAGS=$(JCLASSPATH) -Xlint:unchecked -Xlint:deprecation


all: $(JOBJ)

run: all
	@java $(JFLAGS) cyclesofwar.window.MainWindow

nogui: all
	@java $(JFLAGS) cyclesofwar.console.OneOnOneTournamentConsole

clean:
	@rm -f $(JOBJ) *.time *.ooo *.lms ranking.new

demo: updatereadonly clean all run

updatereadonly:
	svn up

ranking.update:
	svn --config-option config:tunnels:ssh='ssh -i cyclesofwar' up -q; \
        svn info > revision.new

ranking: ranking.update clean all
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

ranking.new: ranking.lms ranking.ooo
	cat ranking.lms ranking.ooo > ranking.new

.PHONY:	ranking.lms ranking.ooo

ranking.lms:
	/usr/bin/time -f "%E" -o ranking.lms.time java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.LastManStandingTournamentConsole > ranking.lms

ranking.ooo:
	/usr/bin/time -f "%E" -o ranking.ooo.time java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.console.OneOnOneTournamentConsole > ranking.ooo

.java.class:
	@echo "JAVAC $<"
	@javac $(JCFLAGS) $<


