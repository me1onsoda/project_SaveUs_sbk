package com.example.Ex02.mapper;

import com.example.Ex02.dto.MemberDto;
import com.example.Ex02.dto.OrderDto;
import com.example.Ex02.dto.ShoppingInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    void insertOrder(OrderDto orderDto);

    int getMaxOid();

    List<OrderDto> orderMall(MemberDto loginInfo);

    List<ShoppingInfo> showDetail(int oid);
}
