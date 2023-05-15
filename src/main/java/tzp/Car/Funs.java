package tzp.Car;

import java.io.*;
import java.util.Properties;

import static com.sun.management.VMOption.Origin.CONFIG_FILE;

class cfg{
    /*
    * 配置文件读取设置类
    * */

    protected Properties mycfg = new Properties();

    protected String path = "src/main/resources/cfg.properties";

    public Properties init( String filepath) throws IOException {

        if (filepath != "def"){
            path = filepath;
        }
        InputStream inputStream = new BufferedInputStream(new FileInputStream(path));
        mycfg.load(inputStream);
        return mycfg;
    }
    public String getCfg(String key) throws IOException{
        return mycfg.getProperty(key);
    }
    public void setCfg(String key,String value) throws IOException {
        mycfg.setProperty(key,value);
    }
    public void storeCfg() throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
        mycfg.store(out,"stored");
    }
}
class MySQL{

}