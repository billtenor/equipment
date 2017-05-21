package billtenor.graduation.datacustomization.dataType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by yanjun on 17-4-4.
 */
public class MySqlConfig {
    final public String host;
    final public String port;
    final public String username;
    final public String password;
    final public String databaseName;
    public MySqlConfig(String host,String port,String username,String password,String databaseName){
        this.host=host;
        this.port=port;
        this.username=username;
        this.password=password;
        this.databaseName=databaseName;
    }
    public MySqlConfig(String configJSON){
        JSONParser parser = new JSONParser();
        JSONObject jsonObject=null;
        try{
            jsonObject = (JSONObject) parser.parse(configJSON);
        }
        catch (ParseException e){
            e.printStackTrace();
            this.host=null;
            this.port=null;
            this.username=null;
            this.password=null;
            this.databaseName=null;
            return;
        }
        this.host=(String)jsonObject.get("host");
        this.port=(String)jsonObject.get("port");
        this.username=(String)jsonObject.get("username");
        this.password=(String)jsonObject.get("password");
        this.databaseName=(String)jsonObject.get("databaseName");
    }
}
