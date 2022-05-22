package com.example.springproject.app;

import com.example.springproject.domain.PurchaseRecord;
import com.example.springproject.service.PurchaseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exer")
public class PurchaseRecordApp {
    @Autowired
    private PurchaseRecordService purchaseRecordService;

    @GetMapping("/record")
    public List<PurchaseRecord> findAll(){
        //Fot testing the concrete class
        System.out.println(purchaseRecordService.getClass().getName());
        return purchaseRecordService.findAll();
    }

    @PostMapping("/record")
    public PurchaseRecord addOne(PurchaseRecord purchaseRecord){
        return purchaseRecordService.save(purchaseRecord);
    }

    @PutMapping("/record")
    public PurchaseRecord update(@RequestParam long id,
                                 @RequestParam String username,
                                 @RequestParam String date,
                                 @RequestParam double money,
                                 @RequestParam int type,
                                 @RequestParam String description){
        PurchaseRecord purchaseRecord=new PurchaseRecord();
        purchaseRecord.setId(id);
        purchaseRecord.setUsername(username);
        purchaseRecord.setDescription(description);
        purchaseRecord.setMoney(money);
        purchaseRecord.setType(type);
        purchaseRecord.setDate(date);
        return purchaseRecordService.save(purchaseRecord);
    }

    @PutMapping("/record2")
    public PurchaseRecord update(PurchaseRecord purchaseRecord){
        return purchaseRecordService.save(purchaseRecord);
    }

    @DeleteMapping("record/{id}")
    public void deleteOne(@PathVariable long id){
        purchaseRecordService.deleteById(id);
    }

}
