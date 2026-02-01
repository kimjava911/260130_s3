package kr.java.s3.service;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import kr.java.s3.entity.FileEntity;
import kr.java.s3.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;
    private final FileRepository fileRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 1. S3 업로드: 저장된 파일명을 반환하고 DB에 저장
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;

        s3Template.upload(bucket, storedFileName, file.getInputStream(),
                ObjectMetadata.builder().contentType(file.getContentType()).build());

        FileEntity fileEntity = FileEntity.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .uploadTime(LocalDateTime.now())
                .build();
        fileRepository.save(fileEntity);

        return storedFileName;
    }

    // 2. S3 다운로드: S3Resource(InputStream 포함)를 반환
    public S3Resource getFileResource(String fileName) {
        return s3Template.download(bucket, fileName);
    }

    // 3. 파일 목록 조회
    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }
}
