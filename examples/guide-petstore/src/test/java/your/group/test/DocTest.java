package your.group.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import your.group.PetStoreApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PetStoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DocTest {

    @Test
    public void empty() {
    }

}