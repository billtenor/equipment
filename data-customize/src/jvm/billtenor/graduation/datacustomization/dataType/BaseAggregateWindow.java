package billtenor.graduation.datacustomization.dataType;

import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;
import org.apache.storm.tuple.Tuple;

import java.util.*;

/**
 * Created by lyj on 17-3-24.
 */
public abstract class BaseAggregateWindow implements java.io.Serializable{
    public List<Tuple> tuples;
    public Long createTime;
    protected Set<String> processed;
    protected Object[] state;
    protected Map<String,Integer> stateName;


    BaseAggregateWindow(String[] stateName){
        processed=new HashSet<>();
        this.state=new Object[stateName.length];
        this.stateName=new HashMap<>();
        this.tuples=new ArrayList<>();
        for(int i=0;i<stateName.length;i++){
            this.stateName.put(stateName[i],i);
        }
    }
    protected void clear(String[] stateNames){
        this.tuples.clear();
        this.processed.clear();
        this.createTime=null;
        for(String name:stateNames){
            state[stateName.get(name)]=null;
        }
    }
    public void clear(){
        this.tuples.clear();
        this.processed.clear();
        this.createTime=null;
        for(int i=0;i<state.length;i++){
            state[i]=null;
        }
    }
    public String[] getJSONDataList(){
        List<String> buff= new ArrayList<>();
        for (int i=0;i<tuples.size();i++){
            buff.add(TupleDataTransfer.getData(tuples.get(i)));
        }
        return buff.toArray(new String[buff.size()]);
    }
    public boolean isEmpty(){
        return processed.isEmpty();
    }
    public int countProcessed(){
        return processed.size();
    }
    public void addProcessed(String add){
        processed.add(add);
    }
    public boolean hasProcessed(String add){
        return processed.contains(add);
    }
    public void setState(String name,Object value){
        state[stateName.get(name)]=value;
    }
    public Object getState(String name){
        return state[stateName.get(name)];
    }
    public void print(){
        System.out.println("ProcessSize:"+Integer.toString(processed.size()));
        System.out.println("ProcessData:");
        for(String process:this.processed){
            System.out.println(process);
        }
        for(String key:stateName.keySet()){
            System.out.println(key+":"+state[stateName.get(key)].toString());
        }
        System.out.println("createTime:"+createTime.toString());
    }
}
