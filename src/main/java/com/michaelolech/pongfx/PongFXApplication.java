package com.michaelolech.pongfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class PongFXApplication extends Application {
    private final int WINDOW_WIDTH = 1080;
    private final int WINDOW_HEIGHT = 640;
    private final int BRICKS_LEVELS = 3;
    private final int BRICK_WIDTH = 120;
    private final int BRICK_HEIGHT = 20;
    private final int BRICKS_PADDING = (int) (WINDOW_WIDTH * 0.1);

    @Override
    public void start(Stage stage) throws IOException {
        Pane root = createGame();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("PongFX");
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private Pane createGame() {
        Pane root = createWindow();

        createElements(root);

        return root;
    }

    private Pane createWindow() {
        Pane root = new Pane();
        root.setId("root");

        root.getStylesheets().add("styles.css");

        return root;
    }

    private void createElements(Pane root) {
        createBall(root);
        createRacket(root);
        createBricks(root);
    }

    private void createBall(Pane root) {
        Circle ball = new Circle();

        ball.setRadius(5);
        ball.setId("ball");
        ball.setCenterX((double) WINDOW_WIDTH / 2);
        ball.setCenterY((double) WINDOW_HEIGHT / 2);

        root.getChildren().add(ball);
    }

    private void createRacket(Pane root) {
        Rectangle slab = new Rectangle();

        slab.setId("slab");
        slab.setWidth(100);
        slab.setHeight(5);
        slab.setX((double) WINDOW_WIDTH / 2 - slab.getWidth() / 2);
        slab.setY(WINDOW_HEIGHT - 20);

        root.getChildren().add(slab);
    }

    private void createBricks(Pane root) {
        double positionX = BRICKS_PADDING;
        double positionY = 50;
        int y = 0;

        while (y < BRICKS_LEVELS) {
            Rectangle brick = createBrick(positionX, positionY);
            root.getChildren().add(brick);

            positionX += BRICK_WIDTH;

            if (positionX + BRICK_WIDTH > WINDOW_WIDTH - BRICKS_PADDING) {
                positionY += BRICK_HEIGHT;
                positionX = BRICKS_PADDING;
                y++;
            }
        }
    }

    private Rectangle createBrick(double posX, double posY) {
        Rectangle brick = new Rectangle();

        brick.setId("brick");
        brick.setWidth(BRICK_WIDTH);
        brick.setHeight(BRICK_HEIGHT);
        brick.setX(posX);
        brick.setY(posY);

        return brick;
    }
}