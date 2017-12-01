import os, sys

path = "D:\\New folder\\"
dirs = os.listdir(path)

def write_file1(write_file, content):
    file9 = open(path + write_file + ".txt", "a")
    file9.write(content)


def new_file_open(new_file):
    file9 = open(path+new_file+".txt","w")


def read_file(read_file_name):
    file9 = open(path + read_file_name+ ".txt", "r")
    print(file9.read())

print("select operation \n1.creat a new file \n2.read a file \n3.Write in file")
choice = input("enter your choice (1/2/3) : ")

if choice == '1':

    new_file_name = input("enter a file name to create : ")
    try:
        new_file_open(new_file_name)
        print("a new file is created ")
    except:
        print("some thing wrong /""try after sometime")


elif choice =='2':
    read_file_name = input("enter a file name to create : ")
    try:
        read_file(read_file_name)
    except FileNotFoundError:
        print("You Enter the file is not in the folder")

elif choice == '3':
    write_file = input("enter a file name to write contents :")
    content = input("write some content in the file ")
    write_file1(write_file,content)

else:
    print("invalid input")
