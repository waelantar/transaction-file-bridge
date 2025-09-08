package wael_project.transaction_file_bridge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import wael_project.transaction_file_bridge.model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    @Value("${input.directory:/app/input}")
    private String inputDirectory;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/upload")
    public String uploadForm(Model model) {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        System.out.println(file.getOriginalFilename());
        try {
            // Create input directory if it doesn't exist
            Path uploadPath = Paths.get(inputDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the file
            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            model.addAttribute("message", "File uploaded successfully: " + fileName);
            model.addAttribute("fileName", fileName);

        } catch (IOException e) {
            model.addAttribute("message", "Failed to upload file: " + e.getMessage());
        }

        return "upload";
    }

    @GetMapping("/files")
    public String listFiles(Model model) {


        System.out.println(new StringBuilder().append(inputDirectory).append(".camel").toString());
        try {
            File inputDir = new File("src/main/resources/input", ".camel");
            File[] files = inputDir.listFiles();
             System.out.println(Arrays.toString(files));
            if (files != null) {
                List<FileInfo> fileInfos = Arrays.stream(files)
                        .filter(File::isFile) // Only include files, not directories
                        .map(file -> new FileInfo(
                                file.getName(),
                                file.length(),
                                new Date(file.lastModified()),
                                file.exists()
                        ))
                        .collect(Collectors.toList());

                model.addAttribute("fileInfos", fileInfos);
            } else {
                model.addAttribute("fileInfos", Collections.emptyList());
            }

        } catch (Exception e) {
            model.addAttribute("message", "Error reading files: " + e.getMessage());
            model.addAttribute("fileInfos", Collections.emptyList());
        }

        return "files";
    }

    @GetMapping("/health")
    public String healthCheck(Model model) {
        return "health";
    }
}
