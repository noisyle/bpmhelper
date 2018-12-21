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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinanx.springboot.model.ProcModel;
import com.chinanx.springboot.model.ProcVariable;
import com.chinanx.springboot.repository.ProcModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Controller
public class ProcModelController {
    private static Logger logger = LoggerFactory.getLogger(ProcModelController.class);
    
    @Autowired
    private ProcModelRepository repository;

    @GetMapping("/procmodel")
    public String home() {
        return "procmodel";
    }

    @ResponseBody
    @RequestMapping("/procmodel/page/{p:\\d+}")
    public Object getProcModelByPage(@PathVariable int p, @RequestParam(required=false, defaultValue="") String filter) {
        Page<ProcModel> page = PageHelper.startPage(p, 10);
        repository.getProcModel(filter);
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
    public Object saveProcModelById(@PathVariable long id, @RequestParam String bytes, @RequestParam String variables) {
        try {
            List<ProcVariable> varList = (List<ProcVariable>) new ObjectMapper().readValue(variables, List.class);
            logger.debug("update mode, id: {}, bytes: {}, variables: {}", id, bytes, varList);
        } catch (Exception e) {
            logger.error("save model error", e);
            throw new RuntimeException("save model failed", e);
        }
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
            logger.debug("backup model to {}", file.getPath());
        } catch (Exception e) {
            logger.error("backup model error", e);
            throw new RuntimeException("backup model failed", e);
        }
        
        try {
            model.setBytesString(bytes);
//            repository.save(model);
        } catch (Exception e) {
            logger.error("save model error", e);
            throw new RuntimeException("save model failed", e);
        }
        
        return model;
    }
    
    @GetMapping("/procmodel/{deploymentId:\\d+}_{name:.+\\.png}")
    public void getProcModelImageById(@PathVariable String deploymentId, @PathVariable String name, HttpServletResponse res) throws Exception {
        InputStream is = ((Blob) repository.getProcModelImage(name, deploymentId).get("BYTES_")).getBinaryStream();
        IOUtils.copy(is, res.getOutputStream());
    }
}
