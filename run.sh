#!/usr/bin/env sh
java -XX:-TieredCompilation -XX:CompileThreshold=1000 $JAVA_OPTS -jar spanner-0.1.0.jar $DB_NAME_ARG $PROJECT_ID_ARG $DB_QUERY_ARG $@