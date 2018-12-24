package com.chinanx.springboot.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.chinanx.springboot.model.ProcModel;

@Repository
public interface ProcModelRepository {
    List<Map<String, Object>> getCategories();
    
    List<ProcModel> getProcModel(@Param("name") String name, @Param("category") String category);
    
    ProcModel getProcModelById(long id);
    
    Map<String, Object> getProcModelImage(@Param("name") String name, @Param("deploymentId") String deploymentId);
    
    int updateSource(ProcModel model);
}
