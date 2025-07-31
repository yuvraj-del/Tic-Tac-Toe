package application;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Controller {

    @FXML
    private Label shape, playerTitle, scoreX, scoreY, text;

    @FXML
    private Button playAgainBtn, quit, btn00, btn01, btn02, btn10, btn11, btn12, btn20, btn21, btn22;

    @FXML
    private Line winnerLine;

    private int[][] pos = {{3, 4, 5}, {6, 7, 8}, {9, 10, 11}};

    private String turn = "X";

    private boolean winner = false;

    private int count = 0;

    private Stage stage;
    private Scene scene;

    private static final double CELL_SIZE = 133.5;
    private static final double OFFSET = 66.75;

    private static final double[][] LINE_COORDS = {
            {OFFSET, OFFSET, OFFSET + CELL_SIZE * 2, OFFSET},
            {OFFSET, OFFSET + CELL_SIZE, OFFSET + CELL_SIZE * 2, OFFSET + CELL_SIZE},
            {OFFSET, OFFSET + CELL_SIZE * 2, OFFSET + CELL_SIZE * 2, OFFSET + CELL_SIZE * 2},
            {OFFSET, OFFSET, OFFSET, OFFSET + CELL_SIZE * 2},
            {OFFSET + CELL_SIZE, OFFSET, OFFSET + CELL_SIZE, OFFSET + CELL_SIZE * 2},
            {OFFSET + CELL_SIZE * 2, OFFSET, OFFSET + CELL_SIZE * 2, OFFSET + CELL_SIZE * 2},
            {OFFSET, OFFSET, OFFSET + CELL_SIZE * 2, OFFSET + CELL_SIZE * 2},
            {OFFSET, OFFSET + CELL_SIZE * 2, OFFSET + CELL_SIZE * 2, OFFSET}
    };

    private static final int[][][] WINNING_COMBINATIONS = {
            {{0, 0}, {0, 1}, {0, 2}},
            {{1, 0}, {1, 1}, {1, 2}},
            {{2, 0}, {2, 1}, {2, 2}},
            {{0, 0}, {1, 0}, {2, 0}},
            {{0, 1}, {1, 1}, {2, 1}},
            {{0, 2}, {1, 2}, {2, 2}},
            {{0, 0}, {1, 1}, {2, 2}},
            {{0, 2}, {1, 1}, {2, 0}}
    };

    private void drawWinLine(int lineIndex) {
        double[] coords = LINE_COORDS[lineIndex];
        winnerLine.setStartX(coords[0]);
        winnerLine.setStartY(coords[1]);
        winnerLine.setEndX(coords[2]);
        winnerLine.setEndY(coords[3]);
        winnerLine.setOpacity(1);
    }

    public void play(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Scene.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void quit(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Intro.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
        GameSettings.hard = false;
        GameSettings.easy = false;
    }

    public void easy() {
        GameSettings.easy = true;
        GameSettings.hard = false;
        text.setText("Selected: Easy");
    }

    public void hard() {
        GameSettings.hard = true;
        GameSettings.easy = false;
        text.setText("Selected: Impossible");
    }

    public void plr() {
        GameSettings.hard = false;
        GameSettings.easy = false;
        text.setText("Selected: 2-Player");
    }

    public void nextShape() {
        if (turn.equals("X")) {
            shape.setText("O");
            playerTitle.setText(GameSettings.easy || GameSettings.hard ? "AI" : "Player: 2");
        } else {
            shape.setText("X");
            playerTitle.setText("Player: 1");
        }
        turn = shape.getText();
        if (turn.equals("O") && GameSettings.easy) placeRandom();
        if (turn.equals("O") && GameSettings.hard) placeHard();
    }

    public void place(ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (!((btn.getText()).equals("X")) && !((btn.getText()).equals("O")) && !winner) {
            count++;

            String btnId = btn.getId();
            int btnIdX = Integer.parseInt(String.valueOf(btnId.charAt(3)));
            int btnIdY = Integer.parseInt(String.valueOf(btnId.charAt(4)));

            pos[btnIdY][btnIdX] = turn.equals("X") ? 1 : 2;
            btn.setText(turn);

            for (int i = 0; i < WINNING_COMBINATIONS.length; i++) {
                int[][] line = WINNING_COMBINATIONS[i];

                if (pos[line[0][0]][line[0][1]] == pos[line[1][0]][line[1][1]] && pos[line[1][0]][line[1][1]] == pos[line[2][0]][line[2][1]]) {
                    winner = true;

                    drawWinLine(i);

                    shape.setText(turn);
                    playerTitle.setText("Winner: " + turn);

                    int scoreXVal = Integer.parseInt(scoreX.getText());
                    int scoreYVal = Integer.parseInt(scoreY.getText());
                    if (turn.equals("X")) scoreX.setText(String.valueOf(scoreXVal + 1));
                    else scoreY.setText(String.valueOf(scoreYVal + 1));

                    playAgainBtn.setOpacity(1);
                    quit.setOpacity(1);
                    break;
                }
            }

            if (!winner && count == 9) {
                winner = true;

                shape.setText("");
                playerTitle.setText("Tie");

                playAgainBtn.setOpacity(1);
                quit.setOpacity(1);
            }

            if (!winner) {
                nextShape();
            }

        }

    }

    public Button[] getAllButtons() {
        return new Button[] {btn00, btn01, btn02, btn10, btn11, btn12, btn20, btn21, btn22};
    }

    private void setButtonsDisabled(boolean disabled) {
        for (Button btn : getAllButtons()) {
            btn.setDisable(disabled);
        }
    }

    public void placeRandom() {
        setButtonsDisabled(true);

        ArrayList<Button> emptyBtns = new ArrayList<>();
        for (Button b: getAllButtons()) {
            if (b.getText().isEmpty()) {
                emptyBtns.add(b);
            }
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(_ -> {
            if (!emptyBtns.isEmpty()) {
                place(new ActionEvent(emptyBtns.get((int)(Math.random() * emptyBtns.size())), null));
            }
            setButtonsDisabled(false);
        });

        pause.play();
    }

    public void placeHard() {
        setButtonsDisabled(true);

        Button[][] buttons = {
                {btn00, btn10, btn20},
                {btn01, btn11, btn21},
                {btn02, btn12, btn22}
        };

        int[] btnID = bestMove(getBoard());

        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(_ -> {
            place(new ActionEvent(buttons[btnID[0]][btnID[1]], null));

            setButtonsDisabled(false);
        });

        pause.play();
    }

    public int[] bestMove(char[][] board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = 'O';
                    int score = minimax(board, false);
                    board[i][j] = ' ';

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        return bestMove;
    }

    public int minimax(char[][] board, boolean isMaximizing) {
        char winner = checkWinner(board);
        if (winner == 'O') return 1;
        if (winner == 'X') return -1;
        if (full(board)) return 0;

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = isMaximizing ? 'O' : 'X';
                    int score = minimax(board, !isMaximizing);
                    board[i][j] = ' ';
                    bestScore = isMaximizing ? Math.max(score, bestScore) : Math.min(score, bestScore);
                }
            }
        }

        return bestScore;
    }

    public char[][] getBoard() {
        char[][] board = new char[3][3];
        Button[][] btn = {
            {btn00, btn10, btn20},
            {btn01, btn11, btn21},
            {btn02, btn12, btn22}
        };
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String text = btn[i][j].getText();
                board[i][j] = text.isEmpty() ? ' ': text.charAt(0);
            }
        }
        return board;
    }

    public boolean full(char[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') return false;
            }
        }
        return true;
    }

    public char checkWinner(char[][] board) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return board[i][0];
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return board[0][i];
        }
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return board[0][0];
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return board[0][2];
        return ' ';
    }

    public void playAgainRun() {
        if (playAgainBtn.getOpacity() != 0) {
            winner = false;
            count = 0;
            pos = new int[][] {{3, 4, 5}, {6, 7, 8}, {9, 10, 11}};

            for (Button btn: getAllButtons()) btn.setText("");

            playAgainBtn.setOpacity(0);
            quit.setOpacity(0);
            winnerLine.setOpacity(0);

            nextShape();
        }
    }

}