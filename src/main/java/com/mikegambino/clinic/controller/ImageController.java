package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/img/{name}")
    public byte[] getImg(@PathVariable(value = "name") String name) throws IOException {
        return imageService.getImg(name);
    }
}