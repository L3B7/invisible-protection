package hu.bpe.ipmapper;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.text.html.HTML;

@Controller
public class IndexController {
    @RequestMapping("/")
    public String index(){

        return "redirect:/public/index.html";
    }
}
