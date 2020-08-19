package com.leyou.order.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.interceptor.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.mapper.SeckillOrderMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.pojo.SeckillOrder;
import com.leyou.pojo.Sku;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: OrderService <br/>
 * Description: TODO
 * Date 2020/5/10 13:17
 *
 * @author Lenovo
 **/
@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private SeckillOrderMapper orderSecKillMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private GoodsClient goodsClient;

    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        //编号生成
        long orderId = idWorker.nextId();
        //订单数据
        Order order = new Order();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());

        //用户信息
        UserInfo user = UserInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        //收货人信息
        AddressDTO addressDTO = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addressDTO.getName());
        order.setReceiverAddress(addressDTO.getAddress());
        order.setReceiverCity(addressDTO.getCity());
        order.setReceiverDistrict(addressDTO.getDistrict());
        order.setReceiverMobile(addressDTO.getPhone());
        order.setReceiverState(addressDTO.getState());
        order.setReceiverZip(addressDTO.getZipCode());
        Map<Long, Integer> cartMap = orderDTO.getCarts().stream()
                .collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        //获取所有sku的id
        Set<Long> ids = cartMap.keySet();
        List<Sku> skus = goodsClient.querySkyBySpuIds(new ArrayList<>(ids));

        //orderDetail
        List<OrderDetail> details = new ArrayList<>();

        long totalPay = 0L;
        for (Sku sku : skus) {
            totalPay += sku.getPrice() * cartMap.get(sku.getId());
            //封装orderDetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            orderDetail.setNum(cartMap.get(sku.getId()));
            orderDetail.setOrderId(orderId);
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            details.add(orderDetail);
        }
        order.setTotalPay(totalPay);
        //实付金额： 总金额 + 邮费 - 优惠
        order.setActualPay(totalPay + order.getPostFee() - 0);
        int count = orderMapper.insertSelective(order);
        if (count != 1) {
            log.error("[创建订单] 创建订单失败，orderId:{}", orderId);
            throw new MyException(ExceptionEnums.CART_NOT_FOUND);
        }
        //订单详情
        count = orderDetailMapper.insertList(details);
        if (count != details.size()) {
            log.error("[创建订单] 创建订单详细失败，orderId:{}", orderId);
            throw new MyException(ExceptionEnums.CART_NOT_FOUND);
        }

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(new Date());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());
        count = orderStatusMapper.insertSelective(orderStatus);
        if (count != 1) {
            log.error("[创建订单] 创建订单状态失败，orderId:{}", orderId);
            throw new MyException(ExceptionEnums.CART_NOT_FOUND);
        }
        //购物车去除

        //库存修改
        List<CartDTO> carts = orderDTO.getCarts();
        goodsClient.decreaseStock(carts);   //分布式事务
        return orderId;
    }

    public Order queryOrderById(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw new MyException(ExceptionEnums.ORDER_NOT_FOUND);
        }
        //查询订单详细
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        List<OrderDetail> select = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(select)) {
            throw new MyException(ExceptionEnums.ORDER_DETAIL_NOT_FOUND);
        }
        order.setOrderDetails(select);
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
        if (orderStatus == null) {
            throw new MyException(ExceptionEnums.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    public void createSecKillOrderById(Long skuId, Long userId) {
        SeckillOrder orderSecKill = new SeckillOrder();
        orderSecKill.setSkuId(skuId);
        orderSecKill.setUserId(userId);
        orderSecKillMapper.insertSelective(orderSecKill);   //插入数据
    }
}
