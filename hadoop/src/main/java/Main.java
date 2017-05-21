import Common.TextFileWord;
import WordCount.WordCount;
import org.apache.commons.collections.map.HashedMap;

/**
 * Created by lyj on 17-5-1.
 */
public class Main {
    public static void main(final String[] args) throws Exception{
        boolean run;
        if(args[0].equals("wordCount")) {
            run = WordCount.run(new HashedMap() {{
                put("name", "mywordcount");
                put("tmp", "/tmp");
                put("input", "/input");
                put("output", "/output");
            }}, new TextFileWord());
            System.exit(run ? 0 : 1);
        }
        else{
            System.out.println("Tool not support yet!");
        }
    }
}
