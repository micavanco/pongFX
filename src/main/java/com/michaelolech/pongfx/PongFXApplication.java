package com.michaelolech.pongfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PongFXApplication extends Application {
    private final int WINDOW_WIDTH = 1070;
    private final int WINDOW_HEIGHT = 640;
    private final int BRICKS_LEVELS = 3;
    private final int BRICK_WIDTH = 120;
    private final int BRICK_HEIGHT = 20;
    private final int BRICKS_PADDING = (int) (WINDOW_WIDTH * 0.1);
    private final int BALL_RADIUS = 10;
    private final int RACKET_WIDTH = 100;
    private final int RACKET_HEIGHT = 10;
    private final int RACKET_DISTANCE = 20;
    private final int RACKET_Y_POSITION = WINDOW_HEIGHT - RACKET_DISTANCE - RACKET_HEIGHT / 2;
    private Circle ball;
    private Rectangle racket;
    private boolean isGameRunning = false;
    private Thread gameThread;
    private Scene scene;
    private Pane gameWindow;
    private int ballXVelocity = 5;
    private int ballYVelocity = 5;
    private final int THREAD_SLEEP = 30;
    private List<Rectangle> bricks;

    @Override
    public void start(Stage stage) throws IOException {
        Pane mainManu = createMainMenu();

        scene = new Scene(mainManu, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("styles.css");

        stage.setTitle("PongFX");
        stage.setScene(scene);

        stage.show();
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setOnCloseRequest(event -> {
            isGameRunning = false;
        });

        addMainWindowEventListener(scene);
    }

    public static void main(String[] args) {
        launch();
    }

    private Pane createMainMenu() {
        double centerX = (double) WINDOW_WIDTH / 2;
        double centerY = (double) WINDOW_HEIGHT / 2;
        Pane root = new Pane();
        root.setId("menu");

        Text title = new Text("PongFX Main Menu");
        title.setId("menu-title");
        title.setX(centerX - 230);
        title.setY(centerY - 120);

        Button startGameButton = new Button("Start Game");
        startGameButton.setId("menu-button");
        startGameButton.setMinWidth(240);
        startGameButton.setMaxWidth(240);
        startGameButton.setLayoutX(centerX - 120);
        startGameButton.setLayoutY(centerY);
        startGameButton.setOnAction(event -> {
            gameWindow = createGame();
            scene.setRoot(gameWindow);
            startGame();
        });

        root.getChildren().addAll(title, startGameButton);

        return root;
    }

    private Pane createGame() {
        Pane root = createGameWindow();

        createElements(root);

        return root;
    }

    private Pane createGameWindow() {
        Pane root = new Pane();
        root.setId("game");

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
        racket.setY(WINDOW_HEIGHT - RACKET_DISTANCE);

        root.getChildren().add(racket);
    }

    private void createBricks(Pane root) {
        bricks = new ArrayList<>();
        double positionX = BRICKS_PADDING;
        double positionY = 50;
        int y = 0;

        while (y < BRICKS_LEVELS) {
            Rectangle brick = createBrick(positionX, positionY);
            root.getChildren().add(brick);

            bricks.add(brick);

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
                // handling collision with walls
                if (
                        ball.getCenterX() >= WINDOW_WIDTH - BALL_RADIUS ||
                        ball.getCenterX() <= (double) BALL_RADIUS / 2
                ) {
                    ballXVelocity = -ballXVelocity;
                }
                // handling collision with the ceiling
                else if (
                        ball.getCenterY() <= BALL_RADIUS
                ) {
                    ballYVelocity = -ballYVelocity;
                }
                // handling collision with the racket
                else if (
                        racket.getBoundsInParent().intersects(ball.getBoundsInParent())
                ) {
                    ballYVelocity = -ballYVelocity;

                    // handle bouncing back if hit on half of it
                    if (ball.getCenterX() < racket.getX() + racket.getWidth() / 2 && ballXVelocity > 0) {
                        ballXVelocity = -ballXVelocity;
                    } else if (ball.getCenterX() > racket.getX() + racket.getWidth() / 2 && ballXVelocity < 0) {
                        ballXVelocity = -ballXVelocity;
                    }
                }
                // handling collision with the floor - game over
                else if (ball.getCenterY() > RACKET_Y_POSITION + BALL_RADIUS) {
                    isGameRunning = false;
                    Pane mainManu = createMainMenu();
                    scene.setRoot(mainManu);
                }
                // handling bricks collisions
                else if (collideWithBrick(ball)) {
                    ballYVelocity = -ballYVelocity;
                }


                try {
                    ball.setCenterX(ball.getCenterX() + ballXVelocity);
                    ball.setCenterY(ball.getCenterY() + ballYVelocity);
                    Thread.sleep(THREAD_SLEEP);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            isGameRunning = false;
        });
        gameThread.start();
    }

    private boolean collideWithBrick(Circle ball) {
        for (Rectangle brick : bricks) {
            if (
                brick.intersects(ball.getBoundsInParent())
            ) {
                bricks.remove(brick);
                Platform.runLater(() -> gameWindow.getChildren().remove(brick));
                return true;
            }
        }

        return false;
    }
}