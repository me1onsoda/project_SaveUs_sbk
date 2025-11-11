package com.example.Ex02.mapper;

import com.example.Ex02.dto.ProductsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductsMapper {

    List<ProductsDto> selectAll(Map<String, Object> params);
    int getCount(Map<String, Object> params);

    void insertProduct(ProductsDto pDto);

    ProductsDto findByNum(int num);

    void updateProduct(ProductsDto pDto);

    void deleteProduct(int num);

    void updateStock(ProductsDto pDto);
}
