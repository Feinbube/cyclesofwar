all:
	javac -cp src src/cyclesofwar/*.java src/cyclesofwar/players/frank/*.java src/cyclesofwar/players/robert/*.java src/cyclesofwar/players/training/*.java src/cyclesofwar/tournament/*.java src/cyclesofwar/window/*.java src/cyclesofwar/window/rendering/*.java

run:
	java -cp src:lib/loewis.jar -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.MainWindow
