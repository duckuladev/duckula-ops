---
title: duckula3/install
date: 2020-12-12 22:35:39
tags:
---

# (一)　前言
&emsp;&emsp;互联网体系架构具有可控性差、 数据量大、 架构复杂等特点，错综复杂的各业务模块需要解耦，各异构数据需要同步，双活/多活的容灾方案需要高实时性 等，在各种场合都需要一套可靠的数据实时推送方案。mysql已成为互联网项目存储的主力，围绕着它的各外围模块急需实时地获取它的数据，binlog监听是解决此实时同步问题的不二之选。duckula就是为了满足此需求而设计与开发出来的中间件。

# (二）duckula3简介
&emsp;&emsp;duckula3是一款binlog监听的中间件，但它不仅仅是一款binlog监听的中间件，它可以允许用户自主使用binlog数据。可以有多变的使用方式，可以做为中间件独立布署，也可以嵌入到项目中启动布署，可以嵌入到springboot项目，也可以嵌入到纯java项目，它也有专用于flink的source，可以使flink直连数据库，拿到它的实时数据。由于它良好的插件设计，可以支持任何用户自定义的监听开发。

## duckula的由来

&emsp;&emsp; 大概在2016年的时候，我在***中间件团队时，当时公司使用了一套叫“精卫”的中间，由于阿里不对它开源，只是使用权，在支撑业务线运行的过程中，发现了较多的问题，如采用回退12秒HA的模式带来的数据重复推送、界面全是手工录入，耗时也易出错，配置非常复杂（特别是分库分表情况往往要1、2个小时才能搞定），HA时启动失败需要重置位点导致的丢数据没有补全方案等方方面面。  由于没有源码，只能干瞪眼。所以那时就下决心解决这个问题。经过一段时间的选型，在github找了几个binlog解释器，就一条：性能不达标，一票否决了，那时也有关注canel，性能是OK，但是它的设计初是解决两地数据复制问题（当时的版本），而与我要找的是精卫的替代方案，它是要满足数据的分类推送（如哪个表推送哪个消息的topic中）。就有了自研的念头。于是就有了zorro中间件。

&emsp;&emsp; 离开这家公司后来到了一家央企，无事可干，实在无聊，zorro的设计还是感觉不满意，代码也不能拿出来，但知识是在脑子里的，于是重写binlog中间件，这就是duckula的雏形，纯粹写着玩了。

&emsp;&emsp;等我写完后，离开了央企，到了下一家互联网公司任技术专家，刚好碰到一个“世纪难题”，一张mysql表已3KW了，没有做分库分表，且开发它代码的人都离职了，没人懂里面的细节了，查询条件非常复杂，索引建了无数，但没用，系统越来越慢，而且每天都有5W多的数据新增，而且要保留很长时间的数据（基本上是全部），不能删除，而且实时性要求很高（基本上前一个状态改了马上要能查到，好做下一步操作）， 照这样发展，系统肯定得废啊。于是我想到了duckula，但它只有mysql的binlog到kafka这一个模块，并没有监听kafka插入到es模块，而且3KW数据的全量导入ES也是个问题，总不能产生3KW的binlog事件发kafka吧。kafka也得爆（配置不怎么样）,于是加紧开发了 这2个模块，加上前面已有的binlog监听模块，这3个模块就完成了从mysql到ES同步的闭环。完美，这就是duckula1初步完成。

&emsp;&emsp;接下来公司要上paas系统了，我们把中间件全部迁上k8s，把spring cloud这一套也全部上k8s，那么duckula1的SSH连接主机（就像cachcloud）管理binlog监听任务的这一套必须得变了，把原来的有“服务器”变为k8s的“无服务器”布署模式，而且要试着兼容之前的运行模式，这次改造超出了我当初的预期，花费了大量时间，要考虑的东西实在太多了，好在duckula2版本最终出来了，更进一步的是，把它SaaS化，也就是支持让业务线的同学直接跟据需求配置监听任务，这样就没有基础架构组啥事了。且它后面开发了支持"普罗米修斯"的监控。支持binlog的行列过滤等功能也开发了，于是duckula2变得越来越“重”，它表现在布署非常麻烦，配置项就有几百个。

