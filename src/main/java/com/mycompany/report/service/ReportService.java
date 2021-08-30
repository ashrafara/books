package com.mycompany.report.service;


import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.repository.AuthorRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
//import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.util.Map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private AuthorRepository repository;


    public String exportReport(String reportFormat) throws FileNotFoundException, JRException {
        String path= "C:\\Users\\PC-A\\Documents";
        List<Author> author=repository.findAll();
        //load file and compile
        File file= ResourceUtils.getFile("classpath:author.jrxml");
        JasperDesign jasperDesign;
        JasperReport jasperReport=JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource=new JRBeanCollectionDataSource(author);
        Map<String,Object> parameters= new HashMap<>();
        parameters.put("createdBy","Ashref Elarayed");
        JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        if(reportFormat.equalsIgnoreCase("html")){
            JasperExportManager.exportReportToHtmlFile(jasperPrint,path+"\\author.html");
        }
        if(reportFormat.equalsIgnoreCase("pdf")){
            JasperExportManager.exportReportToHtmlFile(jasperPrint,path+"\\author.pdf");
        }
        return "report generated in path: "+ path;

    }


}
