import com.xcl.dao.UserDao;
import com.xcl.factory.SqlSessionFactory;
import com.xcl.pojo.User;
import com.xcl.service.SqlSession;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.util.List;

/**
 * 测试用户接口
 */
public class UserControllerTest {

    @Test
    public void selectListTest() throws PropertyVetoException, DocumentException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //JDK动态代理，为目标类生成一个代理对象
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        List<User> list = userDao.selectList();
        for (User temp : list) {
            System.out.println(temp);
        }
    }
}
