package com.ragplatform.document.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.ragplatform.document.dto.RagIngestionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RagServiceClient {
    private final RestTemplate restTemplate;

    @Value("${rag.service.url}")
    private String ragServiceUrl;

    public RagIngestionResponse ingestDocument(MultipartFile file) throws IOException {
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() != null
                        ? file.getOriginalFilename()
                        : "document.pdf";
            }
        };

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_PDF);

        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(fileResource, fileHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<RagIngestionResponse> response = restTemplate
                .postForEntity(ragServiceUrl + "/internal/documents/ingest", request, RagIngestionResponse.class);

        return response.getBody();

    }
}
