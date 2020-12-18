# duckula3

#### 介绍
duckula可以自定义发布

#### 软件架构
软件架构说明


#### 安装教程
1.  初始化数据库 init.sql
2.  找一个目录 ,如：d:\test 目录 ，创建一个属性文件“application.properties”,按情况修改相关配置项
spring.datasource.dynamic.primary=master
spring.datasource.dynamic.strict=false
spring.datasource.dynamic.datasource.master.url=jdbc:mysql://docker.for.win.localhost:3306/duckula?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.dynamic.datasource.master.username=root
spring.datasource.dynamic.datasource.master.password=mysql
spring.datasource.dynamic.datasource.master.driver-class-name=com.mysql.jdbc.Driver
spring.cloud.refresh.refreshable=none
mybatis-plus.mapper-locations=classpath:mybatis/primary/sqlmap/*.xml
logging.level.net.wicp.tams.app.duckula.controller.dao=DEBUG
common.aws.profile.accessKey=accessKey
common.aws.profile.secretKey=secretKey
3.  运行docker
docker run -p 8085:8080    -v D:/test:/opt/userconfig   rjzjh:ops.4
4.  浏览器运行：
http://localhost:8085

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
