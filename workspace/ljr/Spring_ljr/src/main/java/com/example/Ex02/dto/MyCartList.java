package com.example.Ex02.dto;

import java.util.HashMap;
import java.util.Map;

public class MyCartList {
    private Map<Integer, Integer> orderlists = null;

    public MyCartList(){
        orderlists = new HashMap<Integer, Integer>(); // key, value
        // 3번 상품, 2개 주문
        // 1번 상품, 5개 주문

    }

    // 장바구니 있는 상품을 더 추가할 경우
    public void addOrder(int pnum, int oqty){
        // orderlists : Map<Integer, Integer>
        // key = 상품번호(pnum), value = 주문수량(qty)

        if(orderlists.containsKey(pnum)){ // ✅ 이미 장바구니에 같은 상품이 있는 경우
            Integer qty = orderlists.put(pnum, oqty);
            int addQty = qty + oqty;
            orderlists.put(pnum,addQty); // 기존 수량 + 새 수량으로 덮어쓰기
        }else{// ✅새상품이면
            orderlists.put(pnum,oqty);  // 그대로 추가
        }
    }


   public Map<Integer,Integer> getAllOrderlists(){

        return orderlists;
    }


}
