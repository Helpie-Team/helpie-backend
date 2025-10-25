package com.helpie.backend.service.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 메시지 압축 서비스
 * 큰 메시지를 압축하여 네트워크 전송량을 줄여 성능을 최적화합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageCompressionService {
    
    private final ObjectMapper objectMapper;
    
    // 압축 임계값 (바이트) - 이보다 큰 메시지만 압축
    private static final int COMPRESSION_THRESHOLD = 500;
    
    /**
     * 메시지 압축
     */
    public CompressedMessage compressMessage(Object message) {
        try {
            // 객체를 JSON 문자열로 변환
            String jsonString = objectMapper.writeValueAsString(message);
            byte[] originalBytes = jsonString.getBytes("UTF-8");
            
            // 압축 임계값 체크
            if (originalBytes.length < COMPRESSION_THRESHOLD) {
                return new CompressedMessage(jsonString, false, originalBytes.length, originalBytes.length);
            }
            
            // GZIP 압축
            byte[] compressedBytes = gzipCompress(originalBytes);
            String compressedData = Base64.getEncoder().encodeToString(compressedBytes);
            
            log.debug("메시지 압축 완료 - 원본: {} bytes, 압축: {} bytes, 압축률: {:.1f}%",
                originalBytes.length, compressedBytes.length, 
                (1.0 - (double) compressedBytes.length / originalBytes.length) * 100);
            
            return new CompressedMessage(compressedData, true, originalBytes.length, compressedBytes.length);
            
        } catch (Exception e) {
            log.error("메시지 압축 실패", e);
            // 압축 실패 시 원본 반환
            try {
                String jsonString = objectMapper.writeValueAsString(message);
                return new CompressedMessage(jsonString, false, jsonString.length(), jsonString.length());
            } catch (JsonProcessingException ex) {
                log.error("메시지 JSON 변환 실패", ex);
                return new CompressedMessage("", false, 0, 0);
            }
        }
    }
    
    /**
     * 메시지 압축 해제
     */
    public String decompressMessage(CompressedMessage compressedMessage) {
        if (!compressedMessage.isCompressed()) {
            return compressedMessage.getData();
        }
        
        try {
            byte[] compressedBytes = Base64.getDecoder().decode(compressedMessage.getData());
            byte[] decompressedBytes = gzipDecompress(compressedBytes);
            
            return new String(decompressedBytes, "UTF-8");
            
        } catch (Exception e) {
            log.error("메시지 압축 해제 실패", e);
            return compressedMessage.getData();
        }
    }
    
    /**
     * GZIP 압축
     */
    private byte[] gzipCompress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(baos)) {
            gzipOut.write(data);
        }
        return baos.toByteArray();
    }
    
    /**
     * GZIP 압축 해제
     */
    private byte[] gzipDecompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (GZIPInputStream gzipIn = new GZIPInputStream(bais)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIn.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
        }
        
        return baos.toByteArray();
    }
    
    /**
     * 압축 효율성 체크
     */
    public boolean shouldCompress(String content) {
        if (content == null) {
            return false;
        }
        
        byte[] bytes = content.getBytes();
        return bytes.length >= COMPRESSION_THRESHOLD;
    }
    
    /**
     * 압축된 메시지 DTO
     */
    public static class CompressedMessage {
        private final String data;
        private final boolean compressed;
        private final int originalSize;
        private final int compressedSize;
        private final long timestamp;
        
        public CompressedMessage(String data, boolean compressed, int originalSize, int compressedSize) {
            this.data = data;
            this.compressed = compressed;
            this.originalSize = originalSize;
            this.compressedSize = compressedSize;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getData() {
            return data;
        }
        
        public boolean isCompressed() {
            return compressed;
        }
        
        public int getOriginalSize() {
            return originalSize;
        }
        
        public int getCompressedSize() {
            return compressedSize;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public double getCompressionRatio() {
            if (originalSize == 0) {
                return 0;
            }
            return (1.0 - (double) compressedSize / originalSize) * 100;
        }
    }
}