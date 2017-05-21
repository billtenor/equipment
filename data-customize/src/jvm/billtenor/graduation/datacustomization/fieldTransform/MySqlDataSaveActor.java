package billtenor.graduation.datacustomization.fieldTransform;

import billtenor.graduation.datacustomization.dataType.MySqlConfig;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lyj on 17-3-31.
 */
public class MySqlDataSaveActor extends BaseDataSaveActor {
    final private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    final private String host;
    final private String port;
    final private String userName;
    final private String passWord;
    final private String databaseName;
    private Connection connection;
    public MySqlDataSaveActor(
            MySqlConfig mySqlConfig,Map<String,String[]> tableNameFactIDs
    ){
        super(tableNameFactIDs);
        this.host=mySqlConfig.host;
        this.port=mySqlConfig.port;
        this.userName=mySqlConfig.username;
        this.passWord=mySqlConfig.password;
        this.databaseName=mySqlConfig.databaseName;
    }
    private String addQuot(String input){
        return "'"+input+"'";
    }

    @Override
    public void close() {
    }

    @Override
    public void init() {
        try {
            Class.forName(JDBC_DRIVER);
            String DB_URL = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
            this.connection = DriverManager.getConnection(DB_URL, userName, passWord);
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean save(List<DataSave> data, String tableName, String[] factsTitle) {
        try{
            Statement statement = connection.createStatement();
            String sqlTableTitle = tableName +"Key,dateID,spaceID,measureID";
            for(int i=0;i<factsTitle.length;i++){
                sqlTableTitle += (",fact_" + factsTitle[i]);
            }
            for(int i=0;i<data.size();i++) {
                String value = addQuot(UUID.randomUUID().toString());
                value += (","+addQuot(data.get(i).dateID));
                value += (","+addQuot(data.get(i).spaceID));
                value += (","+addQuot(data.get(i).measureID));
                for(int factIndex=0;factIndex<data.get(i).facts.length;factIndex++){
                    value+=("," + addQuot(data.get(i).facts[factIndex].toString()));
                }
                String sql = "INSERT INTO " + tableName +  " (" + sqlTableTitle + ") VALUES " + "("+ value + ");";
                statement.execute(sql);
            }
            statement.close();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
