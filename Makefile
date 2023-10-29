SRC_DIR := Templates
BIN_DIR := bin

all: Interpreter.class $(BIN_DIR)/MainTemplate.class

$(BIN_DIR)/MainTemplate.class: $(SRC_DIR)/MainTemplate.java
	javac -d $(BIN_DIR) $<

Interpreter.class: Interpreter.java
	javac -d $(BIN_DIR) $<

run: all
	java -cp $(BIN_DIR) MainTemplate

clean:
	rm -rf bin/*