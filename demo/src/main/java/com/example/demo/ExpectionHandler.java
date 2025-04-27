package com.example.demo;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class ExpectionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSize(Model model) {
        model.addAttribute("message", "Plik jest za duży! Maksymalny rozmiar to 10 MB.");
        return "uploadError";
    }
}
