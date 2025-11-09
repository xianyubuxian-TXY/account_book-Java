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

/*  前端请求的两种方式
    一：自己手动构造
        //(1)增加账单
        1.构造参数
        AddBillParams params=new AddBillParams("2025-10-19",-1,1,1,BigDecimal.valueOf(300.0),"买了件羽绒服朋友");
        2.填写请求
        FrontendRequest<AddBillParams> addBillRequest=new FrontendRequest<AddBillParams>(RequestType.ADD_BILL,params);
        3.发送给中间代理，并接收响应
        BackendResponse<BillSingleResponse> backendResponse=serviceProxy.handleRequest(addBillRequest);
        4.解析响应
        backendResponse.getData().printSelf();
        if(backendResponse.isSuccess())
        {
            backendResponse.getData().printSelf();
        }
        else
        {
            System.err.println(backendResponse.getMessage());
        }
    二：通过proxy模块的helper，调用封装好的接口
        
*/

