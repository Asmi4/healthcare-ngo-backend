package com.example.healthcare; // Make sure this matches your project's actual package name!

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*",allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}) // Allows our future React frontend to talk to this API safely
public class TriageController {

    @Autowired
    private ChatLanguageModel chatModel;

    @PostMapping("/triage")
    public Map<String, String> processIntake(@RequestBody Map<String, String> requestData) {
        String name = requestData.getOrDefault("name", "Anonymous");
        String role = requestData.getOrDefault("role", "Patient");
        String message = requestData.getOrDefault("message", "");

        // 1. Construct a highly specific prompt forcing the LLM to give us clean JSON
        String prompt = String.format(
                "You are an AI assistant for a healthcare NGO. Analyze the following registration submission:\n\n" +
                        "Name: %s\n" +
                        "Role: %s\n" +
                        "Message/Situation: %s\n\n" +
                        "Task: Respond ONLY with a valid JSON object containing exactly these four keys: \"urgencyLevel\", \"category\", \"keyTakeaway\", and \"suggestedAutoResponse\".\n\n" +
                        "Guidelines:\n" +
                        "- \"urgencyLevel\": Output either '🔴 High Priority' or '🟢 Standard Review'.\n" +
                        "- \"category\": The operational category (e.g., Medical Assistance Request, Volunteer Coordination).\n" +
                        "- \"keyTakeaway\": A 1-sentence concise extraction of what they need.\n" +
                        "- \"suggestedAutoResponse\": A compassionate, professional response draft. If a Patient, acknowledge symptoms warmly and advise next steps. If a Volunteer, thank them with enthusiasm and mention onboarding. (Max 3-4 sentences).\n\n" +
                        "CRITICAL: Do not include markdown formatting like ```json or any introductory conversational text. Return only raw, valid JSON.",
                name, role, message
        );

        try {
            // 2. Fire the prompt over to Groq/Llama3
            String llmResponse = chatModel.generate(prompt);

            // 3. Parse the string JSON response directly into a Java Map to return to frontend
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(llmResponse, Map.class);

        } catch (Exception e) {
            // Fallback safety net in case of network issues or bad JSON format from LLM
            System.err.println("LLM Execution failed: " + e.getMessage());
            Map<String, String> fallback = new HashMap<>();
            fallback.put("urgencyLevel", "🟢 Standard Review (System Fallback)");
            fallback.put("category", "General Operational Queue");
            fallback.put("keyTakeaway", "Could not run automated summary. Manual review required.");
            fallback.put("suggestedAutoResponse", "Hi " + name + ", thank you for reaching out to us. We have received your message and our team will get back to you shortly.");
            return fallback;
        }
    }
}