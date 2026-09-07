# Kladronym

Небольшая Java-библиотека для разрешения произвольного российского адреса в код КЛАДР.

Требуется Java 21.

Maven-координаты: `dev.l1nd3n:kladronym:0.1.0-SNAPSHOT`.

## Подключение

Установить текущую версию в локальный Maven-репозиторий:

```bash
make install
```

Maven:

```xml
<dependency>
    <groupId>dev.l1nd3n</groupId>
    <artifactId>kladronym</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

Gradle:

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("dev.l1nd3n:kladronym:0.1.0-SNAPSHOT")
}
```

## Использование

```java
import dev.l1nd3n.kladronym.ResolvedAddress;

var result = new ResolvedAddress("Саратовская обл г Саратов").get();
```

Справочники упакованы в JAR. Они лениво загружаются при первом вызове `get()`,
после чего один неизменяемый снимок данных используется всеми экземплярами.

## Сборка

```bash
make test
make build
```

Готовые JAR создаются в `target`:

- `kladronym-0.1.0-SNAPSHOT.jar` — библиотека и встроенные справочники;
- `kladronym-0.1.0-SNAPSHOT-sources.jar` — исходники для IDE.

Дополнительные цели: `make clean`, `make rebuild`, `make package` и `make install`.
