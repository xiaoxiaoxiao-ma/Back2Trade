Java 回测+量化交易：支持本地 CSV 与 IBKR（可扩展更多数据源）

# Prerequisites / 前置依赖
JDK 17+
Maven
(Optional 可选) 

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
```
java -jar target/main-1.0.0-SNAPSHOT.jar
```
