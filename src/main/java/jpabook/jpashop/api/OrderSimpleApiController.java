package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * XXXToOne
 *
 * Order -> Member N:1
 * Order -> Delivery 1:1
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderService orderService;

//    @GetMapping("/v1/simple-orders")
//    public List<Order> findOrdersV1() {
//        List<Order> orders = orderService.search(new OrderSearch());
//        return orders;
//    }

    @GetMapping("/v2/simple-orders")
    public List<SimpleOrderDTO> findOrdersV2(OrderSearch orderSearch) {
        return orderService.search(orderSearch).stream()
                .map(SimpleOrderDTO::new)
                .toList();
    }

    @GetMapping("/v3/simple-orders")
    public List<SimpleOrderDTO> findOrdersV3() {
        return orderRepository.findAllWithMemberDelivery(0, 100).stream()
                .map(SimpleOrderDTO::new)
                .toList();
    }

    @GetMapping("/v4/simple-orders")
    public List<SimpleOrderDTO> findOrdersV4() {
        return orderRepository.findSimpleOrderDTOs();
    }
}
