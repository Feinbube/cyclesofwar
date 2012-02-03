all:
	javac -cp src src/cyclesofwar/*.java src/cyclesofwar/players/*.java

run:
	java -cp src -Djava.security.manager -Djava.security.policy=cow.policy cyclesofwar.MainWindow
