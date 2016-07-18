package dani.address.view;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CounterBarAppService extends Application {
    StackPane root = new StackPane();
    VBox mainBox = new VBox();
    ProgressBar bar = new ProgressBar(0.0);
    CounterService cs = new CounterService();

    @Override
    public void init() throws Exception {
        super.init();

        mainBox.setAlignment(Pos.CENTER);
        mainBox.setSpacing(10);

        Button btn = new Button();
        btn.setText("Count to Ten Million!");
        btn.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                System.out.println("Count Started");
                bar.progressProperty().bind(cs.progressProperty());
                if (cs.getState() == State.READY) {
                    cs.start();
                }
            }
        });

        Button restartBtn = new Button();
        restartBtn.setText("Restart");
        restartBtn.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                System.out.println("Count Started");
                bar.progressProperty().bind(cs.progressProperty());
                cs.restart();
            }
        });

        mainBox.getChildren().add(btn);
        mainBox.getChildren().add(restartBtn);
        mainBox.getChildren().add(bar);
        root.getChildren().add(mainBox);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Service Example");

        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.show();
    }
    

    
    public class CounterService extends Service<CounterService> {
        @Override
        protected Task<CounterService> createTask() {
            CounterTask ct = new CounterTask();
            return ct;
        }
    }
    
    public class CounterTask extends Task {
        @Override
        public Void call() {
            final int max = 10000000;
            updateProgress(0, max);
            for (int i = 1; i <= max; i++) {
                updateProgress(i, max);
            }
            return null;
        }
    }
}
