#!/bin/bash
#dnf install epel-release

#1
yum groupinstall "Development tools" -y

#2
dnf install boost-devel boost ncurses-devel ncurses -y
tar -zxf /ext/lab4/bastet-0.43.tgz
cd bastet-0.43
make
echo -e "install:\n\tcp \$(PROGNAME) /usr/bin/\n\tchmod +x /usr/bin/\$(PROGNAME)" >> Makefile
make install
cd ..

#3
dnf list installed > ~/task3.log

#4
dnf deplist gcc > ~/task4_1.log
rpm -q --whatrequires libgcc > ~/task4_2.log

#5
mkdir ~/localrepo
cp /ext/lab4/checkinstall-1.6.2-3.el6.1.x86_64.rpm ~/localrepo
dnf install createrepo -y
createrepo localrepo/
echo -e "[localrepo]\nname=localrepo\nbaseurl=file:///root/localrepo/\nenabled=1\ngpgcheck=0" >> /etc/yum.repos.d/localrepo.repo

#6
dnf repolist all > ~/task6.log

#7
for X in /etc/yum.repos.d/*; do mv ${X} "${X}non"; done
mv /etc/yum.repos.d/localrepo.reponon /etc/yum.repos.d/localrepo.repo
dnf install checkinstall -y

mv /etc/yum.repos.d/oracle-linux-ol9.reponon /etc/yum.repos.d/oracle-linux-ol9.repo
mv /etc/yum.repos.d/oracle-epel-ol9.reponon /etc/yum.repos.d/oracle-epel-ol9.repo
mv /etc/yum.repos.d/virt-ol9.reponon /etc/yum.repos.d/virt-ol9.repo
mv /etc/yum.repos.d/uek-ol9.reponon /etc/yum.repos.d/uek-ol9.repo

#8
cp /ext/lab4/fortunes-ru_1.52-2_all.deb .
dnf config-manager --set-enabled ol9_codeready_builder
dnf install alien -y
alien --to-rpm fortunes-ru_1.52-2_all.deb
rpm -ihv fortunes-ru-1.52-3.noarch.rpm

#9
dnf download nano
dnf install rpmrebuild -y
rpmrebuild -enp nano-5.6.1-5.el9.x86_64.rpm
#%post
#mv /usr/bin/nano /usr/bin/newnano
dnf remove nano -y
rpm -i rpmbuild/RPMS/x86_64/nano-5.6.1-5.el9.x86_64.rpm

# wget https://www.nano-editor.org/dist/v7/nano-7.1.tar.xz
# tar -xf nano-7.1.tar.xz
# ./nano-7.1/configure --prefix=/usr/ --program-prefix=new


