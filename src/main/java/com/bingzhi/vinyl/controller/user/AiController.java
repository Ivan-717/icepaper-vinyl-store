package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.service.AiService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private EmbeddingStore embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @PostMapping("/chat")
    public Map<String,String> chat(@RequestHeader(value = "Authorization",required = false)String token,@RequestBody Map<String,String> request){
        String userMessage=request.get("message");


        Embedding userMessageEmbedding=embeddingModel.embed(userMessage).content();

        @SuppressWarnings("unchecked")
        List<EmbeddingMatch> matches=embeddingStore.findRelevant(userMessageEmbedding,3);
        System.out.println("🔍 检索到 " + matches.size() + " 条相关商品");

        StringBuilder productInfo=new StringBuilder();
        for(EmbeddingMatch match:matches){
            productInfo.append(match.embedded().toString()).append("\n");
        }

        String reply= aiService.chat(userMessage,token,productInfo.toString());

        Map<String, String> result = new HashMap<>();
        result.put("reply", reply);
        return result;

    }
}
