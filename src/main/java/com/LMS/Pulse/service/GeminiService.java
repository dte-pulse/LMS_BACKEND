package com.LMS.Pulse.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final Client client;

    public String askGemini(String prompt) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null);
            return response.text();
        } catch (Exception e) {
            System.err.println("Error calling Gemini: " + e.getMessage());
            return "Error: Could not get a response from the AI model.";
        }
    }

    public int getScoreForAnswer(String question, String answer) {
        String prompt = String.format(
                "Evaluate the following answer for the given question and provide a score out of 100. " +
                        "Consider relevance, clarity, and completeness. " +
                        "Question: '%s' " +
                        "Answer: '%s' " +
                        "Return only the integer score, nothing else.",
                question, answer);
        try {
            String scoreText = askGemini(prompt).trim();
            return Integer.parseInt(scoreText.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            System.err.println("Failed to get score from Gemini: " + e.getMessage());
            return 0;
        }
    }
}
