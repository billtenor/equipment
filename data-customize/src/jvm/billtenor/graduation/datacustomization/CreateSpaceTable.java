package billtenor.graduation.datacustomization;

import java.io.*;

/**
 * Created by lyj on 17-5-11.
 */
public class CreateSpaceTable {
    private final String folder="multilang/localDatabase";
    public int size;
    public int countPerArea;
    public String name;
    public CreateSpaceTable(int size,int countPerArea,String name){
        this.size=size;
        this.countPerArea=countPerArea;
        this.name=name;
    }
    public void create() throws IOException{
        String filePath=folder+"/"+name+"_SpaceConformedDimTable.txt";
        File file = new File(filePath);
        synchronized (file) {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write("SpaceID SpaceName SpaceDescription AreaID AreaName\n");
            int areaID=0;
            int areaCount=0;
            for(int i=0;i<size;i++){
                fileWriter.write(String.format("%d \".\" \".\" %d \".\"\n",i,areaID));
                if(++areaCount>=countPerArea){
                    areaID++;
                    areaCount=0;
                }
            }
            fileWriter.close();
        }
    }
    public void active(int count) throws IOException{
        String filePath=folder+"/"+name+"_NodeStatusTable.txt";
        File file = new File(filePath);
        synchronized (file) {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write("SpaceID status\n");
            for(int i=0;i<count;i++){
                fileWriter.write(String.format("%d true\n",i));
            }
            for(int i=count;i<this.size;i++) {
                fileWriter.write(String.format("%d false\n",i));
            }
            fileWriter.close();
        }
    }
}