&emsp;&emsp;应该说我们的使用场景太丰富了（toB行业），  接下来又发生了现在的duckula2无法解决的场景----属地化部署，有一个项目有属地化的需求，给我们的机器资源有限，但是duckula2又对zookeeper、kafka等中间件有依赖，duckula2自身也需要布署一个ops(控制台)，不可能有这么多的资源让我布署了，于是跟据需求，我把duckula2又进行了一次重构，形成了一个轻量级的binlog监听工具包叫ducklite，它可以以jar包的形式被项目直接引用，分布式锁从zookeeper改为了mysql锁，表的“列名”，“位点”等元数据存储在mysql里不用zookeeper， 这次真的轻了，只依赖mysql就行了，但它只是临时方案，离完整的binlog解决方案还是有些差距。

&emsp;&emsp;在现实使用ducklite中又发现状况，没有全量方案，最开始上线没多少数据，可以全量修改产生全量的binlog确实没问题，等上了生产后产生了大量数据就玩不转了，还有老客户的旧版本迁移到新系统后，旧系统的数据如何全量推ES？还有，由于ducklite是嵌入到应用系统的，监听任务的开发由业务人员完成的，由于开发人员的素质差异，差的监听执行器往往会影响ducklite的监听任务运行，所以他们经常抱怨“太慢了”，“怎么又死了”，于是下决心在ducklite的基础上开发可以独立布署的新版本:duckula3。

## 功能概述

　　duckula的重头戏是binlog监听，但它又不止于binlog监听，它的作用范围有了进一步的延伸，如：把监听的消息实时入ES，通过配置完成mysql到ES的快速全量导入等，这样就有了一个从mysql到ES的闭环。有了duckula后，业务开发人员就不用理会mysql到es的同步问题，只需跟据业务需求开发ＥＳ的查询语句就可以了。
### 功能模块
&emsp;&emsp;duckula3可以分为三块：

ops：     调度中心，配置中心，不强依赖，只是方便配置和管理执行器。

执行器： binlog监听执行器/kafka consumer执行器/全量导入执行器，这块是duckula的重心。

插件：     支持执行器往各种不同的存储中间件进行导入，现在支持的有ES5、ES6、ES7、kafka、mysql、log(测试用)、http 等
### 配置项
配置项是为了更好的支持执行器的配置，一切都是为了重用，我们把一个执行器需要的配置项分为6大块

- 布署环境  &emsp;&emsp;执行器将会在哪个环境里运行，现在支持 k8s/docker/contos7，duckula会使用它的配置来布署执行器

- 实例&emsp;&emsp;&emsp;&emsp;binlog监听执行器需要监听哪个数据库，dump（全量导入）需要扫哪个数据库  等配置信息

- 存储中间件 &emsp;&emsp;所有的执行器需要把数据发往哪个地方存储或通知，现在支持ES/mysql等

- 版本&emsp;&emsp;&emsp;&emsp;执行器使用了duckula3的哪个版本

- 检查点&emsp;&emsp;&emsp;&emsp;检查点（checkpoint）是binlog监听执行器用来做HA（高可用），支持 内存/h2db/mysql 三种类型，只有mysql可以做跨机器的HA，h2db只能在本机做HA，内存不能做HA，它需要借助宿主应用做HA，如flink本身就有checkpoint机制，那么duckula3实现的flink source就需要使用内存类型的检查点，duckula3会把元数据借助flink的checkpoint来存储，从而完成HA的。

### 监控
执行器都有通过jmx暴露普罗米修斯接口。

# (三）安装

## 独立安装

### 独立jar包启动

duckula3设计的初衷就是简单，布署简单，配置简单，使用简单

- 执行初始化SQL脚本，创建数据库使用下面的脚本 ：duckula-ops\devops\sql\duckula.sql

    https://gitee.com/rjzjh/duckula-ops/blob/master/devops/sql/duckula.sql
    
- 下载执行jar包：

     https://cloud.189.cn/t/ZfAn2uRvaQZz  
     
