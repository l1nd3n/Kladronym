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
import dev.l1nd3n.kladronym.LikelyKladronym;

var result = new LikelyKladronym("Саратовская обл г Саратов").find();
```

Справочники упакованы в JAR и лениво загружаются при первом вызове `find()`.
Стандартные источники кэшируют неизменяемые данные. Каждый `Aliases`
строит своё дерево при первом обращении и затем использует его повторно.

### Собственные источники

```java
import dev.l1nd3n.kladronym.*;
import dev.l1nd3n.kladronym.source.CachedSource;
import dev.l1nd3n.kladronym.text.name.NormalizedPrefixMatch;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

var abbreviations = new CachedSource<>(new SocrSource(() -> Files.newBufferedReader(Path.of("socrbase.tsv"), StandardCharsets.UTF_8)));
var kladr = new CachedSource<>(new KladrSource(() -> Files.newBufferedReader(Path.of("kladr.tsv"), StandardCharsets.UTF_8)));
var result = new LikelyKladronym(
        "Саратовская обл г Саратов",
        new NormalizedPrefixMatch(),
        abbreviations,
        kladr
).find();
```

`KladrSource` и `SocrSource` принимают `FiasSource<Reader>`: вызывающий код
задаёт открытие текста и кодировку. Каждый `load()` получает новый `Reader`
и закрывает его после чтения, в том числе при ошибке.

Конструкторы не читают файлы. Основной конструктор принимает
`FiasSource<Abbreviations>` и `FiasSource<Kladr>`; можно передать лямбды,
возвращающие подготовленные справочники. Каждый `find()` вызывает переданные
источники. Кэширование загрузки выполняет только явный `CachedSource`;
у `LikelyKladronym` собственного кэша нет.
Ошибка загрузки вызывает `IllegalStateException` с исходной причиной.
`Optional.empty()` означает, что кладроним не найден.

### Топонимы и сравнение

```java
import dev.l1nd3n.kladronym.*;
import dev.l1nd3n.kladronym.text.name.NormalizedPrefixMatch;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

var query = new Toponym("Саратов", "г.");
var candidate = new Toponym("Саратов", "город");
var match = new StandardToponymMatch(new NormalizedPrefixMatch(), new SocrSource(() -> Files.newBufferedReader(Path.of("socrbase.tsv"), StandardCharsets.UTF_8)).load());
boolean matches = query.matches(candidate, match);
```

`Toponym` хранит переданные имя и обозначение типа. `new Toponym("Саратов")`
создаёт топоним без типа. Наличие объекта не подтверждает корректность
обозначения. `ToponymMatch` — функциональный интерфейс для своей стратегии;
стандартная реализация сопоставляет имена и пересекает допустимые полные типы.
Отсутствие типа у любой стороны сохраняет прежнее сравнение только по имени.

### Отображение

```java
import dev.l1nd3n.kladronym.*;
import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

var kladr = new KladrSource(() ->
        Files.newBufferedReader(Path.of("kladr.tsv"), StandardCharsets.UTF_8)).load();
var abbreviations = new SocrSource(() -> Files.newBufferedReader(Path.of("socrbase.tsv"), StandardCharsets.UTF_8)).load();
var code = new KladrCode("64000001000");
var found = kladr.find(code).orElseThrow();

String single = new FormattedKladronym(found, abbreviations).get();
// Саратов (Город)
String hierarchy = new FormattedKladrAddress(code, kladr, abbreviations).get();
// Саратовская (Область), Саратов (Город)
```

`Toponym.toString()` выводит сохранённое обозначение без раскрытия сокращения.
Для прежнего отображения полного типа используйте `FormattedKladronym`.
Несколько полных обозначений соединяются через `/` в порядке справочника;
неизвестное обозначение остаётся исходным. Форматирование иерархии проходит
значимые уровни кода сверху вниз, пропуская нулевые позиции.
Контракт для отсутствующих записей иерархии в собственных данных пока не определён.

### Поиск внутри области

`Kladr.find(toponym, scope, match)` возвращает неизменяемый список кандидатов,
отсортированный по рангу, затем полному коду. `KladrCode.ROOT` задаёт поиск
по всему каталогу. `Kladr.find(code)` ищет точное совпадение кода и возвращает
`Optional<Kladronym>`.

Скрипт подсказок в `scripts/` остаётся исходным прототипом со старым
интерфейсом. Его адаптация не входит в этот рефакторинг.

## Сборка

```bash
make test
make build
```

Готовые JAR создаются в `target`:

- `kladronym-0.1.0-SNAPSHOT.jar` — библиотека и встроенные справочники;
- `kladronym-0.1.0-SNAPSHOT-sources.jar` — исходники для IDE.

Дополнительные цели: `make clean`, `make rebuild`, `make package` и `make install`.
