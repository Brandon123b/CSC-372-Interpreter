SRC_DIR := Templates
BIN_DIR := bin

all: $(BIN_DIR)/MainTemplate.class

$(BIN_DIR)/MainTemplate.class: $(SRC_DIR)/MainTemplate.java
	javac -d $(BIN_DIR) $<

clean:
	rm -f bin/*