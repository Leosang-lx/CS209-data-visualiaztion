package com.example.springproject.web;
import com.example.springproject.domain.PurchaseRecord;
import com.example.springproject.service.PurchaseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PurchaseRecordController {
    @Autowired
    private PurchaseRecordService purchaseRecordService;
    @GetMapping("/allRecord")
    public String list(Model model){
        List<PurchaseRecord> recordList = purchaseRecordService.findAll();
        model.addAttribute("purchaseRecordList",recordList);
        return "PurchaseRecordList";
    }
//    @GetMapping("/insertOneRecord")
//    public String addPurchaseRecord (){
//        return "addPurchaseRecord";
//    }
    @GetMapping("/insertOneRecord")
    public String addPurchaseRecord(Model model){
        model.addAttribute("purchaseRecord",new PurchaseRecord());
        return "addPurchaseRecord";
    }
//    @PostMapping("/allRecord")
//    public String saveNew(PurchaseRecord purchaseRecord){
//        purchaseRecordService.save(purchaseRecord);
//        return "redirect:/allRecord";//重定向
//    }
    @PostMapping("/allRecord")
    public String saveNew(PurchaseRecord purchaseRecord,
                          RedirectAttributes attributes){
        purchaseRecordService.save(purchaseRecord);
        attributes.addFlashAttribute("msg","Update has been saved");
        return "redirect:/allRecord";//重定向
    }

    @GetMapping("/findOneRecord{id}")
    public String findPurchaseRecord(@PathVariable long id, Model model){
        PurchaseRecord purchaseRecord=purchaseRecordService.findById(id);
        model.addAttribute("purchaseRecord",purchaseRecord);
        return "addPurchaseRecord";
    }
    @GetMapping("/deleteOneRecord{id}")
    public String deletePurchaseRecord(@PathVariable long
                                               id,RedirectAttributes attributes){
        purchaseRecordService.deleteById(id);
        attributes.addFlashAttribute("msg","Delete successfully");
        return "redirect:/allRecord";//删完重定向回records
    }
}