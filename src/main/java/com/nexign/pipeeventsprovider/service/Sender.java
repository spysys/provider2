package com.nexign.pipeeventsprovider.service;

import com.nexign.pipeeventsprovider.model.PipeEvent;

public interface Sender {
    void sendMessage(PipeEvent data);

    void sendBar(String data);
}
