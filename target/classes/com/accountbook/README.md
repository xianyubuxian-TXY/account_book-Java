# 新增实体即对应功能的步骤
1.backend后端部分：
- 在factory文件夹下的factory类中注册对应的服务，并在service下创建服务的具体实现
- 在storage文件下，entity下创建相应实体（如果新增实体），dao下实现相应的DAO，并注册到DAOFactory中（复用）
  - DAO的实现：在db下已经封装了mysql语句，可以调用相应的API

2.proxy代理部分：
- 在proxy文件夹下的request、response实现该功能传递的参数格式与返回值格式，并在request的枚举类型中添加
  对应服务枚举值
- 在ServiceProxy的getBusinessService方法中注册对应功能
  - 前端的使用：先创建request，并生成FrontendRequest,然后通过getBusinessService获取相应服务并执行，并得到返回值
- 为了方便前端使用，可以在helper中创建相应的的“帮助类”，对上述过程再封装,并将获取Helper帮助类的接口封装到ProxyHandler中

