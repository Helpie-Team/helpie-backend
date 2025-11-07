package com.helpie.backend.service.file;

import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode; // 예: 커스텀 에러코드 enum
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private static final String BUCKET_NAME = "helpie-bucket";// 개별 파일 최대 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;
    private static final long MAX_TOTAL_SIZE = 30 * 1024 * 1024L;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "application/pdf"
    );
    private final S3Client s3Client;

    public String uploadFile(MultipartFile file) {
        validateFile(file);
        String key = buildKey(file.getOriginalFilename());
        putObject(file, key);
        setPublicReadAcl(key);
        return publicUrlLike(key); // 필요 시 presigned URL로 대체 가능
    }

    public List<String> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) throw new BusinessException(ErrorCode.FILE_NOT_FOUND){};

        long total = files.stream().mapToLong(MultipartFile::getSize).sum();
        if (total > MAX_TOTAL_SIZE) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE,
                    Map.of("limit", "30MB", "total", (total / (1024*1024)) + "MB")){};
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile f : files) {
            validateFile(f);
            String key = buildKey(f.getOriginalFilename());
            putObject(f, key);
            setPublicReadAcl(key);
            urls.add(publicUrlLike(key));
        }
        return urls;
    }

    private void putObject(MultipartFile file, String key) {
        try (InputStream is = file.getInputStream()) {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            s3Client.putObject(req, RequestBody.fromInputStream(is, file.getSize()));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_STREAM_ERROR){};
        }
    }

    // 업로드 후 객체에 퍼블릭 읽기 ACL 부여
    private void setPublicReadAcl(String key) {
        try {
            PutObjectAclRequest aclReq = PutObjectAclRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ) // ★ 공개 읽기
                    .build();
            s3Client.putObjectAcl(aclReq);
        } catch (S3Exception e) {
            log.error("S3 setObjectAcl failed for key {}: code={}, msg={}", key,
                    e.awsErrorDetails().errorCode(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED,
                    Map.of("stage", "putObjectAcl", "code", e.awsErrorDetails().errorCode(), "msg", e.getMessage())){};
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException(ErrorCode.FILE_NOT_FOUND){};
        String type = Optional.ofNullable(file.getContentType()).orElse("");
        if (!ALLOWED_TYPES.contains(type)) throw new BusinessException(ErrorCode.INVALID_FILE_TYPE){};
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE,
                    Map.of("limit","10MB","file",file.getOriginalFilename())){};
        }
    }

    private String buildKey(String originalName) {
        String ext = (originalName != null && originalName.contains(".")) ?
                originalName.substring(originalName.lastIndexOf(".")) : "";
        LocalDate d = LocalDate.now();
        String datePath = "%d/%02d/%02d".formatted(d.getYear(), d.getMonthValue(), d.getDayOfMonth());
        return "uploads/" + datePath + "/" + UUID.randomUUID() + ext;
    }

    private String publicUrlLike(String key) {
        return "https://kr.object.ncloudstorage.com/" + BUCKET_NAME + "/" + key;
    }
}
