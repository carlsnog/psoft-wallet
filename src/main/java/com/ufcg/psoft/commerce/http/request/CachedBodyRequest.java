package com.ufcg.psoft.commerce.http.request;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Wrapper para o HttpServletRequest que permite ler o corpo da requisição novamente.
 */
public class CachedBodyRequest extends HttpServletRequestWrapper {
    private byte[] body;

    public CachedBodyRequest(HttpServletRequest request) {
        super(request);

        try {
            var stream = super.getInputStream();
            this.body = stream.readAllBytes();
        } catch (IOException e) {
            this.body = new byte[0];
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        var bodyStream = new ByteArrayInputStream(this.body);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return body.length == 0;
            }

            public boolean isReady() {
                return true;
            }

            public void setReadListener(ReadListener listener) {
            }

            public int read() {
                return bodyStream.read();
            }
        };
    }

}
