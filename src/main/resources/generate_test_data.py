import pandas as pd
import numpy as np
from datetime import datetime, timedelta
import random

# AI-generated script to programmatically generate test data in csv format. Useful for generating large sample sets of data
# to test performance of application.

# To run, first install pandas -> pip install pandas
# Then run the script -> python .\generate_test_data.py (takes a few mins, depending on the num_rows)
# By default, it will create the csv file in the same directory as the script with 1 million rows


# Parameters
num_rows = 10000000
start_time = datetime(2024, 10, 20, 10, 0, 0)
currency_pairs = [
    "USD/JPY", "EUR/GBP", "AUD/USD", "CAD/JPY", "EUR/USD",
    "GBP/USD", "EUR/AUD", "USD/CAD", "NZD/JPY", "CHF/USD",
    "GBP/JPY", "USD/CHF", "EUR/JPY", "AUD/JPY", "GBP/AUD",
    "CAD/USD", "NZD/USD", "EUR/CHF", "GBP/CHF", "AUD/NZD",
    "JPY/CHF", "EUR/NZD", "GBP/NZD", "CAD/CHF", "AUD/CHF"
]

# Generate timestamps with random increments
timestamps = [start_time]
for _ in range(num_rows - 1):
    increment = random.randint(1, 5)  # Random increment between 1 to 5 minutes
    new_time = timestamps[-1] + timedelta(seconds=increment)
    timestamps.append(new_time)

# Format timestamps in ISO 8601 Instant format
timestamps_iso = [ts.strftime('%Y-%m-%dT%H:%M:%SZ') for ts in timestamps]

# Generate random currency pairs, prices, and volumes
currency_pairs_random = [random.choice(currency_pairs) for _ in range(num_rows)]
prices = [round(random.uniform(0.5, 1.5) * 100, 2) for _ in range(num_rows)]
volumes = [random.randint(500, 3000) for _ in range(num_rows)]

# Create DataFrame
data = {
    'Timestamp': timestamps_iso,
    'Currency-pair': currency_pairs_random,
    'Price': prices,
    'Volume': volumes
}
df = pd.DataFrame(data)

# Save to CSV
df.to_csv('./random_currency_data.csv', index=False, header=True)
