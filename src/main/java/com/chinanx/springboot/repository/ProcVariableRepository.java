package com.chinanx.springboot.repository;

import org.springframework.stereotype.Repository;

import com.chinanx.springboot.model.ProcVariable;

@Repository
public interface ProcVariableRepository {
    int save(ProcVariable var);
}
