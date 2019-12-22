package com.nexign.pipeeventsprovider.service;

import com.nexign.pipeeventsprovider.model.PipeEvent;
import lombok.NonNull;

public interface PipeEventsProducer {
    void produce(@NonNull PipeEvent event);
}
