#!/bin/bash

############################################################################
# @desc: 
#	- 1. create user;
#	- 2. the user password;
#   - 3. the hosts;
# @author: zhoujunhui
# @email: rjzjh@163.com
# @time:  2018-09-30
# @安装好jdk,安装好yum install -y perl
###########################################################################

set -o nounset
#set -o errexit

# check if the user exists
checkExist() {
	local num=`cat /etc/passwd | grep -w $1 | wc -l`
		 
	#cat /etc/passwd | grep -q "$1"
	if [[ $num == 1 ]]; then
		echo "delete existed user: $1."
		userdel -r "$1"
		createUser "$1" "$2"
		return 0
	else
		createUser "$1" "$2"
		return 0
	fi
	
}
#check if the docker installed
checkDocker() {
	local num=`command -v docker|grep -w docker |wc -l`		 
	if [[ $num == 1 ]]; then
		echo "the docker is installed。"
		gpasswd -a "$1" docker
		systemctl restart docker
		#todo run the data images for initcd 
	fi
	return 0
}



# create the user
createUser() {
    #yum install -y perl
	# create a user 
	useradd -m -d /home/$1 -s /bin/bash $1 -p  $(perl -e 'print crypt($ARGV[0], "password")' $2)
	 
	# give the user a password
	#passwd $1

	# add the user to sudoers
	#	echo "$1	ALL=(ALL)   ALL" >> /etc/sudoers

	#  Maximum number of days between password change
	chage -M 9999 $1
	echo "OK: create user: $1 done"

}

# create defautl dirs and authorize
init() {
    #url="/usr/lib/jvm/jdk1.8/bin/java"
    #echo ${url: 1:0-8}	
    java_path=$(which java)
    jdkhome=${java_path: 1:0-8}
    echo "the jdkhome:${jdkhome}"
	#setting environment variables note: $JAVA_HOME not support
	echo -e  "JAVA_HOME=${jdkhome}\nDUCKULA3_HOME=/opt/duckula\nDUCKULA3_DATA=/data/duckula-data\nPATH=$PATH:${jdkhome}/bin\nexport PATH JAVA_HOME DUCKULA3_HOME DUCKULA3_DATA" >  /etc/profile.d/duckula.sh
	#source /etc/profile  
	echo "the env added: $1 done"
	if [ "$hosts" != "" ]; then
	   echo -e  "${hosts}" >> /etc/hosts
	   echo "the hosts is not null"
	fi
    
    for path in \
		 /opt/duckula \
		 /opt/duckula-history \
		 /data/duckula-data \
		 /data/duckula-data/history \
		 /data/duckula-data/conf \
		 /data/duckula-data/logs  \
		 /data/duckula-data/logs/consumer  \
		 /data/duckula-data/logs/dump  \
		 /data/duckula-data/logs/gc  \
		 /data/duckula-data/logs/metrics  \
		 /data/duckula-data/logs/task  \
	; do \
		mkdir -p "$path"; \
        chown -R "$1:$1" "$path"; \
	done; 
	echo "OK: init: $1 done"
}

username=$1
password=$2
hosts=""
if [ $# -ge 3 ]; then
   hosts=$3
fi
checkExist "${username}" "${password}"
init "${username}"
checkDocker "${username}"
