package com.ckn.dictionary.controller;

import com.ckn.dictionary.model.RecordRepository;
import com.ckn.dictionary.pojo.SearchForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/")
public class MainController {

    private RecordRepository repository;

    @Autowired
    public MainController(RecordRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home(ModelMap model) throws UnirestException {
        // List<Record> records = repository.findAll();
        // model.addAttribute("records", records);
        model.addAttribute("searchForm", new SearchForm());
        return "home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String searchWord(ModelMap model,
                             @ModelAttribute("searchForm") SearchForm searchForm,
                             BindingResult result) throws UnirestException, IOException {
        /*if (!result.hasErrors()) {
            repository.save(record);
        }*/

        // TODO Refactor call to wordsAPI
        if(searchForm != null && searchForm.getKey().equals(System.getenv("APP_KEY"))){
            String url = String.format(System.getenv("WORDSAPI_URL"), searchForm.getWord().replaceAll(" ", "%20"));
            System.out.println("url: " + url);

            HttpResponse<String> response = Unirest.get(url)
                    .header("X-Mashape-Key", System.getenv("WORDSAPI_KEY"))
                    .header("X-Mashape-Host", System.getenv("WORDSAPI_HOST"))
                    .asString();

            String jsonBody = response.getBody();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(jsonBody, new TypeReference<Map<String, Object>>(){});
            System.out.println("responseBody=" + map);

            if(map.get("success") != null && !((Boolean) map.get("success"))){
                model.addAttribute("message", "Message from source: " + map.get("message"));
            }else{
                model.addAttribute("responseBody", map);
            }
        }

        return home(model);
    }
}
