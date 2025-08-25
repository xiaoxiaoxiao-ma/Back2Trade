Java 回测+量化交易：支持本地 CSV 与 IBKR（可扩展更多数据源）

# Prerequisites / 前置依赖
JDK 17+
Maven
(Optional 可选) 

从官网下载IBKR Trader Workstation  
Download IBKR Trader Workstation from offical website  
`https://www.interactivebrokers.com/en/trading/tws.php#tws-software`

Install IBKR SDK to local Maven / 将 IBKR SDK 安装到本地 Maven 仓库
```
mvn install:install-file \
  -Dfile=lib/TwsApi.jar \
  -DgroupId=com.ibkr \
  -DartifactId=twsapi \
  -Dversion=10.23 \
  -Dpackaging=jar
```

# Build / 构建
```
  mvn -DskipTests package
```

# Run / 运行
打开Trader Workstation
登录账户
打开File -> Global Configuration -> API -> Settings -> Enable ActiveX and Socket Clients
确定Socket port为7496

```
java -jar target/main-1.0.0-SNAPSHOT.jar
```
