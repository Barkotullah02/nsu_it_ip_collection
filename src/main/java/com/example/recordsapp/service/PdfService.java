package com.example.recordsapp.service;

import com.example.recordsapp.model.IpHistory;
import com.example.recordsapp.model.Record;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfService {

    public ModelAndView generateIpRecordsView(List<Record> records, String filterInfo) {
        ModelAndView mav = new ModelAndView("records/pdf");
        mav.addObject("records", records);
        mav.addObject("filterInfo", filterInfo);
        mav.addObject("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return mav;
    }

    public ModelAndView generateHistoryView(List<IpHistory> histories, String ipAddress) {
        ModelAndView mav = new ModelAndView("records/pdf-history");
        mav.addObject("histories", histories);
        mav.addObject("ipAddress", ipAddress);
        mav.addObject("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return mav;
    }
}