- 、、跟据实距情况修改下面配置，并保存到用户home目录的duckula-ops.properties文件

  ```properties
  spring.datasource.dynamic.primary=master
  spring.datasource.dynamic.strict=false
  spring.datasource.dynamic.datasource.master.url=jdbc:mysql://docker.for.win.localhost:3306/duckula?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
  spring.datasource.dynamic.datasource.master.username=root
  spring.datasource.dynamic.datasource.master.password=mysql
  spring.datasource.dynamic.datasource.master.driver-class-name=com.mysql.jdbc.Driver
  spring.cloud.refresh.refreshable=none
  mybatis-plus.mapper-locations=classpath:mybatis/primary/sqlmap/*.xml
  logging.level.net.wicp.tams.app.duckula.controller.dao=DEBUG
  ```

- 启动ops。   

  简单启动   : java -jar -server duckula-ops.jar

### 在docker中启动:

前面的文件配置和初始化步骤不变，然后执行：

docker run -d -it --name=duckula-ops -v C:/Users/Administrator:/home/duckula  -p 8080:8080  registry.cn-hangzhou.aliyuncs.com/rjzjh/duckula:ops.3.0.0

停止任务：docker stop duckula-ops

删除任务：docker rm duckula-ops

### 在k8s里启动

要在k8s里启动，首先需要安装helm3，具体安装步骤，k8s的config相关配置， 请自行google。

1. 找到源码的这个目录： duckula-ops\devops\k8s\duckula3-ops

2. cmd到这个目录下，跟据实际情况修改文件：values.yaml，再执行命令：

   helm install  -name rjzjh .\duckula3-ops\  --namespace default

3. 查看相关日志：

   <div align=center><img src="https://rjzjh.gitee.io/images/duckula3/k8s-view-log.png" width = "600" height = "400" /></div>


4. 本地访问地址

&emsp;&emsp;设置本地转发：kubectl port-forward po/rjzjh-duckula-ops-58f87bf5b9-pl7k2   8080:8080  -n   default

&emsp;&emsp;访问地址：http://localhost:8080/



###  在java编译工具中启动(ide,eclipse等)

1. 下载源码，安装好jdk和maven，编译成功

2. 修改配置文件

&emsp;&emsp;如上创建好配置文件duckula-ops.properties，你可以在bootstrap.yml修改配置文件的位置。

3. 启动springboot的启动类
    &emsp;&emsp;启动类为：net.wicp.tams.duckula.ops.App

4. 打docker镜像

   先通过maven打包或本地安装jar： maven clean install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true

   切换到target目录下执行：docker build -t   registry.cn-hangzhou.aliyuncs.com/rjzjh/duckula:duckula-ops.3.0.0  .

   上面的group：registry.cn-hangzhou.aliyuncs.com/rjzjh/duckula跟据自己的情况进行替换。

5. 推送镜像到image私服

   docker login -u XXXXX 

   docker push  registry.cn-hangzhou.aliyuncs.com/rjzjh/duckula:duckula-ops.3.0.0  .

## 嵌入式使用
duckula3可以做为一个jar包的方式存在于一个项目的依赖中，如同使用其它的工具类一样，只要引入相关的jar包，很简单的做些配置就可以在宿主项目中跑起来。duckula3已上传到maven中央库，可以在任何使用maven的项目是直接使用，maven坐标为：

```xml
	<dependency>
			<groupId>net.wicp.tams</groupId>
			<artifactId>common-binlog-alone</artifactId>
            <version>最大版本</version>
	</dependency>
```

下面的示例项目在oschina上：https://gitee.com/rjzjh/duckula3-dev-demo

###  纯java（非spring boot）

项目结构如下：


<div align=center><img src="https://rjzjh.gitee.io/images/duckula3/java-struct.png" width = "600" height = "400" /></div>


主文件入口：

```java
package net.wicp.tams.duckula.demo.cuslistener;

import java.io.IOException;
import java.util.Properties;
import net.wicp.tams.common.Conf;
import net.wicp.tams.common.apiext.IOUtil;
import net.wicp.tams.common.binlog.alone.BusiAssit;

public class Main {
	public static void main(String[] args) throws IOException {
		Properties props = IOUtil.fileToProperties("/common-binlog-chk-mysql.properties", Main.class);
		Conf.overProp(props);
		BusiAssit.startListenerForConfig();
		System.in.read();
	}
}
```

配置文件：

