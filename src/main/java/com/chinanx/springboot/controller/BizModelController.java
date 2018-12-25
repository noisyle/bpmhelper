package com.chinanx.springboot.controller;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinanx.springboot.model.BizModel;
import com.chinanx.springboot.model.ProcModel;
import com.chinanx.springboot.model.ProcVariable;
import com.chinanx.springboot.repository.BizModelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @GetMapping("/bizmodel/page/{p:\\d+}")
    public Object getBizModelByPage(@PathVariable int p, @RequestParam(required=false, defaultValue="") String name, @RequestParam(required=false, defaultValue="") String category) {
        Page<BizModel> page = PageHelper.startPage(p, 10);
        repository.getBizModel(name, category);
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
        File folder = new File(System.getProperty("user.home"), ".bpmhelper/bizmodel");
        if(!folder.exists()) {
            folder.mkdirs();
        }
        try {
            File file = new File(folder, String.valueOf(id) + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            FileWriter writer = new FileWriter(file);
            writer.write(model.getJson());
            writer.flush();
            writer.close();
            logger.debug("Backup model to {}", file.getPath());
        } catch (Exception e) {
            logger.error("Backup model error", e);
            throw new RuntimeException("backup model failed", e);
        }

        try {
            logger.debug("Update mode, id: {}, json: {}", id, json);
            model.setJson(json);
            repository.save(model);
        } catch (Exception e) {
            logger.error("Save model error", e);
            throw new RuntimeException("Save model error", e);
        }
        
        return model;
    }
}
