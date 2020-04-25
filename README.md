### 第一阶段模块一作业

1、MyBatis动态SQL是做什么的？都有哪些动态SQL？简述一下动态SQL的执行原理？

​	可根据具体需要动态拼接SQL语句。

​	动态SQL包括以下几种元素：

|            元素             |        功能        |
| :-------------------------: | :----------------: |
|            <if>             | 单条件分支判断语句 |
| <choose> <when> <otherwise> | 多条件分支判断语句 |
|    <trim> <where> <set>     |        过滤        |
|          <foreach>          | 循环语句，遍历集合 |

​	原理：根据表达式的值动态拼接SQL。



2、MyBatis是否支持延迟加载？如果支持，它的实现原理是什么？

​	延迟加载即使用时再加载，MyBatis支持延迟加载，可将lazyLoadingEnabled属性设置为true开启延迟加载。

​	原理：使用CGLIB创建目标对象的代理对象。当调用目标对象的方法时，会进入代理对象的的invoke()方法，invoke()方法中此时进行关联查询。

​	场景：查询用户及其所有的订单（1对多）

​	方案1：不使用延迟加载，对应的结果集封装以及sql语句

```xml
 <resultMap id="userList" type="com.xcl.pojo.User">
        <result property="id" column="id"></result>
        <result property="username" column="username"></result>
        <collection property="orders" ofType="com.xcl.pojo.Order">
            <result property="id" column="oid"></result>
            <result property="orderTime" column="orderTime"></result>
        </collection>
  </resultMap>
  
  <select id="selectList" resultMap="userList">
        SELECT u.*,o.id oid,o.ordertime FROM `user` u LEFT JOIN orders o ON u.id = o.uid;
    </select>
```

​	方案2：使用延迟加载，对应的结果集封装以及sql语句

```xml
<resultMap id="userList" type="com.xcl.pojo.User">
        <result property="id" column="id"></result>
        <result property="username" column="username"></result>
        <collection property="orders" column="id" ofType="com.xcl.pojo.User" select="com.xcl.pojo.Order.selectOrdersByUid"></collection>
  </resultMap>
  <!--userMapper.xml-->
  <select id="selectList" resultType="com.xcl.pojo.User">
       select * from user;
  </select>
 <!--OrderMapper.xml-->
  <select id="selectOrdersByUid" resultType="com.xcl.pojo.Order">
       select * from Order where uid = #{uid};
  </select>
```



3、MyBatis都有哪些Executor执行器？它们之间的区别是什么？

​	MyBatis提供了三种执行器BatchExecutor、ReuseExecutor、SimpleExecutor，在运行过程中根据配置创建相应的执行器。三种执行器区别如下：

|     执行器     |               功能               |
| :------------: | :------------------------------: |
| BatchExecutor  | 重用语句和批量更新，针对批量专用 |
| ReuseExecutor  |          重用预处理语句          |
| SimpleExecutor |       简易执行器，默认配置       |



4、简述一下MyBatis的一级、二级缓存。

​	缓存是存储在内存中的数据，其目的在于提高查询速度且有效降低数据库的压力。MyBatis为我们提供了两种缓存一级缓存和二级缓存。

|   缓存   | 存储结构 |       范围        |                          失效场景                           |
| :------: | :------: | :---------------: | :---------------------------------------------------------: |
| 一级缓存 | map<K,V> |    SqlSession     | 进行增删改并提交事务或调用sqlSession.clearCache()会清空缓存 |
| 二级缓存 | map<K,V> | SqlSessionFactory |                    进行增删改并提交事务                     |



5、简述MyBatis的插件运行原理，以及如何实现一个插件？

​	 	插件的初始化是在加载核心配置文件的时候完成的，其被保存在一个list集合中。之后使用责任链模式为四大对象生成代理对象并返回，当代理对象调用方法时就会进入invoke()方法中，在invoke方法中，如果存在签名的拦截方法，这时就会调用插件的intercept()。否，就直接使用反射调用要执行的方法。

​		自定义插件：

​		实现Interceptor接口

​		重写重要的3个方法

​				intercept(Invocation invocation)：覆盖目标对象原有的方法。invocation对象，通过它可以利用反射调				用目标对象的方法。

​				plugin(Object tartget)：target是被拦截的对象，plugin的作用是为target生成一个代理对象并返回。

​				setProperties(Properties properties)：允许配置插件所需参数。

​		@Intercepts注解声明拦截器，@Signature注册拦截器签名

```java
@Intercepts({
        @Signature(type= StatementHandler.class,
                  method = "prepare",
                  args = {Connection.class,Integer.class})
})
public class MyPlugin implements Interceptor {

    /*
        拦截方法：只要被拦截的目标对象的目标方法被执行时，每次都会执行intercept方法
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("对方法进行了增强....");
        return invocation.proceed(); //原方法执行
    }

    /*
       主要为了把当前的拦截器生成代理存到拦截器链中
     */
    @Override
    public Object plugin(Object target) {
        Object wrap = Plugin.wrap(target, this);
        return wrap;
    }

    /*
        获取配置文件的参数
     */
    @Override
    public void setProperties(Properties properties) {
        System.out.println("获取到的配置文件的参数是："+properties);
    }
}
```


