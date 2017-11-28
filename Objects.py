a=496
print(id(a))
b=1729
print(id(b))
b=a
print(id(b))
print(id(a)==id(b))
print(a is b)