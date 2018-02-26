package com.ckn.dictionary.controller;

import javax.validation.Valid;
import java.util.List;

import com.ckn.dictionary.model.Record;
import com.ckn.dictionary.model.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class MainController {

    private RecordRepository repository;

    @Autowired
    public MainController(RecordRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home(ModelMap model) {
        List<Record> records = repository.findAll();
        model.addAttribute("records", records);
        model.addAttribute("insertRecord", new Record());
        return "home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String insertData(ModelMap model,
                             @ModelAttribute("insertRecord") @Valid Record record,
                             BindingResult result) {
        if (!result.hasErrors()) {
            repository.save(record);
        }
        return home(model);
    }
}
