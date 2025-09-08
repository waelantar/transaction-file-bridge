package wael_project.transaction_file_bridge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class FileUploadController {

    @Value("${input.directory:/app/input}")
    private String inputDirectory;

    @GetMapping("/upload")
    public String uploadForm(Model model) {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
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
        try {
            Path inputPath = Paths.get(inputDirectory);
            if (Files.exists(inputPath)) {
                File[] files = inputPath.toFile().listFiles();
                model.addAttribute("files", files);
            } else {
                model.addAttribute("files", new File[0]);
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error listing files: " + e.getMessage());
            model.addAttribute("files", new File[0]);
        }
        return "files";
    }
}
