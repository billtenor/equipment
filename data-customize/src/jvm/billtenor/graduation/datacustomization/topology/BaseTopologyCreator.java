package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.bolt.*;
import billtenor.graduation.datacustomization.dataType.KafkaSpoutConfig;
import billtenor.graduation.datacustomization.dataType.MySqlConfig;
import billtenor.graduation.datacustomization.fieldTransform.MySqlDataSaveActor;
import billtenor.graduation.datacustomization.fieldTransform.NoneDataSaveActor;
import billtenor.graduation.datacustomization.spout.DataSourceScheme;
import billtenor.graduation.datacustomization.statusCheck.StatusCheck;
import billtenor.graduation.datacustomization.xmlModel.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.storm.topology.TopologyBuilder;
import org.w3c.dom.Document;

import java.util.*;

/**
 * Created by yanjun on 17-4-4.
 */
public abstract class BaseTopologyCreator {
    final private KafkaSpoutConfig kafkaSpoutConfig;
    final private Long aggregateTimeRange;
    final private Long noBlockTimeVPT;
    final private Properties kafkaProducerProp;
    final private Map<String,Integer> parallel;
    final private int dataSaveWindowCount;
    final private int dataSaveRetryCount;

    //for space table usage:
    protected String getSpaceTableField(DimGrain[] dimGrains){
        String result="";
        for(int i=0;i<dimGrains.length;i++){
            if(i!=0)result+=",";
            result+=dimGrains[i].dimGrainKey;
        }
        return result;
    }
    protected Map<Integer,String> getGrainLevelGrainID(DimGrain[] dimGrains){
        Map<Integer,String> result = new HashMap<>();
        for(int i=0;i<dimGrains.length;i++){
            result.put(dimGrains[i].dimGrainLevel,dimGrains[i].dimGrainKey);
        }
        return result;
    }
    //for measure table usage:
    protected String getMeasureTableField(){
        return "MeasureID,MeasureType,MeasureConstraints,MeasureRely,MeasureEquation";
    }
    //for fact table usage:
    protected String getFactTableField(){
        return "*";
    }

    protected DataWarehouseModel dataWarehouseModel;
    protected DataSourceModel dataSourceModel;
    protected DataModel dataModel;
    protected DataModelFactsTable[] dataModelFactsTables;
    protected List<StormComponent> componentQueue;

    protected abstract Document getXMLFile(String name);
    protected abstract Document[] getXMLFile(String[] names);
    protected abstract void setDataWarehouseModel();
    protected abstract void setDataSourceModel();
    protected abstract void setDataModel();

