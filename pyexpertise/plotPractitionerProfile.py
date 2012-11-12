import csv
import matplotlib.pyplot as plt
from numpy import *

# For installing numpy sudo easy_install -U numpy

filename = './results/1679581102.xls'
lines = open(filename).readlines()
x=[]
y_temp=[]
y=[]

label=[]

for line in lines:
    value = line.split('\t')
    y_temp.append(int(value[2]))
    label.append(str(value[1]))


# Here trying to reduce the values to significant values that are > 1/10 of the max value
max_value = y_temp[0]
treshold = float(max_value/10)
print max_value
print treshold

i =0
curr_value=max_value

while curr_value > treshold:
    curr_value= y_temp[i]
    y.append(y_temp[i])
    i+=1


x = [i for i in range(len(y))]

fig = plt.figure()

# make a new axis on that figure. Syntax for add_subplot() is
# number of rows of subplots, number of columns, and the
# which subplot. So this says one row, one column, first
# subplot -- the simplest setup you can get.
# See later examples for more.

ax = fig.add_subplot(1,1,1)

# your data here:
#x = [1,2,3]
#y = [4,6,3]

# add a bar plot to the axis, ax.
ax.bar(x,y)

# after you're all done with plotting commands, show the plot.
plt.show()


#bins =len(x)
#plt.hist(x, facecolor='green', alpha=0.75)
#plt.xlabel('Time (ms)')
#plt.ylabel('Count')
#plt.suptitle(r'Sup title')
#plt.title(r'Title')
#plt.grid(True)
#plt.savefig(filename + '.png')
#plt.show()
#csv_reader = csv.reader(open('./results/1679581102.xls'),  delimiter='\t')
##f = open('./results/1679581102.xls')
##v = loadtxt(f, delimiter="/t",  comments="#", skiprows=1, usecols=None)

#x,y = [],[]
#csv_reader = csv.reader(open('./results/1679581102.xls'),  delimiter='\t')
##f = open('./results/1679581102.xls')
##v = loadtxt(f, delimiter="/t",  comments="#", skiprows=1, usecols=None)
#
##
#for line in csv_reader:
#    print line
#    x.append(int(line[2]))
#    y.append(str(line[1]))
#
#fig = plt.figure()
#ax = fig.add_subplot(111)
#ax.plot(y,x)
###fig.autofmt_xdate()
##
##v_hist = ravel(v)   # 'flatten' v
##fig = plt.figure()
##ax1 = fig.add_subplot(111)
##
##n, bins, patches = ax1.hist(v_hist, bins=50, normed=1, facecolor='green')
##plt.show()