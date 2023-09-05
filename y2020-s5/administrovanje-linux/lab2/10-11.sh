#!/bin/bash

#10
fdisk /dev/sda
#...
umount /dev/sda2
e2fsck -f /dev/sda2 && resize2fs /dev/sda2

#11
fsck -n /dev/sda2
mount -a
