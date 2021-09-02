package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;
import view.GameView;

import java.net.URL;

public class Controller extends Application
{
    private GameView gameView;
    private Game game;


    public void Load()
    {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        game = new Game();

        URL resource = new GameView().getClass().getResource("game.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.load();

        gameView = fxmlLoader.<GameView>getController();
        gameView.startView(this,game,primaryStage);

        primaryStage.setTitle("Tiles");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(fxmlLoader.getRoot(), 540, 700));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> onClose());
    }

    public boolean placePuzzleBlock(int row, int column,PuzzleBlock puzzleBlock)
    {
        GameState state = game.setPuzzleBlock(row,column,puzzleBlock);
        switch (state)
        {
            case EmptyDockElement:
            case MoveFailed:
                return false;
            case MoveSuccess:
                gameView.update();
                return true;
            case GameOver:
                gameView.showGameOver(false);
                game.newGame();
        }
        return true;
    }

    public void startNewGame()
    {
        game.newGame();
        gameView.update();
    }

    public void surrenderGame()
    {
        game.surrender();
        gameView.showGameOver(true);
    }

    public void resumeGame()
    {
        game.resumeGame();
        gameView.update();
    }

    public void cheat()
    {
        game.cheat();
        gameView.cheatUpdate();
    }

    public void onClose()
    {
        game.saveGame();
    }
}
