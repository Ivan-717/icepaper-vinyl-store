package com.bingzhi.vinyl.controller.user;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStore embeddingStore;

    @PostMapping("/chat")
    public Map<String,String> chat(@RequestBody Map<String,String> request){
        String userMessage=request.get("message");

        String prompt="你是一个黑胶唱片店的AI客服助手。请用友好，热情的语气回答问题。\n \n 用户问题：" +userMessage;

        //调用大模型生成回答
        String aiResponse=chatLanguageModel.generate(prompt);

        Map<String, String> result = new HashMap<>();
        result.put("reply", aiResponse);
        return result;

    }
}
