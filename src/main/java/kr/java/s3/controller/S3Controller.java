package kr.java.s3.controller;

import io.awspring.cloud.s3.S3Resource;
import kr.java.s3.entity.FileEntity;
import kr.java.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/")
    public String index(Model model) {
        List<FileEntity> files = s3Service.getAllFiles();
        model.addAttribute("files", files);
        return "upload";
    }

    // 파일 업로드 처리
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        s3Service.uploadFile(file);
        return "redirect:/";
    }

    // 내부 서빙 엔드포인트: 이미지를 바이트 스트림으로 반환
    @GetMapping("/images/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName) {
        S3Resource resource = s3Service.getFileResource(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
