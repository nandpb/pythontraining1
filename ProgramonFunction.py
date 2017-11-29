#1.	Write a function to print the fibanocci series
#===============================================
print("fibanocci series using function")
print("=================================")

def recur_fibo(n):
   if n <= 1:
       return n
   else:
       return(recur_fibo(n-1) + recur_fibo(n-2))


n_terms = int(input("enter a number  : "))

if n_terms <= 0:
   print("Plese enter a positive integer")
else:
   print("Fibonacci sequence:")
   for i in range(n_terms):
       print(recur_fibo(i), end=' ')


print()
print("================================")





# 2.  To print the sum of two numbers
# ================================
print()
print("To print sum of two numbers")
print("================================")
def sum(x ,y):
    """sum of two number using function"""
    print('sum of ' ,x ,'+' ,y ," = " , x +y)


x= int(input("enter a number : "))
y= int(input("enter a number : "))
sum(x,y)
print()
print("============================================================")
# 3.	Write a Python based program which acts as a calculator .(Menu based)
print("Write a Python based program which acts as a calculator .(Menu based)")
print("============================================================")
def sum(x,y):
    """sum of two number using function"""
    return x+y


def sub(x,y):
    """sub of two number using function"""
    return x-y


def mul(x,y):
    """mul of two number using function"""
    return x*y


def div(x,y):
    """div of two number using function"""
    return x/y


print("select operation \n1.add \n2.sub \n3.mul \n4.div")
choice = input("enter your choice (1/2/3/4) : ")

x1= int (input("enter a number : "))
y1= int (input("enter a number : "))

if choice== '1' :
    print('sum of ',x1, '+' ,y1, " = ",sum( x1,y1))

elif choice== '2':
    print('sum of ',x1, '-', y1, " = ",sub( x1,y1))

elif choice=='3' :
    print('sum of ',x1, '*', y1, " = " ,mul( x1,y1))

elif choice=='4' :
    print('sum of ',x1, '/', y1, " = " ,div( x1,y1))

else:
    print("Invalid choice")
print()
print("===================================")

# 6.	Define a function max() that takes two numbers as arguments and returns the largest of them. Use the if-then-else construct available in Python. (It is true that Python has the max() function built in, but writing it yourself is nevertheless a good exercise.)
print("Define a function max() that takes two numbers as arguments and returns the largest of them. Use the if-then-else construct available in Python. (It is true that Python has the max() function built in, but writing it yourself is nevertheless a good exercise.)")
print("===================================")
def max(x,y):
    if x>y:
        print("max value is : ",x)
    else:
        print("max value is : ",y)



x1 =int(input("enter a number : "))
y1 = int(input("enter a number : "))
max(x1,y1 )
print()
print("========================================")


# 7 Define a function max_of_three() that takes three numbers as arguments and returns the largest of them.
print("Define a function max_of_three() that takes three numbers as arguments and returns the largest of them.")
print("========================================")
def max(num1, num2, num3):
    if (num1 >= num2) and (num1 >= num3):
        return num1
    elif (num2 >= num1) and (num2 >= num3):
        return num2
    else:
        return num3


num1 = int(input("enter a value : "))
num2 = int(input("enter a value : "))
num3 = int(input("enter a value : "))
print('The largest number is : ', max(num1, num2, num3))

print()
print("=================================")

#7 Define a function that computes the length of a given list or string.
#=============================================

print("Define a function that computes the length of a given list or string.")
print("=================================")
def length_str(str):
    count = 0
    for i in str:
        count = count+1
    return count


str = 'Length of thestring'
print("length of the string is :",length_str(str))

print()
print("=================================")
#8.	Write a function that takes a character (i.e. a string of length 1) and returns True if it is a vowel, False otherwise.
#=============================================

print("Write a function that takes a character (i.e. a string of length 1) and returns True if it is a vowel, False otherwise.")
print("=================================")

def vowel_fun(str):
    if str in ('a','e','i','o','u'):
        return "vowel"
    else:
        return "not a vowel"

srt = 'a'
print("given char ",srt,'is a ',vowel_fun(srt))
print("given char ",vowel_fun('d'))

print()
print("=================================")

#10.	Define a function sum() and a function multiply() that sums and multiplies (respectively) all the numbers in a list of numbers.
print("Define a function sum() and a function multiply() that sums and multiplies (respectively) all the numbers in a list of numbers.")
print("=================================")

def multiply(n):
    total = 1
    for i in range(0, len(n)):
        total *= n[i]
    return total


print(multiply([1,2,3,4]))



def sum_of_list(n):
    total = 0
    for i in range(0, len(n)):
        total += n[i]
    return total


print(sum_of_list([1,2,3,4]))

print()
print("=================================")

#11.	Define a function reverse() that computes the reversal of a string
print("Define a function reverse() that computes the reversal of a string")
print("=================================")

# Python code to reverse a string
# using loop

def reverse(s):
    str = ""
    for i in s:
        str = i + str
    return str

s = "I am Testing"

print ("The original string is : ",end="")
print (s)

print ("The reversed string(using loops) is : ",end="")
print (reverse(s))
