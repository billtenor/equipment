package billtenor.graduation.datacustomization.statusCheck;

import java.util.Random;

/**
 * Created by lyj on 17-2-10.
 */
public class MeasureDataCreator implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    public String dataDimTableMajorKey;
    public String dataDimName;
    public String dataDimDescription;
    public String dataType;
    public String dataConstraints;
    public String dataUnit;

    public MeasureDataCreator(){
        this.dataDimTableMajorKey="";
        this.dataDimName="";
        this.dataDimDescription="";
        this.dataType="";
        this.dataConstraints="";
        this.dataUnit="";
    }

    public MeasureDataCreator(String dataDimTableMajorKey, String dataDimName, String dataDimDescription, String dataType, String dataConstraints, String dataUnit){
        this.dataDimTableMajorKey=dataDimTableMajorKey;
        this.dataDimName=dataDimName;
        this.dataDimDescription=dataDimDescription;
        if(dataType.equals("Long")){
            this.dataType=Long.class.getName();
        }
        else if(dataType.equals("Double")){
            this.dataType=Double.class.getName();
        }
        else if(dataType.equals("Boolean")){
            this.dataType=Boolean.class.getName();
        }
        else if(dataType.equals("String")){
            this.dataType=String.class.getName();
        }
        this.dataConstraints=dataConstraints;
        this.dataUnit=dataUnit;
    }
    boolean checkMinMax(char left,char right,Number min,Number max,Number value){
        if(left=='('&&value==min){
            return false;
        }
        else if(right==')'&&value==max){
            return false;
        }
        else{
            return true;
        }
    }
    public Object sampleData(Random rand){
        String[] args=this.dataConstraints.split(" ");
        if(args[0].equals("range")){
            char left=args[1].charAt(0);
            char right=args[1].charAt(args[1].length()-1);
            String[] num=args[1].substring(1,args[1].length()-1).split(",");
            boolean check=false;
            if(this.dataType.equals(Long.class.getName())){
                int max = Integer.parseInt(num[1]);
                int min = Integer.parseInt(num[0]);
                Long sample=0L;
                while(!check){
                    sample = (long)(min + rand.nextInt(max+1-min));
                    check = checkMinMax(left,right,min,max,sample);
                }
                return sample;
            }
            else if(this.dataType.equals(Double.class.getName())){
                double max = Double.parseDouble(num[1]);
                double min = Double.parseDouble(num[0]);
                Double sample=0.0;
                while(!check){
                    sample = min + (max-min) * rand.nextDouble();
                    check=checkMinMax(left,right,min,max,sample);
                }
                return sample;
            }
        }
        else if(args[0].equals("in")){
            String[] choice=args[1].substring(1,args[1].length()-1).split(",");
            int sampleIndex=rand.nextInt(choice.length);
            if(this.dataType.equals(Boolean.class.getName())){
                Boolean sample=Boolean.parseBoolean(choice[sampleIndex]);
                return sample;
            }
            else if(this.dataType.equals(String.class.getName())){
                String sample=choice[sampleIndex];
                return sample;
            }
            else if(this.dataType.equals(Long.class.getName())){
                Long sample=Long.parseLong(choice[sampleIndex]);
                return sample;
            }
            else if(this.dataType.equals(Double.class.getName())){
                Double sample=Double.parseDouble(choice[sampleIndex]);
                return sample;
            }
        }
        return null;
    }
}
