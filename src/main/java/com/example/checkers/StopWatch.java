package com.example.checkers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;

public class StopWatch {
    private final StringProperty text = new SimpleStringProperty("00:00:000");
    private final Timeline timeline;
    private int mins, secs, millis;

    public StopWatch() {
        timeline = new Timeline(new KeyFrame(Duration.millis(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);
    }

    public void start() {
        timeline.playFromStart();
    }

    public void stop() {
        timeline.stop();
    }

    public void reset() {
        mins = 0;
        secs = 0;
        millis = 0;
    }

    public StringProperty textProperty() {
        return text;
    }

    private void tick() {
        if (millis == 1000) {
            secs++;
            millis = 0;
        }
        if (secs == 60) {
            mins++;
            secs = 0;
        }
        text.set(String.format("%02d:%02d:%03d", mins, secs, millis++));
    }
}
