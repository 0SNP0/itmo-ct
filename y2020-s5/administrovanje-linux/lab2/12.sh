#!/bin/bash

fdisk /dev/sda
mke2fs -b 4096 -O journal_dev /dev/sda3
umount /dev/sda2
# tune2fs -O ^has_journal /dev/sda2
# tune2fs -j -J device=/dev/sda3 /dev/sda2
tune2fs -J location=/dev/sda3 /dev/sda2
mount -a
