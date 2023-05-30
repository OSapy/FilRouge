package atelier3.gui;

import atelier3.controller.InputViewData;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


public class View extends BorderPane {

    private Pane board;
    private Label blackScoreLabel;
    private Label whiteScoreLabel;

    public View(EventHandler<MouseEvent> clicListener) {
        super();

        board = new Board(clicListener);
        blackScoreLabel = new Label("Score noir : 0");
        whiteScoreLabel = new Label("Score blanc : 0");

        BorderPane checkersBoard = new BorderPane();
        board.prefWidthProperty().bind(this.widthProperty().multiply(0.8));
        board.prefHeightProperty().bind(this.heightProperty());
        checkersBoard.setCenter(board);
        checkersBoard.setTop(createHorizontalAxis());
        checkersBoard.setBottom(createHorizontalAxis());

        VBox scoresBox = new VBox(10);
        scoresBox.setAlignment(Pos.CENTER);
        scoresBox.getChildren().addAll(blackScoreLabel, whiteScoreLabel);

        HBox mainBox = new HBox(20);
        mainBox.getChildren().addAll(checkersBoard, scoresBox);

        this.setCenter(mainBox);
        this.setLeft(createVerticalAxis());
        this.setRight(createVerticalAxis());
    }

    public void updateScores(int blackScore, int whiteScore) {
        blackScoreLabel.setText("Score noir : " + blackScore);
        whiteScoreLabel.setText("Score blanc : " + whiteScore);
    }

    public void actionOnGui(InputViewData<Integer> dataToRefreshView) {
        ((Board) board).actionOnGui(dataToRefreshView);
    }

    private GridPane createHorizontalAxis() {
        GridPane pane = new GridPane();
        pane.prefWidthProperty().bind(this.widthProperty().multiply(0.8).divide(GuiConfig.SIZE));
        for (char c = 'a'; c <= 'j'; c++) {
            Label label1 = new Label(String.valueOf(c));
            label1.setAlignment(Pos.CENTER);
            pane.add(label1, c - 'a', 0);
        }
        return pane;
    }

    private GridPane createVerticalAxis() {
        GridPane pane = new GridPane();
        pane.prefHeightProperty().bind(this.heightProperty().divide(GuiConfig.SIZE));
        for (int c = 10; c >= 1; c--) {
            Label label1 = new Label(String.valueOf(c));
            pane.add(label1, 0, 10 - c + 1);
        }
        return pane;
    }
}