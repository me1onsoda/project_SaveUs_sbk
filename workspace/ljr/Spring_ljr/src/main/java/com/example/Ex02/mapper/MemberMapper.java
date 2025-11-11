package com.example.Ex02.mapper;

import com.example.Ex02.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberMapper {
    List<MemberDto> selectAllMember(Map<String , Object> map);
    int getCount(Map<String , Object> map);
    void insertMember(MemberDto MDto);

    MemberDto findById(String id);

    void updateMember(MemberDto mDto);
    void deleteMember(String id);

    int selectCountById(String id);


    void addPoint(MemberDto member);
}
