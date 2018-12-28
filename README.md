
+ 服务注册/发现(基于eureka)
+ 远程服务调用
  + 支持grpc
  + 支持thrift
  + 支持http（基于HttpServer）
  + 支持扩展其它调用方式
+ 调用异常/下线节点自动重试
+ 自定义request filter
+ 服务监控和管理（acuprpc-spring-boot-starter-actuator，基于filter实现）
+ 网关支持

项目模块

```
- acuprpc
    - acuprpc-core                          服务提供者(server) / 服务调用者(client)
    - acuprpc-protocol                      远程调用具体实现，使用时选一种即可
        - acuprpc-protocol-grpc             基于gRPC的远程调用
        - acuprpc-protocol-http             基于http的远程调用，使用Java内置HttpServer实现
        - acuprpc-protocol-thrift           基于thrift的远程调用
    - acuprpc-spring-boot-starter           启动模块，完成server端的服务管理和client端的动态代理等
    - acuprpc-spring-boot-starter-actuator  (扩展)服务管理
    - acuprpc-spring-boot-starter-gateway   (扩展)网关
```

## 快速开始

### 服务提供方 service-a

新建springboot项目service-a，包含两个模块service-a-api,service-a-server。

pom.xml
```xml
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>1.5.3.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.acupt</groupId>
            <artifactId>service-a-api</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.acupt</groupId>
            <artifactId>acuprpc-spring-boot-starter</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

application.properties
```properties
spring.application.name=service-a
server.port=8881
#
#rpc服务对外开放端口
acuprpc.port=9991
#
#命名空间，一般为环境
acuprpc.appGroup=local
#
#服务注册/发现中心地址
acuprpc.discoveryServiceUrl=http://127.0.0.1:8761/eureka/
#
#通信层实现，默认使用grpc，也可配置为thrift或自定义，以下两个配置需要同时修改
#acuprpc.rpcServerClass=com.acupt.acuprpc.protocol.thrift.ThriftServer
#acuprpc.rpcClientClass=com.acupt.acuprpc.protocol.thrift.ThriftClient
```

在service-a-api中添加一个接口
```java
public interface HiService {

    String hi(String name);

    String hello(String name);
}
```


在service-a-server中实现接口
```java
@Rpc//声明提供rpc服务
@Service
public class HiServiceImpl implements HiService {

    @Value("${acuprpc.port}")
    private int port;

    public String hi(String name) {
        return "hi," + name + ",i from " + port;
    }

    public String hello(String name) {
        return "hello," + name + ",i from " + port;
    }
}

```

### 服务调用方 service-b


新建springboot项目service-b，包含两个模块service-b-api(非必须),service-b-server。

pom.xml，引入service-a-api
```xml
    <dependencyManagement>
        <dependencies>
            <dependency>
                <!-- Import dependency management from Spring Boot -->
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>1.5.3.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.acupt</groupId>
            <artifactId>service-a-api</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.acupt</groupId>
            <artifactId>acuprpc-spring-boot-starter</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

application.properties
```properties
spring.application.name=service-b
server.port=8885
acuprpc.port=9995
acuprpc.appGroup=local
acuprpc.discoveryServiceUrl=http://127.0.0.1:8761/eureka/
```

启动类中

```java
@SpringBootApplication
@RestController
@Configuration
public class ApplicationB {

    @Resource
    private HiService hiService;

    public static void main(String[] args) {
        SpringApplication.run(ApplicationB.class, args);
    }

    @RequestMapping("/hi")
    public String hi(String name) {
        return hiService.hi(name);
    }

    @RequestMapping("/hello")
    public String hello(String name) {
        return hiService.hello(name);
    }

    @Bean
    public HiService hiService(RpcServiceConsumer rpcServiceConsumer) {
        return rpcServiceConsumer.create("service-a", HiService.class);
    }
}
```

### 服务中心eureka

此模块没做开发，直接使用spring-cloud-starter-eureka-server。

