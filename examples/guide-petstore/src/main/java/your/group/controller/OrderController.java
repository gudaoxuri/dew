package your.group.controller;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.controller.CRUController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import your.group.entity.Order;
import your.group.service.OrderService;
import your.group.vo.BuyVO;

@RestController
@RequestMapping("order/")
@Api(description = "订单操作")
public class OrderController implements CRUController<OrderService, Integer, Order> {

    @PostMapping("buy")
    @ApiOperation(value = "获取记录分页列表")
    // 自定义返回状态，可复写预定义状态
    @ApiResponses({@ApiResponse(code = 401,message = "示例 message",reference = "示例 reference")})
    public Resp<Void> buy(@Validated @RequestBody BuyVO buyVO) {
        return getService().buy(buyVO.getPetId(), buyVO.getCustomerId());
    }

    @GetMapping("{type}/{pageNumber}/{pageSize}")
    @ApiOperation(value = "获取记录分页列表")
    public Resp<Page<Order>> findOrders(@PathVariable String type, @RequestParam int customerId, @PathVariable long pageNumber, @PathVariable int pageSize) {
        return getService().findOrders(customerId, type, pageNumber, pageSize);
    }

}
