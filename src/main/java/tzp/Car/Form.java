package tzp.Car;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

public class Form {
    JFrame frame = new JFrame("小橘子の汽车租赁系统");
    public JPanel Main;
    public JTable table1;
    public JComboBox comboBox1;
    public JButton 删除Button;
    public JTextField textField1;
    public JTextField textField2;
    public JTextField textField3;
    public JTextField textField4;
    public JButton 添加Button;
    public JButton 刷新Button;
    public JPanel AdminFrame;
    public JTextField textField5;
    public JTable table2;
    public JButton 刷新Button1;
    public JButton 拒绝Button;
    public JButton 通过Button;
    public JComboBox comboBox2;
    public JButton OpenRegisterButton;
    public JTextField LoginUserNameEdit;
    public JPanel StartFrame;
    public JButton LoginButton;
    public JPasswordField LoginUserPasswordEdit;
    public JPanel RegisterFrame;
    public JPanel LoginFrame;
    public JTextField textField6;
    public JPasswordField passwordField1;
    public JTextField textField7;
    public JButton RegisterButton;
    public JButton CancelRegButton;
    public JTable table3;
    public JPanel UserFrame;
    public JButton 刷新Button2;
    public JButton button1;
    public JComboBox comboBox3;
    public JButton 修改信息Button;
    public JButton 注销登录Button;
    public JButton 修改信息Button1;
    public JButton 注销登录Button1;
    public JTextField textField9;
    public JPanel AccountFrame;
    public JPasswordField passwordField2;
    public JPasswordField passwordField3;
    public JButton 确定Button;
    public JButton 取消Button;
    public JPasswordField passwordField4;

    public Form(){

        /*
        * 从登陆页面打开注册页面
        */
        OpenRegisterButton.addActionListener(e -> {
            RegisterFrame.setVisible(true);
            LoginFrame.setVisible(false);
        });
        /*
        *  取消注册
        */
        CancelRegButton.addActionListener(e -> {
            RegisterFrame.setVisible(false);
            LoginFrame.setVisible(true);
        });

        /*
        * 当登陆页面的登陆按钮按下时执行
        */
        LoginButton.addActionListener(e -> {
            cfg login = new cfg();
            try {
                login.init("def");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                String username = login.getCfg("AdminName");
                String GUName = LoginUserNameEdit.getText();

                if (Objects.equals(GUName, username) && isValidPassword(LoginUserPasswordEdit.getPassword(),login.getCfg("AdminPassword").toCharArray())) {
                    LoginFrame.setVisible(false);
                    StartFrame.setVisible(false);
                    AdminFrame.setVisible(true);
                }else {

                    JOptionPane.showMessageDialog(null,"账号或密码错误！", "提示", JOptionPane.ERROR_MESSAGE);
                    LoginUserPasswordEdit.setText("");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    private boolean isValidPassword(char[] inputPassword,char[] storedPassword) {
        /*密码验证*/
        String sPasswordStr = new String(storedPassword);
        String iPasswordStr = new String(inputPassword);
        return iPasswordStr.equals(sPasswordStr);
    }
    public void GuiInit() throws IOException {
        /*
        * 页面初始化设置
        */
        //  主题选择atLightLaf() FlatDarkLaf()  FlatIntelliJLaf() FlatDarculaLaf()
        // 尝试加载主题
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
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
        SwingUtilities.invokeLater(() -> {
            try {
                new Form().GuiInit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
