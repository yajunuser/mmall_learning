import com.mymmall.controller.portal.UserController;
import com.mymmall.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserTest {
    @Autowired
    private UserController userController;
    @Test
    public void demo() {
        User user = new User();
        user.setUsername("郑亚军");
        user.setPassword("456789");
        user.setQuestion("我是谁");
        user.setAnswer("你是李荣基");
        userController.register(user);
    }
}
