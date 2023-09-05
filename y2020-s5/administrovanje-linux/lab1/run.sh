#!/bin/bash
TEST=$HOME"/test"
LIST=$TEST"/list"
LINKS=$TEST"/links"
LIST_HLINK=$LINKS"/list_hlink"
LIST_SLINK=$LINKS"/list_slink"
LIST1=$LIST"1"
LIST_CONF=$HOME"/list_conf"
LIST_D=$HOME"/list_d"
LIST_CONF_D=$HOME"/list_conf_d"
SUB=$TEST"/.sub"
MANTXT=$HOME"/man.txt"
MANDIR=$TEST"/man.dir"
NEWMAN=$MANDIR"/man.txt"
PATCH=$HOME"/man.txt.patch"
echo \#1
mkdir $TEST
echo \#2
ls -al /etc > $LIST
echo \#3
ls -al /etc | egrep -c ^d >> $LIST
ls -a /etc | egrep -c "^\." >> $LIST
echo \#4
mkdir $LINKS
echo \#5
ln $LIST $LIST_HLINK
echo \#6
ln -s $LIST $LIST_SLINK
echo \#7
ls -l $LIST_HLINK | egrep -o  "[0-9]+" | head -1
ls -l $LIST | egrep -o  "[0-9]+" | head -1
ls -l $LIST_SLINK | egrep -o  "[0-9]+" | head -1
echo \#8
wc -l $LIST >> $LIST_HLINK
echo \#9
cmp $LIST_SLINK $LIST_HLINK && echo "YES"
echo \#10
mv $LIST $LIST1
echo \#11
cmp $LIST_SLINK $LIST_HLINK && echo "YES"
echo \#12
ln -d $LINKS "~/links"
echo \#13
ls -R /etc | egrep ".*\.conf$" > $LIST_CONF
echo \#14
find /etc -type d -name "*.d" > $LIST_D
echo \#15
cat $LIST_CONF > $LIST_CONF_D
cat $LIST_D >> $LIST_CONF_D
echo \#16
mkdir $SUB
echo \#17
cp $LIST_CONF_D $SUB
echo \#18
cp -b $LIST_CONF_D $SUB
echo \#19
ls -aR $TEST
echo \#20
man man > $MANTXT
echo \#21
split -b 1K $MANTXT $MANTXT"."
echo \#22
mkdir $MANDIR
echo \#23
for x in $MANTXT".*"; do mv $x $MANDIR; done
echo \#24
for x in $MANDIR"/man.txt.*"; do cat $x >> $NEWMAN; done
echo \#25
cmp $MANTXT $NEWMAN && echo "YES"
echo \#26
sed  -i '1i '$(cat </dev/urandom | head -c 5) $MANTXT
for i in $(seq 5); do echo $(cat </dev/urandom | head -c 5) >> $MANTXT; done
echo \#27
diff -u $NEWMAN $MANTXT > $PATCH
echo \#28
mv $PATCH $MANDIR
echo \#29
patch $NEWMAN $MANDIR"/man.txt.patch"
echo \#30
cmp $MANTXT $NEWMAN && echo "YES"
