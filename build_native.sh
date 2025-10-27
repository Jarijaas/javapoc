#!/bin/sh

g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux org_example_CsvDriver.cpp -o org_example_CsvDriver.o
g++ -shared -fPIC -o libpoc.so org_example_CsvDriver.o -lc