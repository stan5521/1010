package model;

import java.util.ArrayList;

class Grid
{
    private ArrayList<String> grid;
    private ArrayList<String> gridBeforeLineRemoves;

    Grid(ArrayList<String> savegame) throws Exception
    {
        if(savegame.stream().anyMatch(a->a.length()!=10))
        {
            throw new Exception();
        }
        loadSaveGame(savegame);
    }

    ArrayList<String> getCopy() // For data encapsulation
    {
        return new ArrayList<>(grid);
    }


    int setPuzzleBlock(int row,int column,PuzzleBlock block)
    {
        boolean[][] minBlock = block.getMinimizedBlock();
        if(movePossible(row,column,minBlock))
        {
            setBlock(row,column,block);
            return block.getScore();// invalid move
        }
        return 0;// valid move
    }

    ArrayList<String> getGridBeforeLineRemoves()
    {
        return gridBeforeLineRemoves;
    }

    int checkCompletedLines()
    {
        boolean linesCompleted = false;
        ArrayList<Integer> completedRows = new ArrayList<Integer>();
        ArrayList<Integer> completedColumns = new ArrayList<Integer>();

        gridBeforeLineRemoves = getCopy();

        for (int i =0; i <grid.size();i++)
        {
            if (!grid.get(i).contains("."))
            {
                completedRows.add(i);
            }

            String verticalLine = "";
            for (int k = 0; k < grid.size(); k++) // grid must be a square
            {
                verticalLine += grid.get(k).toCharArray()[i];
            }

            if (!verticalLine.contains("."))
            {
                completedColumns.add(i);
            }
        }
        if(completedRows.size()>0)
        {
            for (int row : completedRows)
            {
                clearRow(row);
            }
        }

        if(completedColumns.size()>0)
        {
            for (int column : completedColumns)
            {
                clearColumn(column);
            }
        }

        return completedColumns.size()+completedRows.size();
    }

    private void clearRow(int index)
    {
        grid.set(index,"..........");
    }

    private void clearColumn(int index)
    {
        for (int i =0;i<grid.size();i++)
        {
            char[] chars = grid.get(i).toCharArray();
            chars[index] = '.';
            grid.set(i,String.valueOf(chars));
        }
    }

    private void loadSaveGame(ArrayList<String> savegame)
    {
        if(savegame==null)
        {
            return;
        }

        grid = savegame;
    }

    boolean anyMovePossible(PuzzleBlock block)
    {
        for (String row : grid)
        {
            for(int i = 0;i<row.length();i++)
            {
                if(movePossible(grid.indexOf(row),i,block.getMinimizedBlock()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean movePossible(int row,int column,boolean[][] minimizedBlock)
    {
        for(int i = 0;i<minimizedBlock.length;i++)
        {
            for(int k=0;k<minimizedBlock[i].length;k++)
            {
                Boolean isPartOfPuzzleBlock = minimizedBlock[i][k];
                if((row+i)>9||(column+k)>9)
                {
                    if(isPartOfPuzzleBlock)
                    {
                        return false;
                    }
                    else
                    {
                        continue;
                    }
                }
                char destination = grid.get(row+i).charAt(column+k); // check if point is out of grid

                if(destination != '.'&&isPartOfPuzzleBlock)
                {
                    return false;// not a valid move
                }
            }
        }
        return true;
    }

    private void setBlock(int row,int column,PuzzleBlock block)
    {
        boolean[][] minimizedBlock = block.getMinimizedBlock();
        for(int i = 0;i<minimizedBlock.length;i++)
        {
            for(int k=0;k<minimizedBlock[i].length;k++)
            {
                Boolean isPartOfPuzzleBlock = minimizedBlock[i][k];
                if(isPartOfPuzzleBlock)
                {
                    char[] chars = grid.get(row+i).toCharArray();
                    chars[column+k] = block.getBlokChar();
                    grid.set(i+row,String.valueOf(chars));
                }
            }
        }
    }

    static final String[] EMPTY =
    {
        "..........",
        "..........",
        "..........",
        "..........",
        "..........",
        "..........",
        "..........",
        "..........",
        "..........",
        ".........."
    };
}
