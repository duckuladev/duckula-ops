#!/bin/bash
set -e

is_empty_dir(){ 
    return `ls -A $1|wc -w`
}

## 用户配置的配置项
rm -fr /opt/userconfig/lost+found
if is_empty_dir /opt/userconfig/
then
	echo "/opt/userconfig/ 不存在用户定义的卷"    
else
   echo "/opt/userconfig/ 存在用户定义的卷"
   # 防止把找不到目录错No such file or directory
   mkdir -p /data/duckula-data/conf/
   #复制子目录，但是文件复制过去却是空的
   cp -rf /opt/userconfig/*   /data/duckula-data/conf
   #复制文件，为了修复上面命令的不足
   cp -f  /opt/userconfig/*.properties   /data/duckula-data/conf
fi

set -- duckula "$@"

if [ "$1" = 'duckula' -a "$(id -u)" = '0' ]; then
	# Change the ownership of user-mutable directories to elasticsearch
	for path in \
		/data/duckula-data \
		/opt/duckula \
	; do
		chown -R duckula:duckula "$path"
	done
	
	set -- su-exec "$@"
	#exec su-exec duckula "$BASH_SOURCE" "$@"
fi

exec "$@"
