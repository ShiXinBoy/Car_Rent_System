package tzp.Car;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;
import static java.lang.Class.forName;


/**
 * 配置文件读取设置类
 * */
class cfg{
    protected Properties mycfg = new Properties();
    protected String path = "src/main/resources/cfg.properties";
    public void init(String filepath) throws IOException {

        if (!Objects.equals(filepath, "def")){
            path = filepath;
        }
        InputStream inputStream = new BufferedInputStream(new FileInputStream(path));
        mycfg.load(inputStream);
    }
    public String getCfg(String key) {
        return mycfg.getProperty(key);
    }
    public void setCfg(String key,String value) {
        mycfg.setProperty(key,value);
    }
    public void storeCfg(String Comment) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
        mycfg.store(out,Comment);
        out = null;
        mycfg = null;
    }
}

/**
 * JDBC类，用于操作数据库
 */
class JDBC{
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    /**
     * JDBC 初始化
     * @param dbconfig 配置文件
     * @throws ClassNotFoundException 类错误
     * @throws SQLException sql错误
     * @throws IOException io错误
     */
    public void init(String... dbconfig) throws ClassNotFoundException, SQLException, IOException {
        //DEFAULT config
        String Url;
        forName("com.mysql.cj.jdbc.Driver");
//        = "jdbc:mysql://" + dbhost + ":" + dbport + "/" + dbname;
        if (Objects.equals(dbconfig[0], "def")){
            cfg dbdef = new cfg();
            dbdef.init("def");
            Url = "jdbc:mysql://" + dbdef.getCfg("dbhost")+":"+ dbdef.getCfg("dbport")+"/"+ dbdef.getCfg("dbname");
            conn = DriverManager.getConnection(Url,dbdef.getCfg("dbuser"),dbdef.getCfg("dbpass"));
            dbdef.storeCfg("success");
        }
        if (Objects.equals(dbconfig[0], "notdef")){
            Url = "jdbc:mysql://" + dbconfig[1]+":"+ dbconfig[2]+"/"+ dbconfig[3];
            conn = DriverManager.getConnection(Url,dbconfig[4],dbconfig[5]);
        }
    }

    /**
     * 尝试查询语句
     * @param sqlcode 查询语句内容
     * @param strs 变量列表，支持>= 0
     * @return ResultSet对象
     * @throws SQLException SQL错误
     */
    public ResultSet tryQuery(String sqlcode ,String... strs) throws SQLException {
//        查询
        stmt = conn.prepareStatement(sqlcode);
        for (int i = 0; i < strs.length; i++) {
            stmt.setString(i+1, strs[i]);
        }
        rs = stmt.executeQuery(); // 执行查询并返回结果集
        return rs;
    }
    /**
     * 执行语句
     * @param sqlcode 语句内容
     * @param strs 变量列表，支持>= 0
     * @return stmt.executeUpdate() 操作后数据库受影响的数据行数
     * @throws SQLException SQL错误
     */
    public int tryUpdate(String sqlcode, String... strs) throws SQLException{
        stmt = conn.prepareStatement(sqlcode);
        for (int i = 0; i < strs.length; i++) {
            stmt.setString(i+1, strs[i]);
        }
        return stmt.executeUpdate();
    }

    /**
     * 获取ResultSet对象
     * @return ResultSet 对象
     */
    public ResultSet rs(){
        return rs;
    }

