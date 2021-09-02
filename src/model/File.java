package model;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class File
{

    private String path;

    File(String path)
    {
        this.path = path;
    }

    File(String path, ArrayList<String> allLines)
    {
        this.path = path;
        writeLines(allLines);
    }

    ArrayList<String> readAllLines()
    {
        try
        {
            List<String> lines = Files.readAllLines(Paths.get(path));
            ArrayList<String> result = new ArrayList<>();
            result.addAll(lines);
            return result;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void writeLines(ArrayList<String> lines)
    {
        Path file = Paths.get(path);
        try
        {
            Files.write(file, lines, Charset.forName("UTF-8"));
        }
        catch (Exception e2)
        {
            System.out.println("Can't write file");
        }
    }
}
