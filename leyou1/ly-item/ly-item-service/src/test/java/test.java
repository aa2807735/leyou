import com.leyou.api.LyItemApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ClassName: test <br/>
 * Description: TODO
 * Date 2020/4/30 16:14
 *
 * @author Lenovo
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LyItemApplication.class})
public class test {

    @Test
    public void testApplication(){
        //System.out.println(1/2);
        double a = 1*1.0/2;
    }
}
