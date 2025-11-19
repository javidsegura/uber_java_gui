# TeeTime Makefile

.PHONY: run clean compile test
.DEFAULT_GOAL := run

run:
	mvn clean javafx:run

compile:
	mvn compile

clean:
	mvn clean

test:
	mvn test

install:
	mvn install

help:
	@echo "TeeTime - Campus Ride Sharing"
	@echo ""
	@echo "Available commands:"
	@echo "  make run     - Clean and run the JavaFX application"
	@echo "  make compile - Compile the project"
	@echo "  make clean   - Clean build files"
	@echo "  make test    - Run unit tests"
	@echo "  make install - Install dependencies"

