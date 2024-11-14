# vwap-calculator

Java 8 application that calculates the hourly Volume Weighted Average Prices of foreign exchange trades.

## Installation

Install vwap-calculator with maven

```bash
  mvn clean
  mvn install
```

## Generate test data
Requires python and pip installations
```bash
  cd src/main/resources
  pip install pandas
  python ./generate_test_data.py
```

## Run the application with the test data
```bash
  <path_to_java_exe> -jar ~/.m2/repository/org/vwap/vwap-calculator/0.0.1/vwap-calculator-0.0.1.jar src/main/resources/random_currency_data.csv
```

## Run the application and print results (much slower)
```bash
  <path_to_java_exe> -jar ~/.m2/repository/org/vwap/vwap-calculator/0.0.1/vwap-calculator-0.0.1.jar src/main/resources/random_currency_data.csv -debug
```
