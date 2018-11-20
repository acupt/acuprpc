
+ 远程服务调用
  + 支持grpc
  + 支持thrift
  + 支持自扩展 
+ 服务注册/发现(基于eureka)
+ 调用异常节点自动重试

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