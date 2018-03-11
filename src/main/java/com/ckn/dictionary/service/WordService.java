package com.ckn.dictionary.service;

import com.ckn.dictionary.model.Word;
import com.ckn.dictionary.model.WordRepository;
import com.ckn.dictionary.pojo.SearchForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    public Map<String, Object> search(SearchForm searchForm) throws UnirestException, IOException {
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

        save(searchForm.getWord());

        return map;
    }

    public void save(String word){
        Word wordModel = new Word();
        wordModel.setWord(word);
        wordModel.setCreatedDate(new Date());
        wordRepository.save(wordModel);
    }

    public List<Word> getHistory(){
        return wordRepository.findAll();
    }

}
