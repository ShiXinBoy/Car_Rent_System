package tzp.Car;

import com.formdev.flatlaf.FlatIntelliJLaf;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Queue;

public class Form {
// =========================  界面对象创建   =================================================
    public JPanel Main;
    public JTable AdminCarsListTable;
    public JButton CarDropOKButton;
    public JTextField CarCfgNameEdit;
    public JTextField CarCfgValueEdit;
    public JTextField CarCfgSitNumEdit;
    public JTextField CarCfgStatusEdit;
    public JButton CarCfgOkButton;
    public JButton AdminCarsListReloadButton;
    public JPanel AdminFrame;
    public JTextField CarCfgCardEdit;
    public JTable RequestsTable;
    public JButton RequestsListReloadButton;
    public JButton RequestsNoButton;
    public JButton RequestsYesButton;
    public JButton OpenRegisterButton;
    public JTextField LoginUserNameEdit;
    public JPanel ACFrame;
    public JButton LoginButton;
    public JPasswordField LoginUserPasswordEdit;
    public JPanel RegisterFrame;
    public JPanel LoginFrame;
    public JTextField RegUserIdEdit;
    public JPasswordField RegPsdEdit;
    public JButton RegisterButton;
    public JButton CancelRegButton;
    public JTable UserCarsListTable;
    public JPanel UserFrame;
    public JButton UserCarsListReloadButton;
    public JButton RentSendButton;
    public JButton AdminAccountInfoButton;
    public JButton AdminLogoutButton;
    public JButton UserAccountInfoButton;
    public JButton UserLogoutButton;
    public JTextField AccountUserIdEdit;
    public JPanel AccountFrame;
    public JPasswordField AccountNewPsd1;
    public JPasswordField AccountNewPsd2;
    public JButton AccountOKButton;
    public JButton AccountCancelButton;
    public JPasswordField AccountOldPsdEdit;
    public JButton AccountDropButton;
    public JPasswordField RegCheckPsdEdit;
    public JPanel CarsConfigFrame;
    public JPanel AdminCarsListFrame;
    public JPanel AdminAccountFrame;
    public JPanel RequestsListFrame;
    public JPanel UserCarListFrame;
    public JScrollPane AdminCarsListScroll;
    public JScrollPane RequestsScroll;
    public JScrollPane UserCarListScroll;

// =============================  全局对象  ====================================
    public String LogedUID;
    public debug debug = new debug(true);
    JFrame frame = new JFrame("小橘子の汽车租赁系统");
    String[] CarRows = new String[]{"车牌号码","车辆名称", "座位数量", "租金 元/天", "当前状态"};
    String[] RequestRows = new String[]{"租用账号","租用车辆","租用时长"};
    Table ObjectATB= new Table(AdminCarsListTable,CarRows);
    Table ObjectUTB = new Table(UserCarsListTable,CarRows);
    Table ObjectRTB = new Table(RequestsTable,RequestRows);


