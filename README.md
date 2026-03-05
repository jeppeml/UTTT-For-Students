# UTTT-For-Students
This is an implementation of the Ultimate Tic Tac Toe game. The game is written in Java using JavaFX and FontAwesomeFX by Jens Deters.

## Getting it to work in IntelliJ
1. Open the project as a Maven project. Dependencies (including JavaFX) are resolved automatically via `pom.xml`.
2. Run `Launcher` (in `src/dk/easv/Launcher.java`) — click the green play arrow next to its `main` method.

Any JDK 21+ works (no special JavaFX distribution needed).

## The Game

On the start screen you choose player types (human or AI) and select bots from the dropdown. The bot move delay slider controls how fast bot-vs-bot games play out.

![Start screen](/start-screen.png)

During gameplay, the board highlights playable sections in gold. Players place moves as diamonds (green, player 0) or trash cans (red, player 1). Won macro-board sections show a large icon for the winning player.

![Gameplay mid-game](/gameplay.png)

When a player wins, the result is shown as an overlay on the board.

![Win screen](/win-screen.png)

## Game rules
Here is a nice explanation of the rules of the game https://www.thegamegal.com/2018/09/01/ultimate-tic-tac-toe/

## Bots
The game can be played as either human or bot, and any combination can be used: human-human, human-bot, bot-human or bot-bot.

The bots must follow the IBot interface in the BLL package. The game uses reflection for loading class files from the Bots folder under BLL. To implement your own bot, create a class that implements IBot and place it in the `dk.easv.bll.bot` package.

When the game starts it creates a list of the bot names in the project root folder (`bots.txt`). This is for usage with online tournament tools.

The bots provided with the game are simple examples — see `src/dk/easv/bll/bot/README.md` for descriptions.

### REST Bot (TeacherBotRESTv2)
A REST client bot that plays against a remote server hosting stronger MCTS bots. The REST server is part of the UTTT challenge at SEA Business Academy and is not publicly available. If you'd like access to test your bot against the server, feel free to reach out.

Change `SERVER_URL` in `TeacherBotRESTv2.java` to point to your server. Change `DEFAULT_SLUG` to select the bot strength:
- `teacher` — Basic MCTS (strength 2.0)
- `r16linrave` — MCTS + RAVE (strength 3.0)
- `r19bitreuse` — Bitboard + tree reuse + RAVE (strength 3.5)
- `r20multiroll` — R19 + multi-rollout (strength 4.0)

Network/connection errors are clearly reported instead of showing as "false move".

## Game Features

### Bot Move Delay
Configurable delay (0–2500ms, default 1000ms) for bot-vs-bot games so you can watch the game progress.

### Simulation
Run bulk bot-vs-bot games across all CPU cores. Games are split evenly: half with bot A as player 0, half swapped. The simulation slider goes from 1 to 200 games.

### Stats Window
The stats window is split into two resizable sections:

**Current Games** — live mini-board cards showing every active game. Each card displays the full 9x9 board with colored cells (green = player 0, red = player 1, blue = tie). Won macro-board sections turn solid. The current player's name shows a ▶ arrow. Cards appear when games start and disappear when they finish.

![Stats window — current games and early results](/stats-current-games.png)

**Finished Games** — two columns (one per player) with W/L/T stats and mini-board cards showing the final board state of each completed game.

![Stats window — finished games](/stats-finished-games.png)

Other features:
- **Progress bar** — animated indeterminate bar while simulation is running
- **Warning banner** — reminder about REST bot server load (closeable)

When both bots have the same name, they're automatically suffixed (#1, #2) to distinguish them in stats.

During bot-vs-bot games, the UI disables the board to prevent accidental clicks while the AI is computing.

## YouTube on setup in IntelliJ
https://www.youtube.com/watch?v=WU1eJXllIgU
