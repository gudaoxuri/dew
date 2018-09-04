package your.group.service;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.cluster.ClusterLock;
import com.tairanchina.csp.dew.core.service.CRUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import your.group.dao.OrderDao;
import your.group.entity.Order;

import javax.annotation.PostConstruct;

@Service
public class OrderService implements CRUService<OrderDao, Integer, Order> {

    // 使用分布式锁
    private ClusterLock lock;

    @Autowired
    private PetService petService;

    @PostConstruct
    public void init() {
        // 锁的初始化，写在@PostConstruct方法中
        lock = Dew.cluster.dist.lock("petstore:buy");
    }

    /**
     * 购买方法
     *
     * @return {@link Resp}
     */
    public Resp<Void> buy(int petId, int customerId) {
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setPetId(petId);
        try {
            // 加锁，推荐加上锁过期时间
            if (lock.tryLock(100, 5000)) {
                if (petService.getById(petId).getBody().isEnabled()) {
                    // 只能未被购买的宠物才能购买
                    getDao().insert(order);
                    // 标记宠物已被购买
                    petService.disableById(petId);
                }
            } else {
                return Resp.locked("请求忙，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Resp.serverError("未知错误");
        } finally {
            // 解锁，不要忘了
            lock.unLock();
        }
        return Resp.success(null);
    }

    public Resp<Page<Order>> findOrders(int customerId, String petType, long pageNumber, int pageSize) {
        return Resp.success(getDao().findOrders(customerId, petType, pageNumber, pageSize));
    }

}
