#!/bin/bash

#1
cat /etc/passwd | awk -F: '{print "user " $1 " has id " $3}' > ~/work3.log

#2
chage -l root | head -1 >> work3.log

#3
cut -d: -f1 < /etc/group | tr -s '\n' ',' >> work3.log

#4
echo "Be careful!" > /etc/skel/readme.txt

#5
useradd -p $(openssl passwd -crypt 12345678) u1

#6
groupadd g1

#7
usermod -a -G g1 u1

#8
id u1 | cut -d ' ' -f1,3 >> work3.log

#9
usermod -a -G g1 snp

#10
egrep "^g1:" /etc/group | cut -d: -f4 >> work3.log

#11
usermod -s /usr/bin/mc u1

mkhomedir_helper u1
