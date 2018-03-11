package com.ckn.dictionary.controller;

import com.ckn.dictionary.model.Word;
import com.ckn.dictionary.pojo.SearchForm;
import com.ckn.dictionary.service.WordService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private WordService wordService;

    @RequestMapping(method = RequestMethod.GET)
    public String home(ModelMap model) throws UnirestException {
        List<Word> history = wordService.getHistory();
        model.addAttribute("history", history);
        model.addAttribute("searchForm", new SearchForm());
        return "home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String searchWord(ModelMap model,
                             @ModelAttribute("searchForm") SearchForm searchForm,
                             BindingResult result) throws UnirestException, IOException {
        if(searchForm != null && searchForm.getKey().equals(System.getenv("APP_KEY"))){
            Map<String, Object> map = wordService.search(searchForm);
            if(map.get("success") != null && !((Boolean) map.get("success"))){
                model.addAttribute("message", "Message from source: " + map.get("message"));
            }else{
                if(map.get("results") != null){
                    model.addAttribute("definitions", map.get("results"));
                    model.addAttribute("word", searchForm.getWord());
                }
            }
        }
        return home(model);
    }
}