```properties
#插件可以看common-es-plugin的test
common.binlog.alone.binlog.global.busiPluginDir=null
#默认的监听服务配置,如果有多套，复制此配置并修改global.conf为conf.XXXX(见下面示例)
common.binlog.alone.binlog.global.conf.host= localhost
common.binlog.alone.binlog.global.conf.port = 3307
common.binlog.alone.binlog.global.conf.username = root
common.binlog.alone.binlog.global.conf.password = mysql
common.binlog.alone.binlog.global.conf.listener = net.wicp.tams.duckula.demo.cuslistener.TestListener
common.binlog.alone.binlog.global.conf.rds = false
#设置了groupId表示需要做分布式锁，同一个groupId+ip就是一个集群分布式锁
common.binlog.alone.binlog.global.conf.groupId = 20000
#默认使用CheckPointH2db，还提供了CheckPointMemory的chk实现,CheckPointMysql多进程的HA机制
common.binlog.alone.binlog.global.chk=net.wicp.tams.common.binlog.alone.checkpoint.CheckPointMysql
#如果其它与配置相同就可以不配置
common.binlog.alone.binlog.global.chk.mysql.defaultdb=tams
#cur表示从当前最新位点启动，last表示从记录的最后位点启动，pos表示从上面设置的gtids启动
common.binlog.alone.binlog.global.conf.haType = last
#为了做HA,必须填一个，否则不能区分lastPo
common.binlog.alone.binlog.conf.abc.clientId=20000
#监听规则,这是2组，就是test库的user_info和user_addr表被监听，这两张表有binlog时才会给到上面配置的listener
common.binlog.alone.binlog.conf.abc.rule=test`user_info`{}&test`user_addr`{}
```

由于使用的是具有HA和分布式锁的最完备的CheckPointMysql，所以配置项会多一些，而且需要在监听项上创建一个tams数据库（binlog监听时需要存储元数据），监听的帐号对这个库具有建表权限（通过表锁来实现分布式锁），上面这些配置都有说明。

实际上可以使用的配置项会非常多，而且会不断的增加，如果不熟悉的使用者肯定搞晕，所以会有duckula-ops帮助配置和布署。

示例项目见：duckula3-dev-demo-cuslistener

### spring boot

  在spring boot项目中嵌入式使用duckula3，除了上面提及的的maven坐标依赖外，还需引入另一个jar包：

```xml
<dependency>
	<groupId>net.wicp.tams</groupId>
	<artifactId>common-spring-autoconfig</artifactId>
    <version>最后一个版本</version>
</dependency>
```

在启动类需要中入enable注解：

<div align=center><img src="https://rjzjh.gitee.io/images/duckula3/springboot-main.png" width = "600" height = "400" /></div>


springconfpres配置是指像你数据库连接地址的配置，跟据实际情况修改。

接下来就创建一个监听器，使用注解的方式说明：

```java
//chk默认为CheckPointH2db,如果不变无需配置。conf：随意，不能重复  rule：监听配置，监听duckula库sys_user表
@BinlogListener(conf = "abc", chk = "net.wicp.tams.common.binlog.alone.checkpoint.CheckPointMemory", rule = "duckula`sys_user`{}")
public class TestListener extends AbsBinlogListener {
	@Override
	public void doBusiTrue(Rule rule, DuckulaEvent duckulaEvent, boolean isSplit) {
		String username = DuckulaAssit.getValue(duckulaEvent, "username", 0);
		System.out.println("username=" + username);
	}

	// 当修改表时触发，如加字段
	@Override
	public Result doAlterTableCallBack(Rule rule, ColHis colHis, String sql) {
		return Result.getSuc();
	}

	@Override
	public void doInit(Rule rule, int index) {

	}
}
```

示例项目见：duckula3-dev-demo-springboot

### flink source

duckula3也可以在flink里使用,已完成用于监听binlog的source开发。

用法见博文：https://blog.csdn.net/rjzjh/article/details/100864012

## 访问

  到了见证成果的时候了：

<div align=center><img src="https://rjzjh.gitee.io/images/duckula3/home.png" width = "600" height = "400" /></div>

布署页：
<div align=center><img src="https://rjzjh.gitee.io/images/duckula3/deploy.png" width = "600" height = "400" /></div>







