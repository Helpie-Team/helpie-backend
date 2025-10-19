package com.helpie.backend.dto.global;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class Response<T> {

    private int statusCode;
    private String message;
    private T result;

    public static <T> Response<T> success() {
        return new Response<T>(HttpStatus.NO_CONTENT.value(), HttpStatus.NO_CONTENT.getReasonPhrase(), null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<T>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), result);
    }

    public static <T> Response<T> error(int statusCode, String message, T result) {
        return new Response<T>(statusCode, message, result);
    }

    @Builder
    private Response(int statusCode, String message, T result) {
        this.statusCode = statusCode;
        this.message = message;
        this.result = result;
    }
}
