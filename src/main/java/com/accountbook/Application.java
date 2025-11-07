package com.accountbook;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import com.accountbook.backend.storage.db.DBInitializer;
import com.accountbook.frontend.MainPage;


/*应用入口类：初始化个各模块，启动系统 */
public class Application {
    public static void main(String[] args)
    {
        /*1.初始化数据库模块 */
        DBInitializer.init();

        /*2.启动主页面 */
                SwingUtilities.invokeLater(() -> {
            try {
                MainPage mainPage = new MainPage();
                mainPage.setVisible(true);
            } catch (Exception e) {
                System.err.println("程序启动失败：" + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "启动失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
