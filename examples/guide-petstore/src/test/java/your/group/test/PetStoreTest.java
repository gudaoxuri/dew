package your.group.test;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import your.group.PetStoreApplication;
import your.group.entity.Customer;
import your.group.entity.Order;
import your.group.entity.Pet;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PetStoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ComponentScan(basePackageClasses = {Dew.class, PetStoreTest.class})
public class PetStoreTest {

    private static final String url = "http://127.0.0.1:8080/";

    @Test
    public void testAll() throws Exception {
        // 添加2个宠物
        $.http.post(url+"pet/", "{\"type\":\"dog\",\"price\":1000,\"enabled\":true}");
        $.http.post(url+"pet/", "{\"type\":\"dog\",\"price\":1000,\"enabled\":true}");
        // 添加一个客户
        Customer customer = Resp.generic($.http.post(url+"customer/", "{\"name\":\"张三\"}"), Customer.class).getBody();
        // 查看可购买的宠物列表，有2个
        List<Pet> pets = Resp.genericList($.http.get(url+"pet/?enabled=true"), Pet.class).getBody();
        Assert.assertEquals(2, pets.size());
        // 购买一个宠物
        $.http.post(url+"order/buy", "{\"petId\":\"" + pets.get(0).getId() + "\",\"customerId\":\"" + customer.getId() + "\"}");
        // 查看订单列表
        List<Order> orders = Resp.genericPage($.http.get(url+"order/dog/1/10?customerId=" + customer.getId()), Order.class).getBody().getObjects();
        Assert.assertEquals(1,orders.size());
        Assert.assertEquals(pets.get(0).getId(), orders.get(0).getPetId());
        // 查看可购买的宠物列表，只有1个
        pets = Resp.genericList($.http.get(url+"pet/?enabled=true"), Pet.class).getBody();
        Assert.assertEquals(1, pets.size());
    }

}
