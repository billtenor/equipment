package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.dataType.KafkaSpoutConfig;
import billtenor.graduation.datacustomization.tableType.localFile.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by lyj on 17-4-6.
 */
public class LocalTopologyCreator extends BaseTopologyCreator {
    public LocalTopologyCreator(
            String dataWarehouseName,
            String dataSourceName,
            String dataName,
            String[] dataFactNames,
            KafkaSpoutConfig kafkaSpoutConfig,
            Long spaceAggregateWaitTime,
            Long noBlockTimeVPT,
            String kafkaBrokerHosts,
            Map<String,Integer> parallel,
            int dataSaveWindowCount,
            int dataSaveRetryCount
    ){
        super(dataWarehouseName,dataSourceName,dataName,dataFactNames,kafkaSpoutConfig,
                spaceAggregateWaitTime,noBlockTimeVPT,kafkaBrokerHosts,parallel,dataSaveWindowCount,dataSaveRetryCount);
    }
    @Override
    protected Document getXMLFile(String name) {
        LocalXML localXML=new LocalXML();
        return localXML.getXMLFile(name);
    }

    @Override
    protected Document[] getXMLFile(String[] names) {
        Document[] result = new Document[names.length];
        for(int i=0;i<names.length;i++){
            result[i]=getXMLFile(names[i]);
        }
        return result;
    }

    @Override
    protected void setDataModel() {
        this.dataModel.spaceTable = new LocalSpaceTable(
                getGrainLevelGrainID(this.dataModel.spaceDim),
                this.dataModel.getDataModelSpaceTableName(),
                getSpaceTableField(this.dataModel.spaceDim)
        );
        this.dataModel.measureTable = new LocalMeasureTable(
                this.dataModel.getDataModelMeasureTableName(),
                getMeasureTableField()
        );
        this.dataModel.factsTable = new LocalFactTable(
                this.dataModel.getDataModelFactsHeadsTableName(),
                getFactTableField()
        );
    }

    @Override
    protected void setDataSourceModel() {
        this.dataSourceModel.measureCheck=null;
        this.dataSourceModel.statusTable = new LocalNodeStatusTable(
                this.dataSourceModel.getNodeStatusTableName()
        );
    }

    @Override
    protected void setDataWarehouseModel() {
        this.dataWarehouseModel.spaceTable=new LocalSpaceTable(
                getGrainLevelGrainID(this.dataWarehouseModel.spaceConformedDim),
                this.dataWarehouseModel.getSpaceConformedTableName(),
                getSpaceTableField(this.dataWarehouseModel.spaceConformedDim)
        );
        this.dataWarehouseModel.measureTable=new LocalMeasureTable(
                this.dataWarehouseModel.getMeasureConformedTableName(),
                getMeasureTableField()
        );
        this.dataWarehouseModel.factsTable=new LocalFactTable(
                this.dataWarehouseModel.getFactsConformedTableName(),
                getFactTableField()
        );
    }
}
