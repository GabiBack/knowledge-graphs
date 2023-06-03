package com.example.knowledgegraphs;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelFileExporter {


    private Workbook workbook = new XSSFWorkbook();
    CellStyle headerStyle = this.setCellStyle();

    public ExcelFileExporter() {
    }

    private void createSheet(String sheetName, List<RDFGroup> rdfGroups){
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);

        this.createHeader("Predicate",0, headerRow);
        this.createHeader("Predicate Theme",1, headerRow);
        this.createHeader("Value", 2, headerRow);
        this.addDataIntoCells(rdfGroups, sheet);
    }

    public void writeExcel(List<RDFGroup> rdfGroups, String fileName, List<RDFGroup> rdfGroupsFromSubject) throws IOException {
        this.createSheet("outcome", rdfGroups);
        this.createSheet("subject", rdfGroupsFromSubject);

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            workbook.write(outputStream);
        }
    }

    private Cell createHeader(String cellValue, int headerNumber, Row headerRow){
        Cell headerCell = headerRow.createCell(headerNumber);
        headerCell.setCellValue(cellValue);
        headerCell.setCellStyle(this.headerStyle);

        return headerCell;
    }

    private CellStyle setCellStyle(){
        CellStyle headerCellStyle = this.workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setFont(setFontStyle(this.workbook));

        return headerCellStyle;
    }

    private Font setFontStyle(Workbook workbook){
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());

        return font;
    }

    private Sheet addDataIntoCells(List<RDFGroup> rdfGroups, Sheet sheet){
        int rowNum = 1;
        for (RDFGroup data : rdfGroups) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getPredicate());
            row.createCell(1).setCellValue(data.getPredicateTheme());
            row.createCell(2).setCellValue(data.getValue());
        }

        Row subjectRow = sheet.createRow(rowNum+1);
        subjectRow.createCell(0).setCellValue("Subject");
        subjectRow.createCell(1).setCellValue(rdfGroups.get(0).getSubject().substring(10));
        return sheet;
    }


}
