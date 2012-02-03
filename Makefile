all:
	javac -cp src src/cyclesofwar/*.java src/cyclesofwar/players/*.java

run:
	java -cp src cyclesofwar.MainWindow
