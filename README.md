# SWT 개발을 위한 Maven 개발환경 구성

## Overview
* 본 예제는 SWT 개발을 위한 적절한 수준의 Maven 개발환경을 제안하고 있습니다.
* OS 환경별 프로파일을 제공하고 있고 해당 프로파일에 맞춰 executable jar로 빌드 할 수 있도록 제공합니다.
* macOS의 실행시 이슈에 대응하여 실행환경에 대한 방안도 제공합니다.

## 1. pom.xml

### 1.1. profile

SWT의 Dependency는 각 OS별로 나눠져 있습니다.
(http://search.maven.org/#search%7Cga%7C1%7Corg.eclipse.swt)

따라서, 다음과 같이 프로파일로 분리하여 빌드시 해당 OS에 맞는 dependency를 참조하도록 설정합니다.

```xml
<profiles>
    <profile>
        <id>macos_64</id>
        <properties>
            <swt.artifact>org.eclipse.swt.cocoa.macosx.x86_64</swt.artifact>
            <swt.osdepvmoption>-XstartOnFirstThread</swt.osdepvmoption>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    <profile>
        <id>macos</id>
        <properties>
            <swt.artifact>org.eclipse.swt.cocoa.macosx</swt.artifact>
            <swt.osdepvmoption>-XstartOnFirstThread</swt.osdepvmoption>
        </properties>
    </profile>
    <profile>
        <id>win32_64</id>
        <properties>
            <swt.artifact>org.eclipse.swt.win32.win32.x86_64</swt.artifact>
            <swt.osdepvmoption></swt.osdepvmoption>
        </properties>
    </profile>
    <profile>
        <id>win32</id>
        <properties>
            <swt.artifact>org.eclipse.swt.win32.win32.x86</swt.artifact>
            <swt.osdepvmoption></swt.osdepvmoption>
        </properties>
    </profile>
    <profile>
        <id>linux64</id>
        <properties>
            <swt.artifact>org.eclipse.swt.gtk.linux.x86_64</swt.artifact>
            <swt.osdepvmoption></swt.osdepvmoption>
        </properties>
    </profile>
    <profile>
        <id>linux</id>
        <properties>
            <swt.artifact>org.eclipse.swt.gtk.linux.x86</swt.artifact>
            <swt.osdepvmoption></swt.osdepvmoption>
        </properties>
    </profile>
</profiles>
```

macOS 빌드환경인 경우 실행시 `Invalid Thread Access` 오류가 발생합니다. 해당문제를 해결하기 위해 실행시 `-XstartOnFirstThread` VM Argument를 전달해야합니다.
해당 argument를  `${swt.osdepvmoption}`에 담아 실행시 사용합니다.

다른 OS의 artifact가 필요하신 분들은 Maven Central을 참조바랍니다.
(http://www.eclipse.org/swt/faq.php#swtawtosxmore)

### 1.2. dependencies

dependency는 profile로 선택된 `${swt.artifact}`를 참조하도록 구성합니다

```xml
<dependencies>
    <dependency>
        <groupId>org.eclipse.swt</groupId>
        <artifactId>${swt.artifact}</artifactId>
        <version>${swt.version}</version>
    </dependency>
</dependencies>
```

### 1.3. plugins for executable jar

executable jar를 생성하기 위해 다음과 같이 플러그인을 사용합니다.
* executable jar를 생성하기 위해 Manifest를 구성합니다.
* 참조 라이브러리를 모두 포함한 executable jar를 추가로 생성하기 위해 assembly plugin을 사용합니다.

```xml
<plugin>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <archive>
            <manifest>
                <mainClass>${swt.mainclass}</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>

<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <archive>
            <manifest>
                <mainClass>${swt.mainclass}</mainClass>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
</plugin>
```

### 1.4. plugins for execute jar

예제를 실행해 보기위해 exec 플러그인을 사용합니다.

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>exec</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <executable>java</executable>
        <arguments>
            <argument>${swt.osdepvmoption}</argument>
            <argument>-jar</argument>
            <argument>${project.build.directory}/${project.artifactId}-${project.version}-jar-with-dependencies.jar</argument>
        </arguments>
    </configuration>
</plugin>
```

## 2. Build

아래와 같이 프로파일을 사용하여 OS환경에 맞게 빌드합니다.

### 2.1. macOS 64bit

```
mvn clean package -P macos_64
```

### 2.2. Windows 64bit

```
mvn clean package -P win32_64
```

## 3. Run

### 3.1. macOS 64bit

```
mvn exec:exec -P macos_64
```

실행결과는 다음과 같습니다.

![ImageTest](https://cloud.githubusercontent.com/assets/5626425/16642732/aa06c768-4448-11e6-9f59-5fa983df832d.png)
