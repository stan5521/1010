package view;

import controller.Controller;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Game;
import model.PuzzleBlock;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameView
{
    private Controller controller;
    private Stage primaryStage;
    private Game model;

    final URL resourceLineComplete = getClass().getResource("/linecomplete.wav");
    final AudioClip clipLineComplete = new AudioClip(resourceLineComplete.toString());

    final URL resourceClick = getClass().getResource("/click.wav");
    final AudioClip clipClick = new AudioClip(resourceClick.toString());

    @FXML private GridPane gridPanePlayingField;
    @FXML private GridPane gridPaneDock;
    @FXML private GridPane gridPaneLeftDock;
    @FXML private GridPane gridPaneCenterDock;
    @FXML private GridPane gridPaneRightDock;

    @FXML private BorderPane borderPaneGameOver;

    @FXML private StackPane stackPaneResume;

    @FXML private Label labelScore;
    @FXML private Label labelHighScore;
    @FXML private Label labelGameOverHighScore;
    @FXML private Label labelGameEndCause;

    @FXML private Rectangle rectangleResume;

    @FXML private ImageView imageViewTrophyIcon;

    public void startView(Controller controller, Game model, Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.controller = controller;
        this.model = model;

        clear();
        fillGrid(model.getGrid());
        fillDock(model.getDock());
        updateScores();
    }

    public void update()
    {
        clear();
        updateGrid(model.getGridBeforeRemove());
        fillDock(model.getDock());
        updateScores();
    }

    public void cheatUpdate()
    {
        gridPaneLeftDock.getChildren().clear();
        gridPaneCenterDock.getChildren().clear();
        gridPaneRightDock.getChildren().clear();

        clearRowsAndColumns(gridPaneLeftDock);
        clearRowsAndColumns(gridPaneCenterDock);
        clearRowsAndColumns(gridPaneRightDock);
        updateGrid(model.getGrid());
        fillDock(model.getDock());
        updateScores();
    }

    private void clear()
    {
        gridPaneLeftDock.getChildren().clear();
        gridPaneCenterDock.getChildren().clear();
        gridPaneRightDock.getChildren().clear();

        clearRowsAndColumns(gridPaneLeftDock);
        clearRowsAndColumns(gridPaneCenterDock);
        clearRowsAndColumns(gridPaneRightDock);

        gridPanePlayingField.getChildren().clear();
    }

    private void updateScores()
    {
        labelHighScore.setText(model.getHighscore() + "");
        labelScore.setText(model.getScore() + "");
    }

    private void clearRowsAndColumns(GridPane pane)
    {
        pane.getRowConstraints().clear();
        pane.getColumnConstraints().clear();
    }

    private void fillGrid(ArrayList<String> gridData)
    {
        for(int i =0;i< gridPanePlayingField.getColumnCount();i++)
        {
            for(int k=0;k< gridPanePlayingField.getColumnCount();k++)
            {
                GridPane wrapper = new GridPane();
                Rectangle square = new Rectangle();
                square.setFill(model.getPuzzleBlockColor(gridData.get(i).toCharArray()[k]));
                square.setHeight(30);
                square.setWidth(30);
                GridPane.setMargin(square,new Insets(1));
                square.setArcHeight(7);
                square.setArcWidth(7);
                wrapper.getChildren().add(square );
                gridPanePlayingField.add(wrapper,k,i);
            }
        }
    }

    private void updateGrid(ArrayList<String> gridData)
    {
        if(gridData == null)
        {
            gridData = model.getGrid();
        }

        fillGrid(gridData);

        int[] clearedColumns = model.getClearedColumns();
        int[] clearedRows = model.getClearedRows();

        for(int columnIndex : clearedColumns)
        {
            for(int i =0;i<10;i++)
            {
                Rectangle r = (Rectangle)((GridPane)gridPanePlayingField.getChildren().get(Integer.parseInt((i+""+columnIndex)))).getChildren().get(0);
                resetBlockWithAnimation(r,i*30);
            }
        }
        for(int rowIndex : clearedRows)
        {
            for(int i =0;i<10;i++)
            {
                Rectangle r = (Rectangle)((GridPane)gridPanePlayingField.getChildren().get(Integer.parseInt((rowIndex+""+i)))).getChildren().get(0);
                resetBlockWithAnimation(r,i*20);
            }
        }

    }

    private void resetBlockWithAnimation(Rectangle rectangle,int delay)
    {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), rectangle);
        FillTransition ft = new FillTransition(Duration.millis(200), rectangle, (Color) rectangle.getFill(), Color.LIGHTGRAY);
        ft.setDelay(new Duration(delay));
        st.setToX(0);
        st.setDelay(new Duration(delay));
        st.setToY(0);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
        ft.play();
        clipLineComplete.play(1.0);
    }

    private void fillDock(ArrayList<PuzzleBlock> puzzleBlocks)
    {
        for(PuzzleBlock puzzleBlock : puzzleBlocks)
        {
            if(puzzleBlock == null)
            {
                continue;
            }
            boolean[][] grid = puzzleBlock.getMinimizedBlock();
            GridPane dock = (GridPane)gridPaneDock.getChildren().get(puzzleBlocks.indexOf(puzzleBlock));
            for (int i =0;i<grid.length;i++)
            {
                dock.getRowConstraints().add(new RowConstraints());
                dock.getColumnConstraints().add(new ColumnConstraints());
            }

            for (int i =0;i<grid.length;i++)
            {
                for(int k =0;k< grid[i].length;k++)
                {
                    GridPane gridPaneRectangleWrapper = new GridPane();
                    Rectangle square = new Rectangle();
                    if(grid[i][k])
                    {
                        square.setFill(puzzleBlock.getColor());
                        gridPaneRectangleWrapper.setOnMouseDragged(event -> movePuzzleBlock(puzzleBlock,gridPaneRectangleWrapper,event));
                        gridPaneRectangleWrapper.setOnMouseReleased(event -> puzzleBlockReleased(puzzleBlock,gridPaneRectangleWrapper,event));
                        gridPaneRectangleWrapper.setOnMousePressed(event -> puzzleBlockClicked(event,gridPaneRectangleWrapper ));
                    }
                    else
                    {
                        square.setFill(Color.TRANSPARENT);
                        square.setId("not-active");
                    }
                    square.setHeight(30);
                    square.setWidth(30);
                    if(puzzleBlocks.stream().filter(a->a!=null).count() == 3)
                    {
                        square.setTranslateX(500);
                        TranslateTransition tt = new TranslateTransition(Duration.millis(400), square);
                        tt.setToX(0);
                        tt.setToY(0);
                        tt.setAutoReverse(true);
                        tt.play();
                    }
                    GridPane.setMargin(square,new Insets(1));
                    square.setArcHeight(7);
                    square.setArcWidth(7);
                    gridPaneRectangleWrapper.add(square,0 ,0 );
                    dock.add(gridPaneRectangleWrapper,k,i);
                    dock.setScaleX(0.6);
                    dock.setScaleY(0.6);
                }
            }
        }
    }

    public void showGameOver(boolean isSurrender)
    {
        borderPaneGameOver.setVisible(true);
        labelGameEndCause.setText(isSurrender?"You Surrendered":"No Moves Left");
        if(isSurrender)
        {
            rectangleResume.setFill(Color.rgb(2, 185, 25));
            stackPaneResume.setOnMouseClicked(event -> resumeGame());
        }
        else
        {
            rectangleResume.setFill(Color.GRAY);
            stackPaneResume.setOnMouseClicked(null);
        }

        labelGameOverHighScore.setText(model.getScore()+"");

        if(!model.isHighScore())
        {
            imageViewTrophyIcon.setImage(null);
        }
        else
        {
            imageViewTrophyIcon.setImage(new Image("/highscore_white.png"));
        }
    }

    @FXML
    private void surrenderGame()
    {
        controller.surrenderGame();
    }

    @FXML
    private void startNewGame()
    {
        borderPaneGameOver.setVisible(false);
        controller.startNewGame();
    }

    @FXML
    private void resumeGame()
    {
        borderPaneGameOver.setVisible(false);
        controller.resumeGame();
    }

    @FXML
    private void cheat()
    {
        controller.cheat();
    }

    private void movePuzzleBlock(PuzzleBlock puzzleBlock,GridPane wrapper, MouseEvent event)
    {
        ArrayList<Rectangle> allBlocksInDock = getAllBlocksInDock(wrapper);


        for(Rectangle block : allBlocksInDock)
        {
            changeColorIfBlockNotPossible(puzzleBlock,block);
            GridPane blockWrapper =(GridPane) block.getParent();
            blockWrapper.setTranslateX(event.getX() + blockWrapper.getTranslateX()-15);
            blockWrapper.setTranslateY(event.getY() + blockWrapper.getTranslateY()-15);
            blockWrapper.getParent().getParent().toFront();
        }
        event.consume();
    }

    private void puzzleBlockClicked(MouseEvent e,Node ele)
    {
        if(e.getButton() == MouseButton.PRIMARY)
        {
            ScaleTransition st = new ScaleTransition(Duration.millis(200),ele.getParent());
            st.setToY(1.0);
            st.setToX(1.0);
            st.play();
        }
    }

    private void changeColorIfBlockNotPossible(PuzzleBlock puzzleBlock, Rectangle block)
    {
        Color puzzleBlockColor = puzzleBlock.getColor();
        Bounds boundsBlock = block.localToScene(block.getBoundsInLocal());
        double x = ((boundsBlock.getMinX() + boundsBlock.getMaxX())/2);
        double y = ((boundsBlock.getMinY() + boundsBlock.getMaxY())/2);

        List<Node> children = gridPanePlayingField.getChildren();
        boolean possible = false;
        for(Node child:children)
        {
            Bounds bounds = child.localToScene(child.getBoundsInLocal());
            if(bounds.getMaxY()+1 > y && bounds.getMinY()-1 < y)
            {
                if(bounds.getMaxX()+1 > x && bounds.getMinX()-1 < x)
                {
                    Rectangle blockOnPlayingField = (Rectangle) ((GridPane) child).getChildren().get(0);
                    possible = blockOnPlayingField.getFill() == Color.LIGHTGRAY;
                }
            }
        }
        if(block.getId() != "not-active")
        {
            block.setFill(possible?puzzleBlockColor:Color.RED);
        }

    }

    private void puzzleBlockReleased(PuzzleBlock puzzleBlock, GridPane wrapper, MouseEvent e)
    {
        if(e.getButton() == MouseButton.SECONDARY)
        {
            return;
        }
        Rectangle origin =  getTopLeftBlock((Rectangle) wrapper.getChildren().get(0));
        Bounds originBounds = origin.localToScene(origin.getBoundsInLocal());
        double x = ((originBounds.getMinX() + originBounds.getMaxX())/2);
        double y = ((originBounds.getMinY() + originBounds.getMaxY())/2);

        List<Node> children = gridPanePlayingField.getChildren();
        for(Node child:children)
        {
            Bounds bounds = child.localToScene(child.getBoundsInLocal());
            if(bounds.getMaxY()+1 > y && bounds.getMinY()-1 < y)
            {
                if(bounds.getMaxX()+1 > x && bounds.getMinX()-1 < x)
                {
                    int col = GridPane.getColumnIndex(child);
                    int row = GridPane.getRowIndex(child);
                    if(controller.placePuzzleBlock(row,col,puzzleBlock))
                    {
                        clipClick.play(1.2);
                        return;
                    }
                }
            }
        }
        puzzleBlockJumpBack(wrapper,puzzleBlock);

    }

    private void puzzleBlockJumpBack(GridPane wrapper, PuzzleBlock puzzleBlock)
    {
        ScaleTransition st = new ScaleTransition(Duration.millis(200),wrapper.getParent());
        st.setToY(0.6);
        st.setToX(0.6);
        st.play();
        Color blockColor =puzzleBlock.getColor();
        ArrayList<Rectangle> allBlocksInDock = getAllBlocksInDock(wrapper);
        for(Rectangle block : allBlocksInDock)
        {
            if(block.getId()!="not-active")
            {
                block.setFill(blockColor);
            }
            GridPane blockWrapper = (GridPane) block.getParent();
            TranslateTransition tt = new TranslateTransition(Duration.millis(250), blockWrapper);
            tt.setToX(0);
            tt.setToY(0);
            tt.setAutoReverse(true);
            tt.play();
        }
    }

    private ArrayList<Rectangle> getAllBlocksInDock(GridPane wrapper)
    {
         ArrayList<Rectangle> result = new ArrayList<>();
         List<Node> children = ((GridPane)wrapper.getParent()).getChildren();
         for (Node child : children)
         {
             Rectangle r = (Rectangle) ((GridPane)child).getChildren().get(0);
             result.add(r);

         }
         return result;
    }

    private Rectangle getTopLeftBlock(Rectangle rectangle)
    {
        GridPane dock = ((GridPane)rectangle.getParent().getParent());
        for(Node child : dock.getChildren())
        {
            Rectangle rect = (Rectangle) ((GridPane)child).getChildren().get(0);
            if(GridPane.getRowIndex(rect)==0&&GridPane.getColumnIndex(rect)==0)
            {
                return rect;
            }
        }
        return null;
    }
}
