package com.helpie.backend.controller.file;

import com.helpie.backend.dto.global.Response;
import com.helpie.backend.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileTestController {
    private final FileService fileService;

    @Operation(
            summary = "단일 파일 업로드",
            description = """
            Multipart 단일 파일 업로드.
            - 허용 타입: image/jpeg, image/png, image/gif, application/pdf
            - 단일 파일 최대 10MB
            업로드 후 접근 가능한 URL을 반환
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = """
                {
                  "success": true,
                  "data": "https://kr.object.ncloudstorage.com/helpie-bucket/uploads/2025/11/07/uuid.png"
                }
                """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 파일/사이즈 초과/허용 타입 아님"),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 S3 업로드 실패")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<String> upload(@RequestPart("file") MultipartFile file) {
        return Response.success(fileService.uploadFile(file));
    }

    @Operation(
            summary = "여러 파일 업로드",
            description = """
            Multipart 다중 파일 업로드.
            - 같은 키(files)로 여러 개 첨부
            - 파일당 최대 10MB, 요청 전체 30MB
            업로드 후 각 파일 URL 리스트를 반환합니다.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = @ExampleObject(value = """
                {
                  "success": true,
                  "data": [
                    "https://kr.object.ncloudstorage.com/helpie-bucket/uploads/2025/11/07/uuid1.png",
                    "https://kr.object.ncloudstorage.com/helpie-bucket/uploads/2025/11/07/uuid2.pdf"
                  ]
                }
                """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 파일/사이즈 초과/허용 타입 아님"),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 S3 업로드 실패")
    })
    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<List<String>> uploadBatch(@RequestPart("files") List<MultipartFile> files) {
        return Response.success(fileService.uploadFiles(files));
    }
}