```xml
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!--conf server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka-server</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Dalston.RC1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

```java

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EurekaServerApplication.class);
        springApplication.run(args);
    }
}
```

搞定，调用service-b的http接口
http://localhost:8885/hello?name=tom

最终会请求到service-a，然后输出结果。

## filter

原理和用法类似 javax.servlet.Filter

```java
package com.acupt.service.a.filter;

import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;
import com.acupt.acuprpc.server.filter.RpcFilter;
import com.acupt.acuprpc.server.filter.RpcFilterChain;

/**
 * @author liujie
 */
public class RequestFilter implements RpcFilter {

    @Override
    public void doFilter(RpcRequest request, RpcResponse response, RpcFilterChain filterChain) {
        System.out.println("RequestFilter in");
        filterChain.doFilter(request, response);
        System.out.println("RequestFilter end");
    }
}

```

```java
@Bean
public RequestFilter requestFilter(RpcServer rpcServer) {
        return rpcServer.addFilter(new RequestFilter());
}
```

## 管理和监控

基于spring-boot-starter-actuator模块提供的endpoint特性，可以用http接口与rpc服务交互。

引入模块

```xml
<dependency>
    <groupId>com.acupt</groupId>
    <artifactId>acuprpc-spring-boot-starter-actuator</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

配置

```properties
# 此模块下的endpoint是否数据敏感，false时可直接访问，默认true
acuprpc.endpoints.sensitive=false

# 此模块下endpoint请求白名单，不设置则全部ip都可以访问
acuprpc.endpoints.ipWhiteList=127.0.0.1,123.123.123.123
```

### 服务端执行请求统计

请求：/rpcstat

返回

```json
{
    "counts": [
        {
            "key": "service-a:com.acupt.service.a.api.HiService#hello", // app:service#method
            "received": 6, // 已接收请求
            "invoking": 0, // 处理中的请求
            "success": 6, // 处理成功的请求
            "failed": 0 // 处理失败的请求
        }
    ],
    "serving": false //是否有正在处理的请求
}
```

### 管理

+ /rpc/status 服务状态，http status为200时为正常状态，503为下线状态
+ /rpc/offline 服务下线，所有rpc请求返回NOT_AVAILABLE，客户端会重新寻找其他节点
+ /rpc/online 服务上线


## 网关支持

引入acuprpc-spring-boot-starter-gateway模块即可得到一个不能再简单的网关。

原理是一个controller

```java
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * 动态调用rpc服务的关键，在acuprpc-spring-boot-starter中已经生成，可以随时引用
     */
    private RpcClientManager rpcClientManager;

    public ApiController(RpcClientManager rpcClientManager) {
        this.rpcClientManager = rpcClientManager;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public Object invoke(@RequestBody RpcRequestDTO requestDTO) {
        RpcServiceInfo serviceInfo = new RpcServiceInfo(requestDTO.getApp(), requestDTO.getService());
        RpcClient client = rpcClientManager.lookup(serviceInfo);//获取一个可以提供所需服务的连接
        RpcRequest request = new RpcRequest(requestDTO.getApp(), requestDTO.getService(), requestDTO.getMethod());
        if (requestDTO.getParameters() != null) {
            Map<String, String> map = new HashMap<>();
            requestDTO.getParameters().forEach((k, v) -> map.put(k, JsonUtil.toJson(v)));
            request.setNamedParameter(map);
        }
        return client.invoke(request);//调用服务获得返回的json字符串
    }
}
```

### 使用方法

引入依赖

```xml
<dependency>
    <groupId>com.acupt</groupId>
    <artifactId>acuprpc-spring-boot-starter-gateway</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

+ post
+ http://localhost:8080/api
+ application/json

```json
{
  "app": "service-a",
  "service": "com.acupt.service.a.api.HiService",
  "method": "hello",
  "parameters": {
    "name": "liu"
  }
}
```