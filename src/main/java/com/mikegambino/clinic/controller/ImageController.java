package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.service.ImageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
public class ImageController {
    private final ImageService imageService;
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/img/{name}")
    public byte[] getImg(@PathVariable(value = "name") String name) throws IOException {
        return imageService.getImg(name);
    }
}