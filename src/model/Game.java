package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game
{
    private Grid grid;
    private int score = 0;
    private int highscore = 0;
    private GameState state;

    private ArrayList<PuzzleBlock> dock;

    public Game()
    {
        start();
    }

    private void start()
    {
        getSaveGameData();
    }

    public void newGame()
    {
        state = GameState.NewGame;
        createCleanNewGame();
    }

    private void addScore(int score)
    {
        this.score += score;
        if(this.score>highscore)
        {
            highscore = this.score;
        }
    }

    public void resumeGame()
    {
        state = GameState.Resumed;
    }

    public int getScore()
    {
        return score;
    }

    public int getHighscore()
    {
        return highscore;
    }

    public GameState setPuzzleBlock(int row, int column, PuzzleBlock puzzleBlock)
    {
        int dockIndex = dock.indexOf(puzzleBlock);

        if(state == GameState.GameOver||state == GameState.Surrendered)
        {
            return state;
        }

        if(dock.get(dockIndex) == null)
        {
            state = GameState.EmptyDockElement;
            return state;
        }

        int score = grid.setPuzzleBlock(row,column,dock.get(dockIndex));

        if(score == 0)
        {
            state = GameState.MoveFailed;
            return state;
        }
        
        addScore(score);

        dock.set(dockIndex,null);
        calculateScore(grid.checkCompletedLines());
        checkRefill();

        if(!anyMovesPossible()) // no moves left
        {
            System.out.println("no moves left, Game over");
            state = GameState.GameOver;
            return state;
        }

        state = GameState.MoveSuccess;
        return state;
    }

    private void calculateScore(int completedLines)
    {
        int bonus = 0;

        for(int i = 1;i<completedLines&&completedLines>1 ;i++)
        {
            bonus += 10*i;
        }

        int tilesScore = completedLines * 10 ;
        addScore(tilesScore + bonus);
    }

    public void cheat()
    {
        score = 0;
        generateRandomPuzzleBlocks();
    }

    public boolean isHighScore()
    {
        return !(score < highscore);
    }

    private void checkRefill()
    {
        if(!dock.stream().anyMatch(d->d != null))
        {
            generateRandomPuzzleBlocks();
        }
    }

    private void generateRandomPuzzleBlocks()
    {
        for(int i =0;i<3;i++)
        {
            dock.set(i,new PuzzleBlock(new Random().nextInt(19)));
        }
    }

    private boolean anyMovesPossible()
    {
        for(PuzzleBlock block: dock)
        {
            if(block == null)
            {
                continue;
            }

            if(grid.anyMovePossible(block))
            {
                return true;
            }
        }
        return false;
    }

    public void saveGame()
    {
        writeSaveGameData();
    }

    public void surrender()
    {
        state = GameState.Surrendered;
    }

    private void getSaveGameData()
    {
        ArrayList<String> lines = new File("resources/savegame.txt").readAllLines();

        if(lines == null)
        {
            createCleanNewGame();
            lines = new File("resources/savegame.txt").readAllLines();
        }

        try
        {
            setSaveGameData(lines);
        }
        catch (Exception e)
        {
            createCleanNewGame();
            writeSaveGameData();
        }
    }

    private void setSaveGameData(ArrayList<String> lines) throws Exception
    {
        String[] blockNumbers = lines.get(10).split(" ");

        dock = new ArrayList<PuzzleBlock>(Arrays.asList(
                isNull(blockNumbers[0])?null:new PuzzleBlock((Integer.parseInt(blockNumbers[0])-1)),
                isNull(blockNumbers[1])?null:new PuzzleBlock((Integer.parseInt(blockNumbers[1])-1)),
                isNull(blockNumbers[2])?null:new PuzzleBlock((Integer.parseInt(blockNumbers[2])-1))
        ));

        addScore(Integer.parseInt(lines.get(11)));
        highscore = Integer.parseInt(lines.get(12));
        grid = new Grid(new ArrayList<String>(lines.subList(0,10)));
    }

    public ArrayList<String> getGridBeforeRemove()
    {
        return grid.getGridBeforeLineRemoves();
    }

    public int[] getClearedColumns()
    {
        ArrayList<Integer> clearedColumns = new ArrayList<Integer>();
        ArrayList<String> gridBeforeLineRemoves = grid.getGridBeforeLineRemoves();
        if(gridBeforeLineRemoves == null)
        {
            return new int[0];
        }
        for(int i = 0;i<10;i++)
        {
            String verticalLine = "";
            for(int k = 0;k<10;k++)
            {
                verticalLine += gridBeforeLineRemoves.get(k).charAt(i);
            }
            if(!verticalLine.contains("."))
            {
                clearedColumns.add(i);
            }
        }
        return clearedColumns.stream().mapToInt(i->i).toArray();
    }

    public int[] getClearedRows()
    {
        ArrayList<String> gridBeforeLineRemoves = grid.getGridBeforeLineRemoves();
        if(gridBeforeLineRemoves == null)
        {
            return new int[0];
        }
        return gridBeforeLineRemoves.stream()
                .filter(a->!a.contains("."))
                .mapToInt(gridBeforeLineRemoves::indexOf)
                .toArray();

    }

    private void writeSaveGameData()
    {
        ArrayList<String> data = grid.getCopy();
        String dockIndexes = "";
        for (PuzzleBlock block : dock)
        {
            if(block == null)
            {
                dockIndexes += "n ";
            }
            else
            {
                dockIndexes += block.getBlockIndex() + " ";
            }
        }
        data.add(dockIndexes);
        data.add(String.valueOf(score));
        data.add(String.valueOf(highscore));

        new File("resources/savegame.txt",data);
    }

    public Color getPuzzleBlockColor(char character)
    {
        if(character=='.')
        {
            return Color.LIGHTGRAY;
        }
        return new PuzzleBlock(character).getColor();
    }

    public ArrayList<PuzzleBlock> getDock()
    {
        return new ArrayList<>(dock);
    }

    public ArrayList<String> getGrid()
    {
        return grid.getCopy();
    }

    private void createCleanNewGame()
    {
        dock = new ArrayList<>(Arrays.asList(new PuzzleBlock[]{null, null, null}));
        checkRefill();
        try
        {
            grid = new Grid(new ArrayList<>(Arrays.asList(Grid.EMPTY)));
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        this.score = 0;
        writeSaveGameData();
    }

    private boolean isNull(String s)
    {
        return s.equals("n");
    }
}
