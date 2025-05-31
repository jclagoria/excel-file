package dev.mj.excelupload.service;

import java.io.File; // Though tempFile is not directly used, multipartFile.transferTo creates a file at tempFilePath
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter; // For reading cell values as displayed in Excel
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.monitorjbl.xlsx.StreamingReader;
// Unused imports removed:
// import static java.util.stream.Collectors.toMap;
// import java.util.function.Supplier;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;
// import dev.mj.excelupload.enums.HeaderCell;
// import org.apache.poi.ss.usermodel.WorkbookFactory;
// import dev.mj.excelupload.util.UploadUtil;
// import javax.swing.*;

@Service
public class UploadService {

    public List<Map<String, String>> upload(MultipartFile multipartFile) {
        Path tempFilePath = null;
        // File tempFile = null; // Unused variable
        List<Map<String, String>> dataList = new ArrayList<>();

        try {
            // 1. Convert MultipartFile to a temporary File
            tempFilePath = Files.createTempFile("upload-", ".xlsx");
            multipartFile.transferTo(tempFilePath);
            // tempFile = tempFilePath.toFile(); // Unused assignment

            // 2. Use StreamingReader to open and read the Excel workbook
            try (InputStream is = Files.newInputStream(tempFilePath);
                 Workbook workbook = StreamingReader.builder()
                         .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                         .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                         .open(is)) {          // InputStream or File for XLSX file (required)

                // 3. Access the first sheet
                Sheet sheet = workbook.getSheetAt(0);
                if (sheet == null) {
                    throw new RuntimeException("Excel file is empty or the first sheet is not found.");
                }

                List<String> headers = new ArrayList<>();
                Iterator<Row> rowIterator = sheet.iterator();

                // 4. Read the first row as headers
                if (rowIterator.hasNext()) {
                    Row headerRow = rowIterator.next();
                    for (Cell cell : headerRow) {
                        headers.add(cell.getStringCellValue());
                    }
                } else {
                    throw new RuntimeException("Excel file is empty or header row is missing.");
                }

                // 5. Iterate over the remaining rows
                while (rowIterator.hasNext()) {
                    Row dataRow = rowIterator.next();
                    Map<String, String> rowMap = new HashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        Cell cell = dataRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        String cellValue = "";
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    cellValue = cell.getStringCellValue();
                                    break;
                                case NUMERIC:
                                    // Handle numeric cells, convert to string. You might need specific formatting.
                                    // For simplicity, using DataFormatter to get the string value as displayed in Excel.
                                    org.apache.poi.ss.usermodel.DataFormatter formatter = new org.apache.poi.ss.usermodel.DataFormatter();
                                    cellValue = formatter.formatCellValue(cell);
                                    break;
                                case BOOLEAN:
                                    cellValue = String.valueOf(cell.getBooleanCellValue());
                                    break;
                                case FORMULA:
                                    // Handle formula cells - getting cached value
                                    // For simplicity, using DataFormatter.
                                    org.apache.poi.ss.usermodel.DataFormatter formulaFormatter = new org.apache.poi.ss.usermodel.DataFormatter();
                                    cellValue = formulaFormatter.formatCellValue(cell);
                                    break;
                                default:
                                    cellValue = ""; // Or handle other types as needed
                            }
                        }
                        rowMap.put(headers.get(i), cellValue);
                    }
                    dataList.add(rowMap);
                }
            }
            return dataList;
        } catch (Exception e) {
            // 9. Basic error handling
            // Log the exception here if a logging framework is set up
            throw new RuntimeException("Failed to process Excel file: " + e.getMessage(), e);
        } finally {
            // 9. Ensure the temporary file is deleted
            if (tempFilePath != null) {
                try {
                    Files.deleteIfExists(tempFilePath);
                } catch (IOException e) {
                    // Log this error if a logging framework is available
                    System.err.println("Failed to delete temporary file: " + tempFilePath + " with error: " + e.getMessage());
                }
            }
        }
    }
}
