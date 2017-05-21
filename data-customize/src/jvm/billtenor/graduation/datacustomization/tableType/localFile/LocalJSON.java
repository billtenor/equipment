package billtenor.graduation.datacustomization.tableType.localFile;

import java.io.*;

/**
 * Created by lyj on 17-3-25.
 */
public class LocalJSON implements java.io.Serializable{
    public String data;
    public void loadJSON(InputStream input){
        data=new String("");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        try{
            String line;
            while((line=bufferedReader.readLine())!=null){
                data=data+line;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public LocalJSON(String path,boolean isPath){
        try {
            InputStream input = new FileInputStream(path);
            loadJSON(input);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public LocalJSON(String fileName){
        String folder="/jsonExample";
        String dir = folder+"/"+fileName;
        InputStream input = this.getClass().getResourceAsStream(dir);
        loadJSON(input);
    }


}
