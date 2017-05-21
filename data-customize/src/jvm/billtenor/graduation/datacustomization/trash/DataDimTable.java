package billtenor.graduation.datacustomization.dataType;

import billtenor.graduation.datacustomization.statusCheck.MeasureDataCreator;

/**
 * Created by lyj on 17-2-10.
 */
public class DataDimTable implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    public MeasureDataCreator[] measureDataCreators;
    public int dataDimSize;
    public DataDimTable(int n){
        this.measureDataCreators = new MeasureDataCreator[n];
        this.dataDimSize=n;
    }
}
