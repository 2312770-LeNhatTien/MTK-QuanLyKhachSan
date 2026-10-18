package vn.edu.dlu.khachsan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class LoiController {

    @GetMapping("/loi/403")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String khongCoQuyen() {
        return "error/403";
    }
}
