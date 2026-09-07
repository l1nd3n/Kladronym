MAVEN ?= mvn

.PHONY: all test build package install clean rebuild

all: build

test:
	$(MAVEN) test

build:
	$(MAVEN) package

package: build

install:
	$(MAVEN) install

clean:
	$(MAVEN) clean

rebuild: clean build
