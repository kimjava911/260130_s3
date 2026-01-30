package kr.java.s3.service;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 1. S3 업로드: 저장된 파일명을 반환
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        s3Template.upload(bucket, fileName, file.getInputStream(),
                ObjectMetadata.builder().contentType(file.getContentType()).build());
        return fileName;
    }

    // 2. S3 다운로드: S3Resource(InputStream 포함)를 반환
    public S3Resource getFileResource(String fileName) {
        return s3Template.download(bucket, fileName);
    }
}