#!/bin/bash

#1
fdisk /dev/sda

#2
blkid | egrep "sda2" | egrep -o "PARTUUID=\".+\"" > ~/partuuid

#3
mkfs.ext4 -b 4096 /dev/sda2

#4
dumpe2fs -h /dev/sda2

#5
tune2fs -c 2 -i 2m /dev/sda2

# 6
mkdir /mnt/newdisk && mount /dev/sda2 /mnt/newdisk/

# 7
ln -s /mnt/newdisk/ ~/newdisk

# 8
mkdir ~/newdisk/testDir

#9
echo UUID=$(blkid | egrep "sda2" | egrep -o "\"[A-Za-z0-9\-]+\"" | head -1 | egrep -o "[A-Za-z0-9\-]+") /mnt/newdisk ext4 noexec,nodiratime,rw 0 2 >> /etc/fstab
