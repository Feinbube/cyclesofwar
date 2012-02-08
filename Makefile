all:
	javac -cp src:lib/loewis.jar:lib/theo.jar src/cyclesofwar/*.java src/cyclesofwar/*/*.java src/cyclesofwar/*/*/*.java src/cyclesofwar/*/*/*/*.java 

run:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.window.MainWindow

nogui:
	java -cp src:lib/loewis.jar:lib/theo.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.window.MainWindow --noGUI

