import com.xcl.mapper.UserMapper;
import com.xcl.session.SqlSessionFactory;
import com.xcl.pojo.User;
import com.xcl.session.SqlSession;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.util.List;

/**
 * 测试用户接口
 */
public class UserControllerTest {

    private UserMapper userMapper;

    @Before
    public void before(){
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactory();
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        //sqlSession.selectList();
        //JDK动态代理，为目标类生成一个代理对象
        userMapper = sqlSession.getMapper(UserMapper.class);
    }

    @Test
    public void insertTest(){
        User user = new User();
        user.setId(3);
        user.setUsername("xcl33");
        user.setPassword("666666");
        user.setBirthday("2020-02-20");
        int result = userMapper.insert(user);
        System.out.println("result="+result);
    }

    @Test
    public void selectListTest(){
        List<User> list = userMapper.selectList();
        for (User temp : list) {
            System.out.println(temp);
        }
    }

    @Test
    public void deleteById(){
        User user = new User();
        user.setId(5);
        int result = userMapper.deleteById(user);
        System.out.println("result="+result);
    }

    @Test
    public void updateByIdTest(){
        User user = new User();
        user.setId(2);
        user.setUsername("xcl");
        int result = userMapper.updateById(user);
        System.out.println("result="+result);
    }





}
