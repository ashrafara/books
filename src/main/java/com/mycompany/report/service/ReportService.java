package com.mycompany.report.service;


import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.repository.AuthorRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private AuthorRepository repository;


    public String exportReport(String reportFormat) throws FileNotFoundException, JRException {
        List<Author> author=repository.findAll();
        //load file and compile
        File file= ResourceUtils.getFile("classpath:author.jrxml");
        JasperDesign jasperDesign;
        JasperReport jasperReport=JasperCompileManager.compileReport(file.getAbsolutePath());

    }
}
