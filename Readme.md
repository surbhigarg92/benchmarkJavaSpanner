# Cloud Spanner Benchmark test
## Build

mvn clean package

## Run

java -XX:-TieredCompilation -XX:CompileThreshold=1000 -jar target/over/over.jar

## Enable metrics

export SPANNER_ENABLE_BUILTIN_METRICS=true
