package com.ragplatform.document.events;

import java.util.UUID;

public record DocumentIngestionStatusEvent(
        UUID documentId,
        String status,
        String message

) {

}
