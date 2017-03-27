Dew
--
基于Spring Cloud的服务封装

### 设计理念
从企业内部框架到开源的EZ-Framework，亲历过架构从单体到分布式再到微服务的演进，
这也体现在EZ-F的各个主线版本中，EZ-F是基于Scala从零构建的，
Dew主要是对Spring Cloud做一层薄的封装，力求在不改变Spring Cloud的开发模式下做简洁明了的扩展。

### 功能

1. 全局Token认证支持
1. WebSocket网关支持
1. 集成常用服务（Redis、分布式锁等）
1. 统一返回信息格式（Resp<E>）
1. 可跟踪日志支持（请求级ID）
1. 基于Spring Data JPA的常用方式扩展
1. 整合了hystrix、config、eureka、turbine、amqp、redis、swagger2等Spring Cloud生态项目

### 运行环境

Java8以上、Redis支持

示例组件需要RabbitMQ及MySQL


### License

Under version 2.0 of the http://www.apache.org/licenses/LICENSE-2.0
