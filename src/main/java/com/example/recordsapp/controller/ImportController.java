package com.example.recordsapp.controller;

import com.example.recordsapp.service.CsvImportService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ImportController {

    private final CsvImportService csvImportService;

    public ImportController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @GetMapping("/import")
    public String showImportPage(Model model) {
        model.addAttribute("isImporting", false);
        return "import";
    }

    @GetMapping("/import/template")
    @ResponseBody
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        Resource resource = new ClassPathResource("templates/import-template.csv");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ip-records-template.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @PostMapping("/import/process")
    public String processImport(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a CSV file");
            return "import";
        }

        try {
            List<CsvImportService.CsvRecord> csvRecords = parseCsv(file);
            String jobId = csvImportService.startImport(csvRecords);

            model.addAttribute("jobId", jobId);
            model.addAttribute("isImporting", true);

            return "import";
        } catch (Exception e) {
            model.addAttribute("error", "Error processing CSV: " + e.getMessage());
            model.addAttribute("isImporting", false);
            return "import";
        }
    }

    @GetMapping("/import/progress")
    @ResponseBody
    public Map<String, Object> getProgress(@RequestParam("jobId") String jobId) {
        CsvImportService.ImportProgress progress = csvImportService.getProgress(jobId);
        if (progress == null) {
            return Map.of("status", "NOT_FOUND");
        }

        return Map.of(
            "status", progress.getStatus(),
            "processed", progress.getProcessed(),
            "total", progress.getTotal(),
            "percentage", progress.getPercentage(),
            "error", progress.getError() != null ? progress.getError() : ""
        );
    }

    @GetMapping("/import/results")
    public String getResults(@RequestParam("jobId") String jobId, Model model) {
        CsvImportService.ImportProgress progress = csvImportService.getProgress(jobId);
        if (progress != null && progress.getResults() != null) {
            long successCount = progress.getResults().stream()
                .filter(CsvImportService.ImportResult::isSuccess).count();
            long failedCount = progress.getResults().stream()
                .filter(r -> !r.isSuccess()).count();

            model.addAttribute("results", progress.getResults());
            model.addAttribute("successCount", successCount);
            model.addAttribute("failedCount", failedCount);
            model.addAttribute("jobId", jobId);
        }
        model.addAttribute("isImporting", false);
        return "import";
    }

    private List<CsvImportService.CsvRecord> parseCsv(MultipartFile file) throws IOException, CsvException {
        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        List<String[]> rows = reader.readAll();
        reader.close();

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty");
        }

        boolean hasHeader = rows.get(0)[0].equalsIgnoreCase("name");
        int startIndex = hasHeader ? 1 : 0;

        return rows.stream()
                .skip(startIndex)
                .filter(row -> row.length >= 7)
                .map(this::mapToRecord)
                .collect(Collectors.toList());
    }

    private CsvImportService.CsvRecord mapToRecord(String[] row) {
        CsvImportService.CsvRecord record = new CsvImportService.CsvRecord();
        record.setName(row[0].trim());
        record.setDesignation(row[1].trim());
        record.setExtNumber(row[2].trim());
        record.setIpAddress(row[3].trim());
        record.setMacAddress(row[4].trim());
        record.setRoom(row[5].trim());
        record.setDepartment(row[6].trim());
        return record;
    }
}