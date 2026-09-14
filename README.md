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

Справочники упакованы в JAR. Они лениво загружаются при первом вызове `get()`,
после чего один неизменяемый снимок данных используется всеми экземплярами.

## Подсказки по началу имени

`PossibleKladronyms` получает имя, необязательный тип и область поиска:

```java
import dev.l1nd3n.kladronym.kladr.KladrCode;
import dev.l1nd3n.kladronym.preprocess.Toponym;
import dev.l1nd3n.kladronym.PossibleKladronyms;
import dev.l1nd3n.kladronym.BundledData;

var city = BundledData.SOCRBASE.get().abbreviations().find("г").orElseThrow();
var candidates = new PossibleKladronyms(
        new Toponym("Ба", city),
        new KladrCode("61000000000"),
        BundledData.KLADR.get()
).get();

candidates.

forEach(candidate ->
        System.out.

println(candidate.toponym() +"  "+candidate.

code())
        );
```

Пустое имя `new Toponym("", city)` возвращает все подходящие города внутри
области поиска. `new Toponym("Ба")` ищет без ограничения типа,
а `KladrCode.ROOT` задаёт поиск по всему каталогу.

Подсказки допускают неполное слово, игнорируют регистр и различие `ё`/`е`.
Результат — неизменяемый список, упорядоченный сначала по позиции кода
от крупной к мелкой, затем по полному коду. Условия поиска передаются уже
разобранными. Для проверки подсказок на вводимой адресной строке есть
отдельная утилита ниже.

## Проверка подсказок в терминале

Нужны Java 21+, Maven и терминал Linux/macOS с `stty`.
Скрипт запуска сначала собирает библиотеку, затем выполняет `scripts/Suggest.java`:

```bash
./scripts/suggest
```

Можно сразу задать начальную строку:

```bash
./scripts/suggest 'Ростовская область, г.'
```

Подсказки обновляются после каждого изменения строки, без Enter.
Для примера выше текущий справочник возвращает 23 города.
После добавления `Ба` остаётся Батайск.

- `←` / `→`, Home / End — перемещение курсора.
- Backspace / Delete — удаление символов.
- `↑` / `↓`, Page Up / Page Down — прокрутка всех результатов.
- Ctrl+U — очистка строки.
- Ctrl+L — обновление размеров после изменения окна терминала.
- Ctrl+C или Ctrl+D — выход с восстановлением настроек терминала.

Полный список для одной строки, в том числе для использования в конвейере:

```bash
./scripts/suggest --once 'Ростовская область, г. Ба'
```

Если библиотека уже собрана, Java-файл можно запустить напрямую:

```bash
java --class-path target/kladronym-0.1.0-SNAPSHOT.jar scripts/Suggest.java
```

Запятые между частями адреса необязательны: `Ростовская обл г.` и
`Ростовская обл, г.` дают одинаковые подсказки городов. Часть до последней
запятой разрешается через `LikelyKladronym` и задаёт область поиска.
Без запятой граница определяется по полному имени из справочника:
распознанная часть ограничивает поиск последующей. Тип может стоять до
или после имени: `г. Ба` и `Ба город` дают одинаковые подсказки.
Утилита показывает кладронимы
встроенного справочника, без улиц и домов.

Проверка утилиты, включая посимвольный ввод в псевдотерминале,
прокрутку и восстановление настроек после Ctrl+C и SIGTERM:

```bash
python3 scripts/test_suggest.py
```

## Сборка

```bash
make test
make build
```

Готовые JAR создаются в `target`:

- `kladronym-0.1.0-SNAPSHOT.jar` — библиотека и встроенные справочники;
- `kladronym-0.1.0-SNAPSHOT-sources.jar` — исходники для IDE.

Дополнительные цели: `make clean`, `make rebuild`, `make package` и `make install`.
