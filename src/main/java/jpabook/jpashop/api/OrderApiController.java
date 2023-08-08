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
import java.util.Comparator;
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

    @GetMapping("/v4/orders")
    public List<OrderDTO> ordersV4() {
        return orderRepository.findOrderDTOs();
    }

    @GetMapping("/v5/orders")
    public List<OrderDTO> ordersV5() {
        return orderRepository.findOrderDTOsV2();
    }

    @GetMapping("/v6/orders")
    public List<OrderDTO> ordersV6() {
        List<OrderFlatDTO> orderFlatDTOs = orderRepository.findOrderFlatDTOs();

        return orderFlatDTOs.stream()
                .collect(
                        Collectors.groupingBy(
                                o -> new OrderDTO(o.getOrderId(), o.getMemberName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                                Collectors.mapping(o -> new OrderItemDTO(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())
                        )
                )
                .entrySet()
                .stream()
                .map(e -> new OrderDTO(e.getKey().getOrderId(),e.getKey().getMemberName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),e.getKey().getAddress(), e.getValue()))
                .sorted(Comparator.comparing(OrderDTO::getOrderId))
                .toList();
    }
}
