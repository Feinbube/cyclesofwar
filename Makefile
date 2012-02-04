all:
	javac -cp src src/cyclesofwar/*.java src/cyclesofwar/players/*.java src/cyclesofwar/players/frank/*.java src/cyclesofwar/players/robert/*.java src/cyclesofwar/players/training/*.java

run:
	java -cp src:lib/loewis.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.MainWindow
