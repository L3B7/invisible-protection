package hu.bpe.ipmapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {
    @RequestMapping()
    public String results(){

        return "{\"text1\":\"Prevent 81 Account takeovers\",\"text2\":\"Catch 1200 scammers\",\"text3\":\"Stop 497 Fraudulent orders\"}";
    }
}