package com.chinanx.springboot.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinanx.springboot.model.ProcModel;
import com.chinanx.springboot.model.ProcVariable;
import com.chinanx.springboot.repository.ProcModelRepository;
import com.chinanx.springboot.repository.ProcVariableRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Controller
public class ProcModelController {
    private static Logger logger = LoggerFactory.getLogger(ProcModelController.class);
    
    @Autowired
    private ProcModelRepository repository;
    @Autowired
    private ProcVariableRepository varRepository;

    @GetMapping("/procmodel")
    public String home() {
        return "procmodel";
    }

    @ResponseBody
    @GetMapping("/categories")
    public Object getCategories() {
        return repository.getCategories();
    }
    
    @ResponseBody
    @GetMapping("/procmodel/page/{p:\\d+}")
    public Object getProcModelByPage(@PathVariable int p, @RequestParam(required=false, defaultValue="") String name, @RequestParam(required=false, defaultValue="") String category) {
        Page<ProcModel> page = PageHelper.startPage(p, 10);
        repository.getProcModel(name, category);
        return page.toPageInfo();
    }
    
    @ResponseBody
    @GetMapping("/procmodel/{id:\\d+}")
    public Object getProcModelById(@PathVariable long id) {
        return repository.getProcModelById(id);
    }
    
    @SuppressWarnings("unchecked")
    @ResponseBody
    @PutMapping("/procmodel/{id:\\d+}")
    @Transactional
    public Object saveProcModel(@PathVariable long id, @RequestParam String bytes, @RequestParam String variables) {
        ProcModel model = repository.getProcModelById(id);
        File folder = new File(System.getProperty("user.home"), ".bpmhelper");
        if(!folder.exists()) {
            folder.mkdirs();
        }
        try {
            File file = new File(folder, String.valueOf(id) + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            FileWriter writer = new FileWriter(file);
            writer.write(model.getBytesString());
            writer.flush();
            writer.close();
            logger.debug("Backup model to {}", file.getPath());
        } catch (Exception e) {
            logger.error("Backup model error", e);
            throw new RuntimeException("backup model failed", e);
        }

        try {
            List<ProcVariable> varList = (List<ProcVariable>) new ObjectMapper().readValue(variables, new TypeReference<List<ProcVariable>>(){});
            logger.debug("Update mode, id: {}, bytes: {}", id, bytes);
            logger.debug("variables: {}", varList);
            for(ProcVariable var : varList) {
                if(!StringUtils.isEmpty(var.getName()) && !StringUtils.isEmpty(var.getValue())) {
                    varRepository.save(var);
                }
            }
        } catch (Exception e) {
            logger.error("Save model error", e);
            throw new RuntimeException("Save model failed", e);
        }
        
        try {
            model.setBytesString(bytes);
            repository.updateSource(model);
        } catch (Exception e) {
            logger.error("Save model error", e);
            throw new RuntimeException("Save model failed", e);
        }
        
        return model;
    }
    
    @GetMapping("/procmodel/{deploymentId:\\d+}_{name:.+\\.png}")
    public void getProcModelImageById(@PathVariable String deploymentId, @PathVariable String name, HttpServletResponse res) throws Exception {
        InputStream is = ((Blob) repository.getProcModelImage(name, deploymentId).get("BYTES_")).getBinaryStream();
        IOUtils.copy(is, res.getOutputStream());
    }
}
