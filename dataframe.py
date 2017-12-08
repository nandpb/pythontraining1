import numpy as np
import pandas as pd
#Create DataFrames
#==================
print("===========")
raw_data = {'first_name': ['Jason', 'Molly', 'Tina', 'Jake', 'Amy'],
        'last_name': ['Miller', 'Jacobson', ".", 'Milner', 'Cooze'],
        'age': [42, 52, 36, 24, 73],
        'preTestScore': [4, 24, 31, ".", "."],
        'postTestScore': ["25,000", "94,000", 57, 62, 70]}

df = pd.DataFrame(raw_data, columns = ['first_name', 'last_name', 'age', 'preTestScore', 'postTestScore'])
print("Dataframe")
print("================")
print(df)


#Save dataframe as csv in the working director
#============================================
df.to_csv("example.csv")


#Load a csv
#============
df = pd.read_csv("example.csv")
print()

#Load a csv with no headers
#============================
df = pd.read_csv('example.csv', header=None)
print("Load a csv with no headers")
print("==========================")
print(df)

print()
#Load a csv while specifying column names
#==========================================
print("Load a csv while specifying column names")
print("==========================================")
df = pd.read_csv('example.csv', names=['UID', 'First Name', 'Last Name', 'Age', 'Pre-Test Score', 'Post-Test Score'])
print(df)

print()
#Load a csv with setting the index column to UID
#===================================================
print("Load a csv with setting the index column to UID")
print("==============================================")
df = pd.read_csv('example.csv', index_col='UID', names=['UID', 'First Name', 'Last Name', 'Age', 'Pre-Test Score', 'Post-Test Score'])
print(df)
print()

#Load a csv while setting the index columns to First Name and Last Name
#==========================================================================

print("Load a csv while setting the index columns to First Name and Last Name")
print("==========================================================================")
df = pd.read_csv('example.csv', index_col=['First Name', 'Last Name'], names=['UID', 'First Name', 'Last Name', 'Age', 'Pre-Test Score', 'Post-Test Score'])
print(df)
print()


#Load a csv while specifying "." as missing values
#==================================================
print("Load a csv while specifying \".\" as missing values")
print("==================================================")
df = pd.read_csv('example.csv', na_values=['.'])
print(pd.isnull(df))


#Load a csv while specifying "." and "NA" as missing values in the Last Name column and "." as missing values in Pre-Test Score column
#==================================================
print("Load a csv while specifying \".\" and \"NA\" as missing values in the Last Name column and \".\" as missing values in Pre-Test Score column")
print("==================================================")
sentinels = {'Last Name': ['.', 'NA'], 'Pre-Test Score': ['.']}
df = pd.read_csv('example.csv', na_values=sentinels)
print(df)
print()

#Load a csv while skipping the top 3 rows
#============================================
print("Load a csv while skipping the top 3 rows")
print("=============================================")
df = pd.read_csv('example.csv', na_values=sentinels, skiprows=3)
print(df)
print()

#Load a csv while interpreting "," in strings around numbers as thousands seperators
#====================================================================================
print("Load a csv while interpreting "," in strings around numbers as thousands seperators")
print("====================================================================================")
df = pd.read_csv('example.csv', thousands=',')
print(df)

