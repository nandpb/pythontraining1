"""
try:
    file1 = open("file.txt","r")
except (FileNotFoundError):
    print("file not found ")

def no_line_file(file1):
    #This function is to find the total no.of line contain in the file
    count = 0
    for line in file1:
        count += 1
        list2 = line.split()
    return count
    
rows = no_line_file(file1)
"""

avg = 0.0 # Average value of second column in list
total = 0 # Total value of second column in list
count = 0 # To count Number of lines in the file

try:
    file1 = open("file.txt","r")

except FileNotFoundError:
    print("File not found")

for line in file1:

    words = line.split()
    count += 1

    try:
        words[1]=float(words[1])
        total += words[1]
        avg = total / count

    except (ValueError, TypeError):
        print("could not convert String to float")
    except (IndexError):
        print("Index Error")
    except ArithmeticError:
        print("file is empty")



print(avg)







