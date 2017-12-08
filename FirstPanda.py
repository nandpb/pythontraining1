import pandas as pd
import numpy as np

s=pd.Series()
print(s) #Empty Series of pandas

data = np.array(['a','b','c','d'])
s = pd.Series(data)
print (s)

print()
data = np.array(['a','b','c','d'])
s = pd.Series(data,index=[100,101,102,103])
print (s)


#Series from dict
#=================
print()
data = {'a' : 0., 'b' : 1., 'c' : 2.} #Series from dict
s = pd.Series(data)
print (s)

print()
data = {'a' : 0., 'b' : 1., 'c' : 2.}
s = pd.Series(data,index=['b','c','d','a'])
print (s)

#Series from Scalar
#====================
print()
s = pd.Series(5, index=[0, 1, 2, 3])
print (s)

print()
s = pd.Series([1,3,5,np.nan,6,8])
print(s)

print()
dates = pd.date_range('20130101', periods=6)
print(dates)

print()
df = pd.DataFrame(np.random.randn(6,4), index=dates, columns=list('ABCD'))
print(df)

print()
df2 = pd.DataFrame({'A': 1.,
                    'B': pd.Timestamp('20130102'),
                    'C': pd.Series(1,index=list(range(4)),dtype='float32'),
                    'D': np.array([3]*4,dtype='int32'),
                    'E': pd.Categorical(["test","train","test","train"]).copy(),
                    'F': 'foo'})
print(df2)
print()
print(df2.dtypes)

print()
print(df2.head(0))

#See the top & bottom rows of the frame
print()
print(df2.head(2))

print()
print(df2.tail(3))

#
print()
print(df2.values)

#Describe shows a quick statistic summary of your data
print()
print(df2.describe())

#Transposing your data
print()
print(df.T)

#Sorting by an axis
df2.sort_index(axis=1,ascending=False)

#Sorting by values
print("/n",df2.sort_values(by='B'))
print("Hi")

