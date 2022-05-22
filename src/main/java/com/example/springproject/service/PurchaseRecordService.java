package com.example.springproject.service;

import com.example.springproject.domain.PurchaseRecord;

import java.util.List;

public interface PurchaseRecordService {
    public List<PurchaseRecord> findAll();
    public PurchaseRecord save(PurchaseRecord purchaseRecord);
    public void deleteById(long id);
    public PurchaseRecord findById(long id);
}
