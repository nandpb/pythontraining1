import pandas as pd
df = pd.read_csv("D:\\Users\\lnc\Downloads\\pythontraining-namelist-pct.csv",header=None)
print(df)
print(df[2:4])
print()
print(df[2:5][0:])
print()
df1 = pd.read_csv("pythontraining-namelist-pct.csv")
print(df1)

