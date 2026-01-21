# GLOOP Setup
## GLOOP lokal installieren:
mvn install:install-file -Dfile=C:\Users\moeyn\Downloads\lib\GLOOP.jar -DgroupId=com.gloop -DartifactId=gloop -Dversion=4.31 -Dpackaging=jar

mvn install:install-file -Dfile=C:\Users\moeyn\Downloads\lib\gluegen-rt-natives-windows-amd64.jar -DgroupId=org.jogamp.gluegen -DartifactId=gluegen-rt-natives-windows-amd64 -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=C:\Users\moeyn\Downloads\lib\jogl-all-natives-windows-amd64.jar -DgroupId=org.jogamp.jogl -DartifactId=jogl-all-natives-windows-amd64 -Dversion=1.0 -Dpackaging=jar

## bGLOOP lokal installieren:
mvn install:install-file -Dfile=C:\Users\moeyn\Downloads\bGLOOP.jar -DgroupId=com.bgloop -DartifactId=bgloop -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=C:\Users\moeyn\Downloads\gluegen-rt-2.3.2-natives-windows-amd64.jar -DgroupId=org.jogamp.gluegen -DartifactId=gluegen-rt-natives-windows-amd64 -Dversion=2.3.2 -Dpackaging=jar

mvn install:install-file -Dfile=C:\Users\moeyn\Downloads\jogl-all-2.3.2-natives-windows-amd64.jar -DgroupId=org.jogamp.jogl -DartifactId=jogl-all-natives-windows-amd64 -Dversion=2.3.2 -Dpackaging=jar

## In launch.json hiter launch-config für Ausführung der Main Methode:
"vmArgs": "--add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d-ALL-UNNAMED"

## Dependencies
```
<dependencies>
    <dependency>
        <groupId>com.gloop</groupId>
        <artifactId>gloop</artifactId>
        <version>4.31</version>
    </dependency>

    <dependency>
        <groupId>org.jogamp.gluegen</groupId>
        <artifactId>gluegen-rt-main</artifactId>
        <version>2.6.0</version>
    </dependency>
    <dependency>
        <groupId>org.jogamp.gluegen</groupId>
        <artifactId>gluegen-rt-natives-windows-amd64</artifactId>
        <version>1.0</version>
    </dependency>

    <dependency>
        <groupId>org.jogamp.jogl</groupId>
        <artifactId>jogl-all</artifactId>
        <version>2.6.0</version>
    </dependency>
    <dependency>
        <groupId>org.jogamp.jogl</groupId>
        <artifactId>jogl-all-natives-windows-amd64</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

## Dependencies bGLOOP
```
<dependencies>
    <dependency>
        <groupId>com.bgloop</groupId>
        <artifactId>bgloop</artifactId>
        <version>1.0</version>
    </dependency>

    <dependency>
        <groupId>org.jogamp.gluegen</groupId>
        <artifactId>gluegen-rt-main</artifactId>
        <version>2.3.2</version>
    </dependency>
    
    <dependency>
        <groupId>org.jogamp.gluegen</groupId>
        <artifactId>gluegen-rt-natives-windows-amd64</artifactId>
        <version>2.3.2</version>
    </dependency>

    <dependency>
        <groupId>org.jogamp.jogl</groupId>
        <artifactId>jogl-all-main</artifactId>
        <version>2.3.2</version>
    </dependency>
    <dependency>
        <groupId>org.jogamp.jogl</groupId>
        <artifactId>jogl-all-natives-windows-amd64</artifactId>
        <version>2.3.2</version>
    </dependency>
</dependencies>
```