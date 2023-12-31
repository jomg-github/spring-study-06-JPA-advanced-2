package jpabook.jpashop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.order;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findById(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByJPQL(OrderSearch orderSearch) {
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 주문상태
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원명
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000);

        // 주문상태
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        // 회원명
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문상태
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원명
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);

        return query.getResultList();
    }

    public List<Order> findAllByQdsl(OrderSearch orderSearch) {
        JPAQueryFactory query = new JPAQueryFactory(em);

        return query.select(order)
                .from(order)
                .join(order.member, member)
                .limit(1000)
                .where(statusEq(orderSearch.getOrderStatus()), memberNameLike(orderSearch.getMemberName()))
                .fetch();
    }

    private BooleanExpression memberNameLike(String memberName) {
        if (!StringUtils.hasText(memberName)) {
            return null;
        }
        return member.name.likeIgnoreCase("%" + memberName + "%");
    }

    private BooleanExpression statusEq(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }
        return order.status.eq(orderStatus);
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member" +
                        " join fetch o.delivery",
                        Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<SimpleOrderDTO> findSimpleOrderDTOs() {
        return em.createQuery(
                "select new jpabook.jpashop.domain.SimpleOrderDTO(o.id, m.name, o.orderDate, o.status, d.address) " +
                        "from Order o" +
                        " join o.member m" +
                        " join o.delivery d",
                        SimpleOrderDTO.class
                )
                .getResultList();
    }

    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i",
                        Order.class
                )
                .getResultList();
    }

    public List<OrderDTO> findOrderDTOs() {
        List<OrderDTO> orderDTOs = findOrders();

        orderDTOs.forEach(orderDTO ->
                orderDTO.setOrderItems(findOrderItems(orderDTO.getOrderId()))
        );

        return orderDTOs;
    }

    private List<OrderDTO> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.domain.OrderDTO(o.id, m.name, o.orderDate, o.status, d.address) " +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d",
                        OrderDTO.class
                )
                .getResultList();
    }

    private List<OrderItemDTO> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.domain.OrderItemDTO(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId",
                        OrderItemDTO.class
                )
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderDTO> findOrderDTOsV2() {
        List<OrderDTO> orders = findOrders();

        Map<Long, List<OrderItemDTO>> orderItemMap = createOrderItemMap(orders);

        orders.forEach(order -> order.setOrderItems(orderItemMap.get(order.getOrderId())));

        return orders;
    }

    private Map<Long, List<OrderItemDTO>> createOrderItemMap(List<OrderDTO> orders) {
        List<OrderItemDTO> orderItems = em.createQuery(
                        "select new jpabook.jpashop.domain.OrderItemDTO(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds",
                        OrderItemDTO.class
                )
                .setParameter("orderIds", orders.stream().map(OrderDTO::getOrderId).toList())
                .getResultList();

        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemDTO::getOrderId));
    }

    public List<OrderFlatDTO> findOrderFlatDTOs() {
        return em.createQuery(
                "select new jpabook.jpashop.domain.OrderFlatDTO(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i",
                        OrderFlatDTO.class
                )
                .getResultList();
    }
}
