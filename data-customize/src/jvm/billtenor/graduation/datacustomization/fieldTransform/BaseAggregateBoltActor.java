package billtenor.graduation.datacustomization.fieldTransform;


import billtenor.graduation.datacustomization.dataType.GlobalConfig;

import java.util.*;

/**
 * Created by lyj on 17-3-27.
 */
public abstract class BaseAggregateBoltActor implements java.io.Serializable {
    protected final static Map<String,String[]> functionType=AggregateCalculate.functionType;
    protected final static Map<String,Set<String>> functionTypeMatchMeasureType= GlobalConfig.dataTypeMap;
    protected final Map<String,String> factIDFunction;
    protected final Map<String,String> measureIDMeasureType;
    protected final Map<String,String[]> factIDTargetFactIDs;

    public BaseAggregateBoltActor(Map<String,String> factIDFunction,
                                  Map<String,String> measureIDMeasureType,
                                  Map<String,String[]> factIDTargetFactIDs){
        this.factIDFunction=factIDFunction;
        this.measureIDMeasureType=measureIDMeasureType;
        this.factIDTargetFactIDs=factIDTargetFactIDs;
    }
    protected boolean measureMatchFact(String measureID,String factID,String aggressiveType){
        String measureType = measureIDMeasureType.get(measureID);
        String[] targetFactIDs=factIDTargetFactIDs.get(factID);
        if(targetFactIDs==null){
            return false;
        }
        else {
            //all in the same patten,so use the first one to judge
            String targetFactID=targetFactIDs[0];
            String[] buff=functionType.get(factIDFunction.get(targetFactID));
            String targetFactIDFunctionType=buff[0];
            final String targetFactIDAggressiveType=buff[1];
            Set<String> aggressiveTypes = new HashSet<String>(){{
                String[] buff = targetFactIDAggressiveType.split(",");
                for(int i=0;i<buff.length;i++)
                    add(buff[i]);
            }};
            boolean aggressiveTypeMatch = aggressiveTypes.contains(aggressiveType);
            boolean measureTypeMatch = functionTypeMatchMeasureType.get(targetFactIDFunctionType).contains(measureType);
            return aggressiveTypeMatch && measureTypeMatch;
        }
    }
}