    /**
     * 关闭数据库连接
     */
    public void Close() {
//        关闭连接
        try {
            if (stmt != null){
                stmt.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        try {
            if (rs != null){
                rs.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        try {
            if (conn != null){
                conn.close();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

/**
* 汽车类,创建后务必使用一种init方法初始化
* */
class Car{
    private final debug debug = new debug(true);
    public String CarCard;
    private String CarName,CarValue,CarsitNum,Carstatus;

    Car(){}

    /**
     * 通过字段从数据库初始化
     * @param CarCard 查询条件
     * @throws SQLException sql错误
     * @throws IOException io错误
     * @throws ClassNotFoundException 类错误
     */
    public void InitFromDB(String CarCard) throws SQLException, IOException, ClassNotFoundException {
        JDBC carinit = new JDBC();
        carinit.init("def");

        carinit.tryQuery("select * from cardb where carcard =?;",CarCard);
        if (carinit.rs.next()){
            this.CarCard = carinit.rs.getString("carcard");
            this.CarName = carinit.rs.getString("carname");
            this.CarsitNum = carinit.rs.getString("carssitnum");
            this.CarValue = carinit.rs.getString("carvalue");
            this.Carstatus = carinit.rs.getString("carstatus");
        }else {
            debug.print("Error to init by carName!");
        }
    }

    /**
     * 通过字符串数组初始化
     * @param Data 字符串数组
     */
    public void InitFromStrs(String[] Data){
        this.CarCard = Data[0];
        this.CarName= Data[1];
        this.CarsitNum= Data[2];
        this.CarValue= Data[3];
        this.Carstatus= Data[4];
    }

    /**
     * 以字符串数组方式返回对象属性
     * @return 属性
     */
    public String[] GetCar(){
        return new String[]{
                this.CarCard,
                this.CarName,
                this.CarsitNum,
                this.CarValue,
                this.Carstatus
        };
    }

    /**
     * 判断本对象是否存在于数据库，使用前必须完成初始化
     * @return result
     * @throws SQLException sql错误
     * @throws IOException io错误
     * @throws ClassNotFoundException 类错误
     */
    public boolean isExist() throws SQLException, IOException, ClassNotFoundException {
        JDBC iscar = new JDBC();
        iscar.init("def");
        return iscar.tryQuery("SELECT * FROM cardb where carcard = ?;", this.CarCard).next();
    }

    /**
     * 保存当前对象到数据库，使用前必须初始化
     * @return 返回int类型为数据库中受到影响的数据行数
     * @throws SQLException sql
     * @throws IOException io
     * @throws ClassNotFoundException class
     */
    public int SavetoDB() throws SQLException, IOException, ClassNotFoundException {
//        汽车不存在
        JDBC jdbc = new JDBC();
        jdbc.init("def");

        return jdbc.tryUpdate(
                "insert into cardb (carcard,carname,carsitnum,carvalue,carstatus) values(?,?,?,?,?);",
                this.CarCard, this.CarName, this.CarsitNum, this.CarValue, this.Carstatus);
    }

    /**
     * 更新当前对象到数据库，使用前必须初始化
     * @return 返回int类型为数据库中受到影响的数据行数
     * @throws SQLException sql
     * @throws IOException io
     * @throws ClassNotFoundException class
     */
    public int UpdatetoDB() throws SQLException, IOException, ClassNotFoundException {
        //            汽车已经存在
        JDBC jdbc = new JDBC();
        jdbc.init("def");
        return jdbc.tryUpdate("update cardb set carname = ?,carsitnum = ?,carvalue = ? ,carstatus =  ?where carcard = ?;",
                this.CarName,
                this.CarsitNum,
                this.CarValue,
                this.Carstatus,
                this.CarCard
        );
    }
}

/**
 * 表格操作
 *
 * @author TVT
 */
class Table{
    JTable JTable;
    String[] ColName;
    Object [][] rows;
    DefaultTableModel model;

    /**
     * 初始化Table
     * @param JTable    Table对象
     * @param columnsName   标题字段
     */
    Table(JTable JTable,String[] columnsName){
        this.JTable = JTable;
        this.ColName = columnsName;
        model = new DefaultTableModel(rows,columnsName);
        JTable.setModel(model);
//      禁用选择列
        JTable.setColumnSelectionAllowed(false);
//      设置字体以及对齐
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.LEFT);
        JTable.getTableHeader().setDefaultRenderer(renderer);
    }
    /**
     * 添加行
     * @param row 行内容
     */
    public void AddRow(Object[] row){
        model.addRow(row);
    }

    /**
     * 删除行内容
     * @param rowIndex 行的索引
     */
    public void DropRow(int rowIndex){
        model.removeRow(rowIndex);
    }
    public void DropAllRows(){
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }
    /**
     * 获取单元格内容
     * @param row index of row
     * @param column index of column
     * @return object to string
     */
    public String GetValue(int row , int column){
       Object rs = model.getValueAt(row, column);
       return rs.toString();
    }
    /**
     * 获取字符串的位置
     * @return location
     */
    public int getRowLocal(){
        return JTable.getSelectedRow();
    }
}

/**
 * debug类
 */
class debug{
    private boolean isdebug;
    public debug(){}
    public debug(boolean isdebug){
        this.isdebug = isdebug;
    }
    public boolean isdebug(){
        return isdebug;
    }
    public void setdebug(boolean isdebug){
        this.isdebug = isdebug;
    }
    public void Debug(String msg){
        String dclass = new Throwable().getStackTrace()[1].getClassName();
        String dmethod = new Exception().getStackTrace()[1].getMethodName();
        int dline = new Throwable().getStackTrace()[1].getLineNumber();
        String dfile = new Throwable().getStackTrace()[1].getFileName();
        String dPath = System.getProperty("user.dir");
        if (isdebug){
            System.out.println(
                    "File:"+dPath + "\\" + dfile + " (Line:" + dline +")\nClass:"+ dclass +"." + dmethod +"\n=> " + msg
            );
        }
    }
    public void print(String msg){
        String dclass = new Throwable().getStackTrace()[1].getClassName();
        String dmethod = new Exception().getStackTrace()[1].getMethodName();
        int dline = new Throwable().getStackTrace()[1].getLineNumber();
        String dfile = new Throwable().getStackTrace()[1].getFileName();
        String dPath = System.getProperty("user.dir");
        System.out.println(
                "File:"+dPath + "\\" + dfile + " (Line:" + dline +")\nClass:"+ dclass +"." + dmethod +"\n=> " + msg
        );
    }
}