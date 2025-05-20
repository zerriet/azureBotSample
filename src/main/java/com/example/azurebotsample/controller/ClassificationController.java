package com.example.azurebotsample.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClassificationController {


@PostMapping("/intent")
public String classify(@RequestParam String userInput) {
    try {
        ProcessBuilder pb = new ProcessBuilder(
                "/Users/pramitshanmugababu/opt/anaconda3/envs/newenv/bin/python",
                "set-fit/predict.py",
                userInput
        );

        pb.environment().put("LC_ALL", "en_US.UTF-8");
        pb.environment().put("LANG", "en_US.UTF-8");
        pb.environment().put("OMP_NUM_THREADS", "1");
        pb.environment().put("KMP_DUPLICATE_LIB_OK", "TRUE");
        pb.environment().put("TOKENIZERS_PARALLELISM", "false");

        pb.directory(new File(System.getProperty("user.dir")));
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String lastLine = "";
        while ((line = reader.readLine()) != null) {
            lastLine = line;
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            return "Error: Python script exited with code " + exitCode;
        }

        return lastLine.trim(); 
    } catch (Exception e) {
        e.printStackTrace();
        return "Exception: " + e.getMessage();
    }
}

}
