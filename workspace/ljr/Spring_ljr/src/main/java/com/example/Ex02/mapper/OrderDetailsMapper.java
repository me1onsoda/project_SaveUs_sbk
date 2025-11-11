package com.example.Ex02.mapper;

import com.example.Ex02.dto.OrderDetailDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailsMapper {

   void insertOrderDetails(OrderDetailDto odDto);

    List<OrderDetailDto> orderView(int oid);
}
