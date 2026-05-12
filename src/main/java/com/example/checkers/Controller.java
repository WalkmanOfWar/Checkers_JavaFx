package com.example.checkers;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML private Label redLabel;
    @FXML private Label whiteLabel;
    @FXML private Label timerLabel;
    @FXML private Label winLabel;

    private int redCount = 12;
    private int whiteCount = 12;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        redLabel.setText("12");
        whiteLabel.setText("12");
    }

    public void bindTimer(StringProperty timerText) {
        timerLabel.textProperty().bind(timerText);
    }

    public void decrementRed() {
        redLabel.setText(String.valueOf(--redCount));
        if (redCount == 0) winLabel.setText("Gracz 2 wygrał!");
    }

    public void decrementWhite() {
        whiteLabel.setText(String.valueOf(--whiteCount));
        if (whiteCount == 0) winLabel.setText("Gracz 1 wygrał!");
    }

    public boolean isGameOver() {
        return redCount == 0 || whiteCount == 0;
    }
}
