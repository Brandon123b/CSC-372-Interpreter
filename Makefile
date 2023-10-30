SRC_DIR := src
BIN_DIR := bin

all: bin/MainTemplate.class bin/Interpreter.class
	
# Generic rule for compiling any java file given its path in bin
$(BIN_DIR)/%.class: $(SRC_DIR)/%.java
	javac -sourcepath $(SRC_DIR) -d $(BIN_DIR) $<

# Compile and run any class file under in the src directory
%: bin/%.class
	java -cp $(BIN_DIR) $@

test: test.java
	javac -sourcepath $(SRC_DIR) -d $(BIN_DIR) $<
	java -cp $(BIN_DIR) test

clean:
	rm -rf bin/*