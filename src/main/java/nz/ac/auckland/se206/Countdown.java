package nz.ac.auckland.se206;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

public class Countdown {
  private int secondsLeft;
  private Timer timer;
  private CountdownUpdateListener updateListener;

  public Countdown(int seconds) {
    this.secondsLeft = seconds;
  }

  public void start() {
    timer = new Timer();
    timer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            if (secondsLeft > 0) {
              Platform.runLater(
                  () -> {
                    if (updateListener != null) {
                      updateListener.onUpdate(secondsLeft);
                    }
                  });
              secondsLeft--;
            } else {
              timer.cancel();
              Platform.runLater(() -> System.out.println("Time's up!"));
            }
          }
        },
        0,
        1000);
  }

  public void stop() {
    if (timer != null) {
      timer.cancel();
    }

    timer = null;
  }

  public String formatTime(int seconds) {
    int mins = seconds / 60;
    int secs = seconds % 60;
    return String.format("%02d:%02d", mins, secs);
  }

  public int getSecondsLeft() {
    return secondsLeft;
  }

  public void setOnUpdate(CountdownUpdateListener listener) {
    this.updateListener = listener;
  }
}
