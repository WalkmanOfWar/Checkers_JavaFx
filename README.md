# Checkers JavaFX

Dwuosobowa gra w warcaby napisana w Javie z użyciem JavaFX.

## Zasady gry

- Gra toczy się na szachownicy 8×8.
- **Gracz 1** (białe pionki) zaczyna jako pierwszy i porusza się w górę planszy.
- **Gracz 2** (czerwone pionki) porusza się w dół planszy.
- Pionki poruszają się o jedno pole po skosie na wolne, ciemne pole.
- Bicie jest **obowiązkowe** — jeśli istnieje możliwość bicia, trzeba z niej skorzystać.
- Bicie wielokrotne: jeśli po biciu pionek może bić kolejny, musi kontynuować serię.
- Pionek, który dotrze do ostatniego rzędu po stronie przeciwnika, staje się **damką** — może poruszać się po skosie w obu kierunkach.
- Gracz, który zbije wszystkie pionki przeciwnika, wygrywa.

## Sterowanie

Pionki przeciąga się myszką (drag & drop) na docelowe pole.  
Jeśli ruch jest niedozwolony, pionek wraca na swoje miejsce.

## Wymagania

- **Java 18+**
- **Maven 3.8+**

## Uruchomienie

```bash
mvn clean javafx:run
```

## Struktura projektu

```
src/main/
├── java/com/example/checkers/
│   ├── CheckersApp.java   — punkt wejścia, logika gry i planszy
│   ├── Controller.java    — kontroler FXML (liczniki, timer, komunikat o zwycięstwie)
│   ├── Piece.java         — pionek z obsługą drag & drop
│   ├── Tile.java          — pole planszy
│   ├── PieceType.java     — typ pionka (RED, WHITE, REDKING, WHITEKING)
│   ├── MoveType.java      — wynik ruchu (NONE, NORMAL, CAPTURE)
│   ├── MoveResult.java    — wrapper wyniku ruchu wraz z bitym pionkiem
│   └── StopWatch.java     — stoper odmierzający czas od ostatniego ruchu
└── resources/com/example/checkers/
    ├── Board.fxml          — układ głównego okna
    ├── RedPiece.fxml       — wygląd czerwonego pionka
    ├── WhitePiece.fxml     — wygląd białego pionka
    ├── RedKingPiece.fxml   — wygląd czerwonej damki
    └── WhiteKingPiece.fxml — wygląd białej damki
```

## Technologie

| Technologia | Wersja |
|---|---|
| Java | 18 |
| JavaFX | 18-ea+6 |
| Maven | 3.8.1 |
| JUnit Jupiter | 5.8.1 |
