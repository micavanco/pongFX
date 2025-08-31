package com.michaelolech.pongfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class PongFXApplication extends Application {
    private final int WINDOW_WIDTH = 1070;
    private final int WINDOW_HEIGHT = 640;
    private final int BRICKS_LEVELS = 3;
    private final int BRICK_WIDTH = 120;
    private final int BRICK_HEIGHT = 20;
    private final int BRICKS_PADDING = (int) (WINDOW_WIDTH * 0.1);
    private final int BALL_RADIUS = 10;
    private final int BALL_DIAMETER = BALL_RADIUS * 2;
    private final int RACKET_WIDTH = 100;
    private final int RACKET_HEIGHT = 10;
    private final int RACKET_Y_POSITION = WINDOW_HEIGHT - 20 - RACKET_HEIGHT;
    private Circle ball;
    private Rectangle racket;
    private boolean isGameRunning = false;
    private Thread gameThread;
    private int ballXVelocity = 15;
    private int ballYVelocity = 15;

    @Override
    public void start(Stage stage) throws IOException {
        Pane root = createGame();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("PongFX");
        stage.setScene(scene);

        stage.show();
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setOnCloseRequest(event -> {
            isGameRunning = false;
        });

        addMainWindowEventListener(scene);
        startGame();
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
        this.ball = new Circle();

        ball.setRadius(BALL_RADIUS);
        ball.setId("ball");
        ball.setCenterX((double) WINDOW_WIDTH / 2);
        ball.setCenterY((double) WINDOW_HEIGHT / 2 - 100);

        root.getChildren().add(ball);
    }

    private void createRacket(Pane root) {
        this.racket = new Rectangle();

        racket.setId("racket");
        racket.setWidth(RACKET_WIDTH);
        racket.setHeight(RACKET_HEIGHT);
        racket.setX((double) WINDOW_WIDTH / 2 - racket.getWidth() / 2);
        racket.setY(WINDOW_HEIGHT - 20);

        root.getChildren().add(racket);
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

    private void addMainWindowEventListener(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> {
                    if (racket.getX() > 0) {
                        racket.setX(racket.getX() - 17);
                    }
                }
                case RIGHT -> {
                    if (racket.getX() < WINDOW_WIDTH - RACKET_WIDTH) {
                        racket.setX(racket.getX() + 17);
                    }
                }
            }
        });
    }

    private synchronized void startGame() {
        isGameRunning = true;
        gameThread = new  Thread(() -> {
            while (isGameRunning) {
                if (
                        ball.getCenterX() >= WINDOW_WIDTH - BALL_DIAMETER ||
                        ball.getCenterX() - BALL_DIAMETER < 0
                ) {
                    ballXVelocity = -ballXVelocity;
                } else if (
                        ball.getCenterY() <= BALL_RADIUS
                ) {
                    ballYVelocity = -ballYVelocity;
                } else if (
                        ball.getCenterX() >= racket.getX() &&
                        ball.getCenterX() - BALL_RADIUS < racket.getX() + racket.getWidth() &&
                        ball.getCenterY() + BALL_RADIUS >= RACKET_Y_POSITION
                ) {
                    ballYVelocity = -ballYVelocity;
                }


                    try {
                    ball.setCenterX(ball.getCenterX() + ballXVelocity);
                    ball.setCenterY(ball.getCenterY() + ballYVelocity);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            isGameRunning = false;
        });
        gameThread.start();
    }
}