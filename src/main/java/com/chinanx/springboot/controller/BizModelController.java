package com.chinanx.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinanx.springboot.model.BizModel;
import com.chinanx.springboot.repository.BizModelRepository;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Controller
public class BizModelController {
    private static Logger logger = LoggerFactory.getLogger(BizModelController.class);
    
    @Autowired
    private BizModelRepository repository;

    @GetMapping("/bizmodel")
    public String home() {
        return "bizmodel";
    }

    @ResponseBody
    @RequestMapping("/bizmodel/page/{p:\\d+}")
    public Object getBizModelByPage(@PathVariable int p, @RequestParam(required=false, defaultValue="") String filter) {
        Page<BizModel> page = PageHelper.startPage(p, 10);
        repository.getBizModel(filter);
        return page.toPageInfo();
    }
    
    @ResponseBody
    @GetMapping("/bizmodel/{id:\\d+}")
    public Object getBizModelById(@PathVariable long id) {
        return repository.getBizModelById(id);
    }
    
    @ResponseBody
    @PutMapping("/bizmodel/{id:\\d+}")
    public Object saveBizModelById(@PathVariable long id, @RequestParam String json) {
        BizModel model = repository.getBizModelById(id);
        logger.debug("update mode, id: {}, bytes: {}", id, json);
        
        try {
            model.setJson(json);
            repository.save(model);
        } catch (Exception e) {
            logger.error("save model error", e);
            throw new RuntimeException("save model error", e);
        }
        
        return model;
    }
}
