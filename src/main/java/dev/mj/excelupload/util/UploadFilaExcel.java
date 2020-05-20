package dev.mj.excelupload.util;

import com.monitorjbl.xlsx.StreamingReader;
import dev.mj.excelupload.enums.HeaderCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Component
public class UploadFilaExcel {

    private UploadUtil uploadUtil;

    public List<Map<String, String>> upload(MultipartFile file) throws Exception {

        Path tempDir = Files.createTempDirectory("");

        File xlsxFile = tempDir.resolve(file.getOriginalFilename()).toFile();

        file.transferTo(xlsxFile);

        //File xlsxFile = new File(tempFile);
        if (!xlsxFile.exists()) {
            System.err.println("Not found or not a file: " + xlsxFile.getPath());
            return null;
        }

        List<String> lSheet;

        try (Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(xlsxFile)) {

            Sheet sheet = workbook.getSheetAt(0);

            Supplier<Stream<Row>> rowStreamSupplier = uploadUtil.getRowStreamSupplier(sheet);

            Row headerRow = rowStreamSupplier.get().findFirst().get();

            List<String> headerCells = uploadUtil.getStream(headerRow)
                    .map(Cell::getStringCellValue)
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            List<String> headerCellsTest = Stream.of(HeaderCell.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());

            int colCount = headerCells.size();

            List<Map<String, String>>  mapList = rowStreamSupplier.get()
                    .skip(1)
                    .map(row -> {

                        List<String> cellList = uploadUtil.getStream(row)
                                .map(Cell::getStringCellValue)
                                .collect(Collectors.toList());

                        return uploadUtil.cellIteratorSupplier(colCount)
                                .get()
                                .collect(toMap(headerCellsTest::get, cellList::get));
                    })
                    .collect(Collectors.toList());

            mapList.isEmpty();

            Map<String, String> map = mapList.get(mapList.size() - 1);

            return null;

        }

    }
}