    public Form(){

//============================  页面交互  =====================================
//----------------------------登陆页面--------------------------------------
//        登陆
        LoginButton.addActionListener (e -> {
            String UID = LoginUserNameEdit.getText();
            String Password = new String(LoginUserPasswordEdit.getPassword());

            if (UID.length() == 0 || Password.length() == 0) {
                ShowMsg("错误","账号或密码不能为空","erroe");
                return;
            }
            try {
                Login(UID, Password);
            } catch (SQLException | IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
//        打开注册页面按钮
        OpenRegisterButton.addActionListener(e -> {
            RegisterFrame.setVisible(true);
            LoginFrame.setVisible(false);
            LoginUserPasswordEdit.setText("");
        });
//----------------------------注册页面---------------------------------------
//       取消注按钮
        CancelRegButton.addActionListener(e -> {
            RegisterFrame.setVisible(false);
            LoginFrame.setVisible(true);

            RegUserIdEdit.setText("");
            RegPsdEdit.setText("");
            RegCheckPsdEdit.setText("");
        });
//        注册按钮
        RegisterButton.addActionListener(e -> {
            String newUId = RegUserIdEdit.getText();
            String Psd1 = new String(RegPsdEdit.getPassword());
            String Psd2 = new String(RegCheckPsdEdit.getPassword());
            try {
                Register(newUId,Psd1,Psd2);
            } catch (SQLException | IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
//----------------------------账户管理页面-------------------------------------
//        确定按钮
        AccountOKButton.addActionListener(e -> {
            String EditUID = AccountUserIdEdit.getText();
            String OldPsd = new String(AccountOldPsdEdit.getPassword());
            String Psd1 = new String(AccountNewPsd1.getPassword());
            String Psd2 = new String(AccountNewPsd2.getPassword());

            if (AccountOK(EditUID,OldPsd,Psd1,Psd2) > 0){
                AccountUserIdEdit.setText("");
                AccountOldPsdEdit.setText("");
                AccountNewPsd1.setText("");
                AccountNewPsd2.setText("");
            }else {
                ShowMsg("错误","缺少必要信息！","error");
            }
        });
//        注销按钮
        AccountDropButton.addActionListener(e -> {
            String UID = AccountUserIdEdit.getText();
            String Psd = new String(AccountOldPsdEdit.getPassword());
            try {
                DropAccount(UID,Psd);
                AccountUserIdEdit.setText("");
                AccountOldPsdEdit.setText("");
                AccountNewPsd1.setText("");
                AccountNewPsd2.setText("");
            } catch (IOException | SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
//        取消按钮
        AccountCancelButton.addActionListener(e -> {
            ACFrame.setVisible(false);
            AccountFrame.setVisible(false);
            AccountUserIdEdit.setText("");
            AccountOldPsdEdit.setText("");
            AccountNewPsd1.setText("");
            AccountNewPsd2.setText("");
            try {
                if (isAdmin(LogedUID)){
                    AdminFrame.setVisible(true);
                }else{
                    UserFrame.setVisible(true);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

//----------------------------Admin页---------------------------------------
//      退出登录
        AdminLogoutButton.addActionListener(e -> {
            try {
                Logout();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
//       管理
        AdminAccountInfoButton.addActionListener(e -> {
            ACFrame.setVisible(true);
            AccountFrame.setVisible(true);

            AdminFrame.setVisible(false);
            UserFrame.setVisible(false);

            AccountUserIdEdit.setText(LogedUID);
            AccountUserIdEdit.setEditable(true);
        });
//        汽车修改提交按钮
        CarCfgOkButton.addActionListener(e -> {
            String[] carData = new String[]{
                    CarCfgCardEdit.getText(),
                    CarCfgNameEdit.getText(),
                    CarCfgSitNumEdit.getText(),
                    CarCfgValueEdit.getText(),
                    CarCfgStatusEdit.getText()
            };
            SubmitCar(carData);
        });
//        表格选择事件
        AdminCarsListTable.getSelectionModel().addListSelectionListener(e -> {
            // 处理选中事件
            int selectrow = ObjectATB.getRowLocal();
            if (selectrow > 0){
                CarCfgCardEdit.setText(ObjectATB.GetValue(selectrow,0));
                CarCfgNameEdit.setText(ObjectATB.GetValue(selectrow,1));
                CarCfgSitNumEdit.setText(ObjectATB.GetValue(selectrow,2));
                CarCfgValueEdit.setText(ObjectATB.GetValue(selectrow,3));
                CarCfgStatusEdit.setText(ObjectATB.GetValue(selectrow,4));
            }
        });
//        刷新汽车列表按钮
        AdminCarsListReloadButton.addActionListener(e -> {
            try {
                CarTableRFromDB(ObjectATB);
            } catch (IOException | ClassNotFoundException | SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
//        请求列表刷新
        RequestsListReloadButton.addActionListener(e -> {

        });
//----------------------------User页面------------------------------------
//        账户管理按钮
        UserAccountInfoButton.addActionListener(e ->  {
            ACFrame.setVisible(true);
            AccountFrame.setVisible(true);

            UserFrame.setVisible(false);
            AdminFrame.setVisible(false);

            AccountUserIdEdit.setText(LogedUID);
            AccountUserIdEdit.setEditable(false);
        });
//        退出登录按钮
        UserLogoutButton.addActionListener(e -> {
            try {
                Logout();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
//        车辆列表刷新
        UserCarsListReloadButton.addActionListener(e -> {
            try {
                CarTableRFromDB(ObjectUTB);
            } catch (IOException | ClassNotFoundException | SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

//============================  逻辑方法  =====================================

    /**
     * 注册账号
     * @param  NewUID 新建账号
     * @param  Psd1 第一次密码
     * @param  Psd2 第二次密码
     * @throws SQLException SQL错误
     * @throws IOException  IOException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public void Register(String NewUID,String Psd1,String Psd2) throws SQLException, IOException, ClassNotFoundException {
        String psd;
//      判断表单完整性
        if (NewUID.length()==0||Psd1.length()==0||Psd2.length() == 0) {
            ShowMsg("错误","缺少必要信息！","error");
            return;
        }
        if (!Objects.equals(Psd1, Psd2)) {
            ShowMsg("错误", "两次输入不同！", "error");
            return;
        }else {
            psd = Psd2;
        }
        if (NewUID.length() !=11){
//      简单判断是否为手机号，若需要准确判断，可使用正则表达式
        ShowMsg("错误","请输入正确的手机号码！","error");
        return;
        }
        JDBC ActRegCheck  = new JDBC();
        ActRegCheck.init("def");
        ActRegCheck.tryQuery("SELECT * FROM userdb WHERE userid = ?",NewUID);
        if (ActRegCheck.rs().next()){
//          查询userid
            ShowMsg("错误","账号已经存在","error");
            ActRegCheck.Close();
            return;
        }
        Account("new",NewUID,psd);
    }
    /**
     * 登陆方法
     * 完成日期：2023年5月16日
     * @param getUID user point UID
     * @param getPsd user point Psd
     * */
    public void Login(String getUID,String getPsd) throws SQLException, IOException, ClassNotFoundException {
        cfg logincfg;
        JDBC Logindb= new JDBC();
        Logindb.init("def");
        if (getUID.length() == 11){
                if (Logindb.tryQuery("select * from userdb where UserId = ?", getUID).next()) {
                   String storedUserPsd = Logindb.rs().getString("UserPsd");
                    if (isValidPassword(getPsd,storedUserPsd)){
//                        用户验证成功
                        LogedUID = getUID;
                        LoginFrame.setVisible(false);
                        ACFrame.setVisible(false);
                        AdminFrame.setVisible(false);
                        UserFrame.setVisible(true);
                        Logindb.Close();
                        CarTableRFromDB(ObjectUTB);
                        return;
                    }else {
                        ShowMsg("错误","账号或密码错误！","error");
                        LoginUserPasswordEdit.setText("");
                    }
                }
            }
            logincfg = new cfg();
            logincfg.init("def");
            if (Objects.equals(getUID, logincfg.getCfg("AdminName")) && isValidPassword(getPsd, logincfg.getCfg("AdminPassword"))) {
//                管理员验证成功        
                LogedUID = getUID;
                LoginFrame.setVisible(false);
                ACFrame.setVisible(false);
                AdminFrame.setVisible(true);
                UserFrame.setVisible(false);
                CarTableRFromDB(ObjectATB);
            } else {
                ShowMsg("提示","账号或者密码错误！","error");
                LoginUserPasswordEdit.setText("");
                logincfg.storeCfg("success");
            }
    }
    /**
    * 注销登陆
    *  完成日期：2023年5月18日
     */
    public void Logout() throws IOException {
        if( isSure("提示","确定要退出？")){
            LogedUID = null;
            LoginFrame.setVisible(true);
            ACFrame.setVisible(true);
            AccountFrame.setVisible(false);
            AdminFrame.setVisible(false);
            UserFrame.setVisible(false);
            LoginUserPasswordEdit.setText("");
            if (isAdmin(LogedUID)){
                ObjectATB.DropAllRows();
                ObjectRTB.DropAllRows();
                return;
            }
            ObjectUTB.DropAllRows();

        }
    }

    /**
    * 更新账号信息，区分管理员和普通用户
    *     完成日期：2023年5月19日14:56:07
     * @param EditUID 输入框的UID
     * @param CarTableTitle 旧密码、新密码1、新密码2
    * */
    public void UpdateAccount(final String EditUID,String... CarTableTitle) throws SQLException, IOException, ClassNotFoundException {
        final String OldPsd = CarTableTitle[0];
        final String NewPsd1 = CarTableTitle[1];
        final String NewPsd2 = CarTableTitle[2];
        String NewPsd;

        String SavedPsd ;

        JDBC selectUser = new JDBC();
        selectUser.init("def");

//        判断两次密码
        if (isValidPassword(NewPsd1,NewPsd2)){
            NewPsd = NewPsd2;
        }else{
            ShowMsg("错误","两次密码不一致！","error");
            return;
        }
//        查找用户
        if ( selectUser.tryQuery("select * from userdb where userid =?;",EditUID).next()){
            SavedPsd = selectUser.rs().getString("userpsd");
        }else{
            cfg AdminINFO = new cfg();
            AdminINFO.init("def");
            if (Objects.equals(LogedUID,AdminINFO.getCfg("AdminName"))){
                SavedPsd = AdminINFO.getCfg("AdminPassword");
            }else {
                ShowMsg("错误","未找到任何匹配的用户名！","error");
                AdminINFO.storeCfg("success");
                selectUser.Close();
                return;
            }
        }

//      判断是否管理员
        if(!isAdmin(LogedUID)){
            if(!isValidPassword(OldPsd,SavedPsd)){
                ShowMsg("错误","非管理员验证旧密码失败！","error");
            }else {
                Account("update",EditUID, NewPsd);
            }
            return;
        }else {
            if (isAdmin(EditUID)){
                cfg e = new cfg();
                e.init("def");
                e.setCfg("AdminPassword",NewPsd);
                e.storeCfg("update password of administrator ");
                LogedUID = EditUID;
                return;
            }else {
                Account("update",EditUID, NewPsd);
            }
        }
//        写入数据库
        ACFrame.setVisible(false);
        AccountFrame.setVisible(false);
        if (isAdmin(LogedUID)){
            AdminFrame.setVisible(true);
            return;
        }
        UserFrame.setVisible(true);
    }

    /**
     * 账号修改确定时间
     * @param EditUID edit UID
     * @param OldPsd 旧密码
     * @param Psd1 新密码1
     * @param Psd2 新密码2
     * @return 状态
     */
    public int AccountOK(String EditUID, String OldPsd, String Psd1,String Psd2) {

        try {
            if (!isAdmin(LogedUID)){
                if (EditUID.length() == 0 || Psd1.length() == 0 || Psd2.length() == 0 || OldPsd.length() ==0){

                    return -1;
                }
            }else {
                if (EditUID.length() == 0 || Psd1.length() == 0 || Psd2.length() == 0){

                    return -1;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            UpdateAccount(EditUID,OldPsd,Psd1,Psd2);
            return 1;
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
    * 账号注销
     * @param EditUID 输入框UID
     * @param GetPsd 获得的密码
    * */
    public void DropAccount(String EditUID,String GetPsd) throws IOException, SQLException, ClassNotFoundException {
        String StoredUID = "";
        String storedPsd;

        cfg ACcfg = new cfg();
        ACcfg.init("def");
        String AdminUID = ACcfg.getCfg("AdminName");
        JDBC ACdropDB = new JDBC();
        ACdropDB.init("def");


        if (ACdropDB.tryQuery("select * from userdb where userid = ?",EditUID).next()) {
            StoredUID = ACdropDB.rs().getString("userid");
            storedPsd = ACdropDB.rs().getString("userpsd");
        }else {
            ShowMsg("错误","未找到此用户！");
            return;
        }
//        判断edituid是否为管理员
        if (Objects.equals(EditUID, AdminUID)) {
            ShowMsg("错误", "无法删除管理员账号！");
            return;
        }
//        若当前操作者不是管理员
        if (!Objects.equals(LogedUID,AdminUID)){
//            验证密码错误
            if (GetPsd.length() == 0){
                ShowMsg("错误", "非管理员需要验证密码！\n请在旧密码输入框输入密码！","error");
                return;
            }
            if (!isValidPassword(storedPsd,GetPsd)) {
                ShowMsg("错误", "密码验证失败！","error");
                return;
            }
        }
        if (isSure("提示","确定注销账号："+EditUID+"？")){
            Account("drop",EditUID,GetPsd);
            ShowMsg("提示","操作成功，即将返回登陆界面","info");
            Logout();
        }else {
            ShowMsg("提示","操作取消","info");
        }
    }
    /**
     * 账户数据库操作
     * 完成日期：2023年5月16日
     * @param med 操作：新建、更新，注销
     * @param getUID 账号
     * @param getPsd 密码
     **/
    public void Account(String med,String getUID,String getPsd) throws IOException, SQLException, ClassNotFoundException {
        debug.Debug("Account Running...\n"+ med + "->"+ getUID + ":"+ getPsd);
        JDBC AccountDB = new JDBC();
        AccountDB.init("def");

        switch (med) {
            //注册
            case "new" -> {
                if (AccountDB.tryUpdate("insert into userdb(" +
                        "UserId,UserPsd,Userstate,UserValue" +
                        ")values" +
                        "(?,?,?,?);", getUID, getPsd, "1", "0") > 0) {
                    ShowMsg("提示", "注册成功！", "info");
                }else {
                    ShowMsg("错误", "注册失败！","error");
                }
                System.out.println("tres");
            }
            // 更新密码
            case "update" -> {
                if (AccountDB.tryUpdate("UPDATE userdb SET userPsd =  ? WHERE userid = ?;",getPsd,getUID)> -0) {
                    ShowMsg("提示", "更新成功！", "info");
                }
            }
            case "drop"->{
                if (AccountDB.tryUpdate("DELETE FROM userdb WHERE userid = ?;", getUID)> -0) {
                    ShowMsg("提示", "注销成功！", "info");
                }else {
                    ShowMsg("错误", "注销失败，未知错误", "error");
                    debug.print("数据库操作失败");
                }
            }
            default -> debug.print("错误的参数！");
        }
        AccountDB.Close();
    }

    /**
     * 从数据库获取数据，并且设置到表格模型
     * @param table Object表格对象
     * @throws IOException ioException
     * @throws SQLException sqlException
     * @throws ClassNotFoundException class
     */
    public void CarTableRFromDB(Table table) throws IOException, SQLException, ClassNotFoundException {
        debug.Debug("Running TableLoad...");
        table.DropAllRows();
        JDBC CarListDB = new JDBC();
        CarListDB.init("def");
        CarListDB.tryQuery("select * FROM cardb");
        while (CarListDB.rs.next()){
            String[] rowdata = new String[]{
                    CarListDB.rs.getString("carcard"),
                    CarListDB.rs.getString("carName"),
                    CarListDB.rs.getString("carsitnum"),
                    CarListDB.rs.getString("carvalue"),
                    CarListDB.rs.getString("carstatus")
            };
            table.AddRow(rowdata);
        }
    }

    /**
     * 提交更改
     * @param carData 车辆数据
     */
    public void SubmitCar(String[] carData){
        //            若有输入框没有数据则报错
        for (String carDatum : carData) {
            if (carDatum == null|| carDatum.length() == 0) {
                ShowMsg("错误", "缺少必要数据", "error");
                return;
            }
        }
        Car EditingCar = new Car();
        EditingCar.InitFromStrs(carData);
        try {
            if (EditingCar.isExist()) {
                if(isSure("提示","车辆 "+EditingCar.CarCard+" 已经存在，确定更新它的数据吗？")){
                    if (EditingCar.UpdatetoDB()> 0){
                        ShowMsg("提示", "更新操作成功！","info");
                    }else {
                        ShowMsg("错误", "操作失败！","error");
                    }
                }
            }else {
                if (EditingCar.SavetoDB()> 0){
                    ShowMsg("提示", "操作成功！","info");
                }else {
                    ShowMsg("错误", "操作失败！","error");
                }
            }
            CarTableRFromDB(ObjectATB);
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * 简化的弹出提示方法
     * @param title 标题
     * @param message 提示文本
     * @param types 图标类型
     * */
    public void ShowMsg(String title, String message,String... types){
        switch (types[0]) {
            case "error" ->
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            case "info" ->
                    JOptionPane.showMessageDialog(null,message, title, JOptionPane.INFORMATION_MESSAGE);
            case "warning" ->
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
            case "question" ->
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
            default ->
                    JOptionPane.showMessageDialog(null, message, title,JOptionPane.PLAIN_MESSAGE);
        }
    }
    /**
    * 操作询问框
     * @param CarTableTitle the message to display, title is first,message is second.
     * @return boolean
    * */
    public boolean isSure(@NotNull String... CarTableTitle) {
        String message = CarTableTitle[1];
        String title = CarTableTitle[0];
        boolean rs = false;
        int re = JOptionPane.showConfirmDialog(null,
                message, title,
                JOptionPane.YES_NO_OPTION);
        if (re == JOptionPane.YES_OPTION) {
            rs = true;
        }
        return rs;
    }
    /**
     * 密码验证
     * 完成日期：2023年5月16日
     * @param inputPassword password1
     * @param storedPassword password2
     * @return boolean
     * */
    private boolean isValidPassword(String inputPassword,String storedPassword) {
        return inputPassword.equals(storedPassword);
    }
    /**
    *严格判断当前登陆用户是否管理员
    * 完成日期：2023年5月20日
     * @param NowUID 当前登陆用户
     * @return booleean
    * */
    public  boolean isAdmin(String NowUID) throws IOException {
        String storedAdminUID;
        cfg cfg = new cfg();
        cfg.init("def");
        storedAdminUID = cfg.getCfg("AdminName");
        cfg.storeCfg("success");
        return Objects.equals(NowUID, storedAdminUID);
    }
    /**
     * 页面初始化设置
     */
    public void GuiInit() throws IOException {
        //  主题选择 FlatLightLaf() FlatDarkLaf()  FlatIntelliJLaf() FlatDarculaLaf()
        // 尝试加载主题
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        frame.setContentPane(new Form().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setSize(1024,768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
//        初始化程序入口
        SwingUtilities.invokeLater(() -> {
            try {
                new Form().GuiInit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}