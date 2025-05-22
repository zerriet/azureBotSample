package com.example.azurebotsample.controller;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClassificationController {

    @PostMapping("/intent")
    public String classify(@RequestParam String userInput) {
        try {
            URL url = URI.create("http://localhost:5001/predict").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // JSON body
            String jsonInputString = "{\"userInput\": \"" + userInput.replace("\"", "\\\"") + "\"}";

            // Send JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response
            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            conn.disconnect();

            // Extract prediction from JSON response: {"prediction":"check_balance"}
            String result = response.toString();
            int startIdx = result.indexOf(":\"") + 2;
            int endIdx = result.indexOf("\"", startIdx);
            return result.substring(startIdx, endIdx);

        } catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
    }
}
