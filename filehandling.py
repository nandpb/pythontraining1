file1 = open("file.txt","r")

def no_line_file(file1):
    """This function is to find the total no.of line contain in the file"""
    count = 0
    for line in file1:
        count += 1
        list2 = line.split()
    return count


rows = no_line_file(file1)

file1 = open("file.txt","r")
avg = 0.0 # Average value of second column in list
total = 0 # Total value of second column in list
count = 0 # To count Number of lines in the file
for line in file1:
    count += 1
    words = line.split()
    words[1]=float(words[1])
    total += words[1]
    avg = total/count


print(avg)







