package model;


import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class PuzzleBlock
{
    private boolean[][] block;
    private char blokChar;

    PuzzleBlock(int blockNumber)
    {
        blokChar = (char)(blockNumber+65);
        setBlock(blockNumber);
    }

    PuzzleBlock(char character)
    {
        blokChar = character;
        setBlock(getBlockIndex()-1);
    }

    public int getBlockIndex()
    {
        return blokChar - 64;
    }

    private void setBlock(int blockNumber)
    {
        ArrayList<String> blockFile = new File("resources/config.txt").readAllLines(); //LESS IMPORTANT TODO: cache textfile
        block = new boolean[5][];

        for (int i = 0;i<25;i+=5)
        {
            block[i/5] = new boolean[]
            {
                blockFile.get(blockNumber).charAt(i)   != '.',
                blockFile.get(blockNumber).charAt(i+1) != '.',
                blockFile.get(blockNumber).charAt(i+2) != '.',
                blockFile.get(blockNumber).charAt(i+3) != '.',
                blockFile.get(blockNumber).charAt(i+4) != '.'
            };
        }
    }

    public int getScore()
    {
        int score = 0;
        for(int i = 0;i<4;i++)
        {
            for(int k=0;k<4;k++)
            {
                if(block[i][k]) score++;
            }
        }
        return score;
    }

    public Color getColor()
    {
        switch (getBlockIndex()-1)
        {
            case 0:
                return Color.rgb(122, 130, 207);
            case 1:
            case 2:
                return Color.rgb(244, 199, 62);
            case 3:
            case 4:
                return Color.rgb(219, 140, 69);
            case 5:
            case 6:
            case 7:
            case 8:
                return Color.rgb(105, 219, 126);
            case 9:
            case 10:
                return Color.rgb(211, 82, 122);
            case 11:
                return Color.rgb(157, 235, 81);
            case 12:
            case 13:
                return Color.rgb(199, 82, 79);
            case 14:
            case 15:
            case 16:
            case 17:
                return Color.rgb(104, 193, 224);
            case 18:
                return Color.rgb(97, 229, 172);
            default:
                return Color.rgb(0, 0, 0);
        }
    }

    public boolean[][] getMinimizedBlock()
    {
        int top = 0;
        loop:
        for(int i = 0;i<4;i++)
        {
            for(int k=0;k<4;k++)
            {
                if(block[i][k])
                {
                    top = i;
                    break loop;
                }
            }
        }

        int bottom = 0;
        loop:
        for(int i = 4;i>0;i--)
        {
            for(int k=0;k<4;k++)
            {
                if(block[i][k])
                {
                    bottom = i;
                    break loop;
                }
            }
        }

        int left = 0;
        loop:
        for(int i = 0;i<4;i++)
        {
            for(int k=0;k<4;k++)
            {
                if(block[k][i])
                {
                    left = i;
                    break loop;
                }
            }
        }

        int right = 0;
        loop:
        for(int i = 4;i>0;i--)
        {
            for(int k=0;k<4;k++)
            {
                if(block[k][i])
                {
                    right = i;
                    break loop;
                }
            }
        }

        boolean[][] minimizedBlock = Arrays.copyOfRange(block,top,bottom+1);
        for(int i = 0;i<minimizedBlock.length;i++)
        {
            minimizedBlock[i] = Arrays.copyOfRange(minimizedBlock[i],left,right+1);
        }
        return minimizedBlock;

    }

    public char getBlokChar()
    {
        return blokChar;
    }
}
