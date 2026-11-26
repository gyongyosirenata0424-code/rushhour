# Rush Hour – Kétjátékos Konzolos Játék (Java 21)

Programozási technológiák beadandó feladat: kétjátékos (Játékos vs. AI) **Rush Hour** logikai társasjáték konzolos megvalósítása Java 21-ben, Maven build rendszerrel, H2 SQL adatbázissal és átfogó egységtesztekkel.

## A játék lényege és szabályai
* A tábla mérete konfigurációs fájlból töltődik be (`src/main/resources/palyarenata.txt`).
* Felül **`a..f`** oszlopazonosítók, bal oldalon **`1..6`** sorszámok segítik a tájékozódást.
* **Játékos (`A`):** Célja, hogy az **`A`** jelű vízszintes céljárművet kicsúsztassa a saját kijáratán (a 3. sor jobb szélén).
* **Gép / AI (`Z`):** Célja, hogy a **`Z`** jelű vízszintes céljárművet előbb juttassa ki a saját kijáratán (a 4. sor jobb szélén).
* A járművek csak a saját tájolásuknak megfelelő irányban mozoghatnak, és nem ugorhatnak át más járműveket.

## Használható parancsok
* **Lépés rövid formában:** `<jármű> <lépésszám>` (pl. `A 1` jobbra 1-et, `B -2` felfelé/balra 2-t)
* **Lépés iránynévvel:** `<jármű> <UP|DOWN|LEFT|RIGHT> <lépésszám>` (pl. `A RIGHT 1`)
* **`save`**: Aktuális játékállás és lépésszám mentése fájlba (`mentes.txt`)
* **`scores`** (vagy `toplista`): Dicsőséglista lekérdezése a beágyazott H2 SQL adatbázisból
* **`history`**: Játékesemények megtekintése
* **`exit`**: Kilépés a játékból

## Alkalmazott technológiák és tervezési minták
* **Java 21**, **Maven**, **JUnit 5**, **Mockito**, **JaCoCo** (80%+ lefedettség), **Checkstyle**, **SLF4J + Logback** (`rushhour.log`)
* **H2 SQL Database (JDBC):** `PreparedStatement` alapú dicsőséglista-kezelés (`JdbcHighScoreRepository`)
* **Tervezési minták (GoF):**
    * **Strategy:** `Player` interfész, `HumanPlayer`, `AiPlayer`
    * **Command:** `Command` interfész, `ExitCommand`, `SaveCommand`, `ScoresCommand`, `HistoryCommand`
    * **Observer:** `GameObserver`, `HistoryLoggerObserver`
    * **Singleton:** `CommandHistorySingleton`
    * **Builder:** `Vehicle.Builder`
* **Tiszta kód és SOLID:** Külön `MoveValidator` (Single Responsibility), megváltoztathatatlan (*immutable*) `Board` és `Vehicle` osztályok, Java `record` (`LoadedState`), valamint Stream API alapú lépésgenerálás.

## Fordítás, tesztelés és futtatás

```bash
# Tesztek, Checkstyle és JaCoCo ellenőrzés + futtatható JAR készítése
mvn clean verify

# Alkalmazás indítása
java -jar target/rushhour-1.0-SNAPSHOT-jar-with-dependencies.jar
```
