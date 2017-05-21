package billtenor.graduation.datacustomization.xmlModel;

/**
 * Created by lyj on 17-5-11.
 */
public class DimGrain implements java.io.Serializable{
    public String dimGrainKey;
    public int dimGrainLevel;
    public String[] dims;
    public DimGrain(String dimGrainKey,int dimGrainLevel,String[] dims){
        this.dimGrainKey=dimGrainKey;
        this.dimGrainLevel=dimGrainLevel;
        this.dims=dims;
    }
}
