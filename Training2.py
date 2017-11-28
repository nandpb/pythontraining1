#Dictionary
#================

dict = {}
dict['one'] = "This is one"
dict[2]     = "This is two"

tinydict = {'name': 'john','code':6734, 'dept': 'sales'}


print (dict['one'])       # Prints value for 'one' key
print (dict[2])           # Prints value for 2 key
print (tinydict)          # Prints complete dictionary
print (tinydict.keys())   # Prints all the keys
print (tinydict.values()) # Prints all the values


dict = {'Name': 'Zara', 'Age': 7, 'Class': 'First'}
del dict['Name']; # remove entry with key 'Name'
dict.clear();     # remove all entries in dict
del dict ;        # delete entire dictionary

#empty dictionary
my_dict = {}

# dictionary with integer keys
my_dict = {1: 'apple', 2: 'ball'}
print(my_dict)

# dictionary with mixed keys
my_dict = {'name': 'John', 1: [2, 4, 3]}

# using dict()
my_dict = dict({1: 'apple', 2: 'ball'})

# from sequence having each item as a pair
my_dict = dict([(1, 'apple'), (2, 'ball')])

# accessing elements from dictionary
my_dict = {'name': 'Jack', 'age': 26}

#output: jack
print(my_dict['name'])

#output: 26
print(my_dict.get('age'))

# Trying to access keys which doesn't exist throws error
# my_dict.get('address')
#my_dict['address']

# change or add elements in dictionary
my_dict ={'name': 'Jack', 'age':26}

#update value
my_dict['age']=27

# Output: {'age': 27, 'name': 'Jack'}
print(my_dict)

# add item
my_dict['address']='Downtown'

# Output: {'address': 'Downtown', 'age': 27, 'name': 'Jack'}
print(my_dict)



# delete or remove elements in dict
squares={1:1,2:4,3:9,4:16,5:25}
# remove a particular item
# Output: 16

print(squares.pop(4))

# Output: {1: 1, 2: 4, 3: 9, 5: 25}
print(squares)


# remove an arbitrary item
# Output: (1, 1)
print(squares.popitem())

# Output: {2: 4, 3: 9, 5: 25}
print(squares)


# delete a particular item
#del squares[5]     KeyError

# Output: {2: 4, 3: 9}
print(squares)

#updation
stocks={'empid':891,'name':'nanda','sub':455}
print(stocks)
stocks.update({'empid':23,'name':'nandakumar'})
print(stocks)


# remove all items
squares.clear()

# Output: {}
print(squares)

# delete the dictionary itself
del squares

# Throws Error
# print(squares) Name Error

# dictionary methods
marks = {}.fromkeys(['Math', 'English', 'Science'], 0)

# Output: {'English': 0, 'Math': 0, 'Science': 0}
print(marks)

for item in marks.items():
    print(item)

# Output: ['English', 'Math', 'Science']
list(sorted(marks.keys()))

# membership test

squares = {1: 1, 3: 9, 5: 25, 7: 49, 9: 81}

# Output: True
print(1 in squares)

# Output: True
print(2 not in squares)

# membership tests for key only not value
# Output: False
print(49 in squares)

# iteration through dictionary
squares = {1: 1, 3: 9, 5: 25, 7: 49, 9: 81}
for i in squares:
    print(squares[i])

    # built in functions


    squares = {1: 1, 3: 9, 5: 25, 7: 49, 9: 81}

    # Output: 5
    print(len(squares))

    # Output: [1, 3, 5, 7, 9]
    print(sorted(squares))

names_and_ages = [('Alice', 32), ('Bob', 48), ('charlie', 32)]
d = dict(names_and_ages)
print(d)


