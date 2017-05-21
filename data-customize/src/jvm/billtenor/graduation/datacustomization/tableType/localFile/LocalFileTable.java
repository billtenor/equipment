package billtenor.graduation.datacustomization.tableType.localFile;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lyj on 17-3-25.
 */
public class LocalFileTable implements java.io.Serializable{
    public Table<String,String,String> data;
    private String[] splitStringBy(char by,String s){
        final String[] step1=s.split("\"");
        final String splitBy=new String(new char[]{by});
        final int splitIndexMod;
        if(s.indexOf("\"")==0){
            splitIndexMod=1;
        }
        else {
            splitIndexMod=0;
        }
        ArrayList<String> step2=new ArrayList<String>(){{
            String[] buff;
            if(step1.length==1){
                buff = step1[0].split(splitBy);
                for (int j = 0; j < buff.length; j++) {
                    add(buff[j]);
                }
            }
            else {
                for (int i = 0; i < step1.length; i++) {
                    if (i % 2 == splitIndexMod) {
                        int size = step1[i].length();
                        if (size != 1) {
                            if (i == 0) {
                                buff = step1[i].substring(0, size - 1).split(splitBy);
                            } else if (i == step1.length) {
                                buff = step1[i].substring(1, size).split(splitBy);
                            } else {
                                buff = step1[i].substring(1, size - 1).split(splitBy);
                            }
                            for (int j = 0; j < buff.length; j++) {
                                add(buff[j]);
                            }
                        }
                    } else {
                        if (step1[i].equals("."))
                            add("");
                        else
                            add(step1[i]);
                    }
                }
            }
        }};
        return step2.toArray(new String[step2.size()]);
    }
    public LocalFileTable(String folder,String fileName,String field){
        data=HashBasedTable.create();
        String dir = folder+"/"+fileName;
        InputStream input = this.getClass().getResourceAsStream(dir);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        try{
            String line=bufferedReader.readLine();
            String[] header=splitStringBy(' ',line);
            Set<String> readColumn=new HashSet<>();
            if(field.equals("*")){
                for(int i=1;i<header.length;i++)
                    readColumn.add(header[i]);
            }
            else{
                String[] readColumnList=field.split(",");
                for(int i=0;i<readColumnList.length;i++)
                    readColumn.add(readColumnList[i]);
            }
            while((line=bufferedReader.readLine())!=null){
                if(!readColumn.isEmpty()){
                    String[] buff=splitStringBy(' ',line);
                    for(int i=1;i<buff.length;i++){
                        if(readColumn.contains(header[i])) {
                            data.put(buff[0], header[i], buff[i]);
                        }
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
