package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.service.OrderService;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @GetMapping("/v2/orders")
    public List<OrderDTO> ordersV2() {
        return orderService.search(new OrderSearch()).stream()
                .map(OrderDTO::new)
                .toList();
    }

    @GetMapping("/v3/orders")
    public List<OrderDTO> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(OrderDTO::new)
                .toList();
    }

    @GetMapping("/v3.1/orders")
    public List<OrderDTO> ordersV3_paging(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        return orders.stream()
                .map(OrderDTO::new)
                .toList();
    }

    @Data
    @NoArgsConstructor
    public static class OrderDTO {
        private Long orderId;
        private String memberName;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDTO> orderItems = new ArrayList<>();

        public OrderDTO(Order order) {
            this.orderId = order.getId();
            this.memberName = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItems = order.getOrderItems().stream()
                    .map(OrderItemDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    @AllArgsConstructor
    public static class OrderItemDTO {
        private String itemName;
        private Integer orderPrice;
        private Integer count;

        public OrderItemDTO(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
