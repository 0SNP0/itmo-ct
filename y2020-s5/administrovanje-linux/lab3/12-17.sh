#!/bin/bash

#12
useradd -p $(openssl passwd -crypt 87654321) u2

#13
mkdir /home/test13
cp work3.log /home/test13/work3-1.log
cp work3.log /home/test13/work3-2.log

#14
chown -R u1:u2 /home/test13
chmod 750 /home/test13
chmod 640 /home/test13/*

#15
mkdir /home/test14
chown u1:u1 /home/test14
chmod -R 755 /home/test14
chmod +t /home/test14

#16
cp /bin/nano /home/test13/nano
chown u1:u1 /home/test13/nano
chmod u+s /home/test13/nano

#17
mkdir /home/test15
touch /home/test15/secret_file
chown root:root /home/test15/secret_file
chmod a-r /home/test15
chmod +t /home/test15/secret_file