    private void setDataWarehouseModel(Document dataWarehouse){
        this.dataWarehouseModel=new DataWarehouseModel();
        this.dataWarehouseModel.setFromXML(dataWarehouse);
        setDataWarehouseModel();
    }
    private void setDataSourceModel(Document dataSourceModel){
        this.dataSourceModel=new DataSourceModel();
        this.dataSourceModel.setFromXML(dataSourceModel);
        setDataSourceModel();
    }
    private void setDataModel(Document dataModel){
        this.dataModel=new DataModel();
        this.dataModel.setFromXML(dataModel);
        setDataModel();
    }
    private void setDataModelFactsTable(Document document,int i){
        this.dataModelFactsTables[i]=new DataModelFactsTable();
        this.dataModelFactsTables[i].setFromXML(document);
    }
    private void setDataModelFactsTables(Document[] dataModelFactsTables){
        this.dataModelFactsTables=new DataModelFactsTable[dataModelFactsTables.length];
        for(int i=0;i<dataModelFactsTables.length;i++){
            setDataModelFactsTable(dataModelFactsTables[i],i);
        }
    }
    private void addToComponentQueue(StormComponent stormComponent){
        int size = this.componentQueue.size();
        if(size!=0) {
            this.componentQueue.get(size - 1).setKeyTransform(stormComponent.getKeyTransform());
        }
        this.componentQueue.add(stormComponent);
    }
    private void setComponentQueue(){
        //KafkaSpoutComponent
        StormComponent kafkaSpoutComponent = new KafkaSpoutComponent(this.parallel.get("spout"),this.kafkaSpoutConfig,new DataSourceScheme(
                this.dataWarehouseModel.measureTable.getMeasureIDMeasureType()
        ));
        addToComponentQueue(kafkaSpoutComponent);

        //measureTransformBoltComponent
        Set<String> prepareMeasureID = this.dataModel.measureTable.getMeasureID();
        Set<String> DoneMeasureID = this.dataModel.measureTable.getFreeMeasureID();
        prepareMeasureID.removeAll(DoneMeasureID);
        Set<String> toDoMeasureID = new HashSet<>();
        Set<String> buff=new HashSet<>();
        while(prepareMeasureID.size()!=0){
            buff.clear();
            for(String measureID:prepareMeasureID){
                String[] measureRely = this.dataModel.measureTable.getRelyMeasureID(measureID);
                boolean measureIDAlready = true;
                for (String relyMeasureID : measureRely) {
                    if (!DoneMeasureID.contains(relyMeasureID)) {
                        measureIDAlready=false;
                        break;
                    }
                }
                if(measureIDAlready){
                    toDoMeasureID.add(measureID);
                    buff.add(measureID);
                }
            }
            for(String measureID:buff){
                prepareMeasureID.remove(measureID);
            }
            StormComponent measureTransformBoltComponent = new MeasureTransformBoltComponent(this.parallel.get("transform"),new MeasureTransformBolt(
                    this.dataModel.measureTable.getMeasureTargetIDMeasureEquation(DoneMeasureID,toDoMeasureID),
                    this.dataModel.measureTable.getMeasureIDMeasureType()
            ));
            DoneMeasureID.addAll(toDoMeasureID);
            toDoMeasureID.clear();
            addToComponentQueue(measureTransformBoltComponent);
        }

        //SpaceBlockAggregateBoltComponent
        List<StormComponent> aggregateQueue = new ArrayList<>();
        Set<String> factIDs = new HashSet<>();
        for(int i=0;i<this.dataModelFactsTables.length;i++){
            for(int j=0;j<this.dataModelFactsTables[i].factIDs.length;j++){
                factIDs.add(this.dataModelFactsTables[i].factIDs[j]);
            }
        }
        boolean isNotRoot;
        Map<String,String[]> sourceFactIDTargetFactIDs;
        do {
            sourceFactIDTargetFactIDs = dataModel.factsTable.getFactIDsTargetFactIDs(factIDs);
            isNotRoot = !sourceFactIDTargetFactIDs.keySet().contains("");
            if(isNotRoot){
                String sourceFactIDSample = sourceFactIDTargetFactIDs.keySet().iterator().next();
                String targetFactIDSample = sourceFactIDTargetFactIDs.get(sourceFactIDSample)[0];
                int sourceSpaceGrain = dataModel.factsTable.getSpaceGrain(sourceFactIDSample);
                int targetSpaceGrain = dataModel.factsTable.getSpaceGrain(targetFactIDSample);
                Long sourceTimeGap = dataModel.factsTable.getTimeGap(sourceFactIDSample);
                Long targetTimeGap = dataModel.factsTable.getTimeGap(targetFactIDSample);
                if(sourceSpaceGrain!=targetSpaceGrain){
                    StormComponent spaceAggregateBoltComponent = new SpaceBlockAggregateBoltComponent(this.parallel.get("aggregate"),new SpaceBlockAggregateBolt(
                            sourceTimeGap,this.aggregateTimeRange,sourceSpaceGrain,targetSpaceGrain,
                            dataModel.factsTable.getDefaultTimeStampFactID(factIDs),
                            sourceFactIDTargetFactIDs,
                            this.dataModel.factsTable.getFactsIDFunction(),
                            this.dataModel.measureTable.getMeasureIDMeasureType(),
                            this.dataModel.spaceTable.getLowGrainKeyHighGrainKey(sourceSpaceGrain,targetSpaceGrain),
                            new StatusCheck(
                                    sourceSpaceGrain,targetSpaceGrain,
                                    this.dataModel.spaceTable,this.dataSourceModel.statusTable
                            )
                    ));
                    aggregateQueue.add(spaceAggregateBoltComponent);
                }
                else if(sourceTimeGap!=targetTimeGap){
                    if(targetTimeGap>noBlockTimeVPT){
                        StormComponent timeNoBlockAggregateSpoutComponent = new TimeNoBlockAggregateSpoutComponent(
                                this.parallel.get("aggregate"),new KafkaSpoutConfig(this.kafkaSpoutConfig,this.dataModel.getBuffKafkaTopicName())
                        );
                        aggregateQueue.add(timeNoBlockAggregateSpoutComponent);

                        StormComponent timeNoBlockAggregateBoltComponent = new TimeNoBlockAggregateBoltComponent(this.parallel.get("aggregate"),new TimeNoBlockAggregateBolt(
                                sourceTimeGap, this.aggregateTimeRange, targetTimeGap,
                                dataModel.factsTable.getDefaultTimeStampFactID(factIDs),
                                sourceFactIDTargetFactIDs,
                                this.dataModel.factsTable.getFactsIDFunction(),
                                this.dataModel.measureTable.getMeasureIDMeasureType(),
                                this.kafkaProducerProp,
                                this.dataModel.getBuffKafkaTopicName()
                        ));
                        aggregateQueue.add(timeNoBlockAggregateBoltComponent);
                    }
                    else {
                        StormComponent timeAggregateBoltComponent = new TimeBlockAggregateBoltComponent(this.parallel.get("aggregate"), new TimeBlockAggregateBolt(
                                sourceTimeGap, this.aggregateTimeRange, targetTimeGap,
                                dataModel.factsTable.getDefaultTimeStampFactID(factIDs),
                                sourceFactIDTargetFactIDs,
                                this.dataModel.factsTable.getFactsIDFunction(),
                                this.dataModel.measureTable.getMeasureIDMeasureType()
                        ));
                        aggregateQueue.add(timeAggregateBoltComponent);
                    }
                }
                factIDs = sourceFactIDTargetFactIDs.keySet();
            }
        }while(isNotRoot);
        for(int i=aggregateQueue.size()-1;i>=0;i--){
            addToComponentQueue(aggregateQueue.get(i));
        }

        //DataSaveBoltComponent
        Map<String,String[]> tableNameFactIDs = new HashMap<>();
        for(int i=0;i<this.dataModelFactsTables.length;i++) {
            tableNameFactIDs.put(
                    dataModelFactsTables[i].getFactsTableName(),
                    dataModelFactsTables[i].factIDs
            );
        }
        if(this.dataModel.dataModelEngine.equals("mysql")) {
            MySqlConfig mySqlConfig = new MySqlConfig(dataModel.dataModelPara);
            StormComponent dataSaveBoltComponent = new DataSaveBoltComponent(this.parallel.get("save"), this.dataSaveWindowCount, new DataSaveBolt(
                    this.dataSaveRetryCount, new MySqlDataSaveActor(
                            mySqlConfig,tableNameFactIDs
            )));
            addToComponentQueue(dataSaveBoltComponent);
        }
        else if(this.dataModel.dataModelEngine.equals("none")){
            StormComponent dataSaveBoltComponent =  new DataSaveBoltComponent(this.parallel.get("save"), this.dataSaveWindowCount,new DataSaveBolt(
                    this.dataSaveRetryCount, new NoneDataSaveActor()
            ));
            addToComponentQueue(dataSaveBoltComponent);
        }
    }
    public BaseTopologyCreator(
            String dataWarehouseName,
            String dataSourceName,
            String dataName,
            String[] dataFactNames,
            KafkaSpoutConfig kafkaSpoutConfig,
            Long aggregateTimeRange,
            Long noBlockTimeVPT,
            String kafkaBrokerHosts,
            Map<String,Integer> parallel,
            int dataSaveWindowCount,
            int dataSaveRetryCount
    ){
        this.kafkaSpoutConfig=kafkaSpoutConfig;
        this.componentQueue=new ArrayList<>();
        this.aggregateTimeRange=aggregateTimeRange;
        this.noBlockTimeVPT=noBlockTimeVPT;
        this.parallel=parallel;
        this.dataSaveWindowCount=dataSaveWindowCount;
        this.dataSaveRetryCount=dataSaveRetryCount;

        setDataWarehouseModel(getXMLFile(dataWarehouseName));
        setDataSourceModel(getXMLFile(dataSourceName));
        setDataModel(getXMLFile(dataName));
        setDataModelFactsTables(getXMLFile(dataFactNames));

        this.kafkaProducerProp=new Properties();
        this.kafkaProducerProp.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaBrokerHosts);
        this.kafkaProducerProp.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        this.kafkaProducerProp.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        this.kafkaProducerProp.put(ProducerConfig.CLIENT_ID_CONFIG, this.dataModel.dataModelID);
        setComponentQueue();
    }
    public TopologyBuilder buildConsumerTopology(){
        TopologyBuilder builder = new TopologyBuilder();
        String previousName="";
        for(int i=0;i<componentQueue.size();i++){
            componentQueue.get(i).addToTopology(builder,previousName);
            previousName = componentQueue.get(i).componentName;
        }
        return builder;
    }
    public TopologyBuilder buildConsumerTopology(int end){
        TopologyBuilder builder = new TopologyBuilder();
        String previousName="";
        for(int i=0;i<end;i++){
            componentQueue.get(i).addToTopology(builder,previousName);
            previousName = componentQueue.get(i).componentName;
        }
        return builder;
    }
}
