#!/usr/bin/env bash
mvn clean install
java -cp target/Autocompletion-1.0-SNAPSHOT.jar:target/deps/* Main
