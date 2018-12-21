package com.chinanx.springboot.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.chinanx.springboot.model.BizModel;

@Repository
public interface BizModelRepository {
    List<BizModel> getBizModel(@Param("filter") String filter);
    
    BizModel getBizModelById(long id);
    
    int save(BizModel model);
}
