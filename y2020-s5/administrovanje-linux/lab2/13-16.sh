#!/bin/bash

#13
fdisk /dev/sdb
#...

#14
pvcreate /dev/sdb1 /dev/sdb2
vgcreate group /dev/sdb1 /dev/sdb2
lvcreate -L 190M -n LVM group
mkdir /mnt/supernewdisk
mkfs.ext4 /dev/group/LVM
mount /dev/group/LVM /mnt/supernewdisk

#15
mkdir /mnt/share
mount.cifs //10.0.2.2/share /mnt/share

#16
echo //10.0.2.2/share /mnt/share cifs auto 0 0 >> /etc/fstab
