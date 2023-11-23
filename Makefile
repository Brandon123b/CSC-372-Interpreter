SRC_DIR := src
BIN_DIR := bin

ARGS=55

all: $(BIN_DIR)/Interpreter.class

# Compile the given class file (in bin)
$(BIN_DIR)/%.class: $(SRC_DIR)/%.java
	javac -sourcepath $(SRC_DIR) -d $(BIN_DIR) $<

# Interprets the file (Does not compile)
%.comp: all %.mpl
	java -cp $(BIN_DIR) Interpreter $*.mpl

# Compile any *.mpl file into a *.class file (in bin)
%.class: all %.mpl
	java -cp $(BIN_DIR) Interpreter $*.mpl
	javac -sourcepath $(SRC_DIR) -d $(BIN_DIR) $(notdir $*).java

# Run the interpreter on the given file, then run it
%: all %.class
	java -cp $(BIN_DIR) $(notdir $@) $(ARGS)

clean:
	rm -rf $(BIN_DIR)/*

# Cleans interpreted java files (Be careful!)
cleanJava:
	rm -f *.java

.PHONY: all clean