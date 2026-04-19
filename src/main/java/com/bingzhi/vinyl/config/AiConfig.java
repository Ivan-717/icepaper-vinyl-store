package com.bingzhi.vinyl.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AiConfig {
    @Value("${zhipu.api.key}")
    private String apiKey;

    //创建对话模型  作用：接受用户问题，返回ai回答
    @Bean
    public ChatLanguageModel chatLanguageModel(){
        return ZhipuAiChatModel.builder()
                .apiKey(apiKey)
                .model("glm-4-flash") //免费模型
                .temperature(0.7)  //温度越高回答越随机，0.7适合客服
                .maxRetries(2) //重试次数
                .maxToken(1024) //最大回复常数
                .build();
    }

    //创建嵌入模型 作用：把商品信息转成向量（一串数字）
    @Bean
    public EmbeddingModel embeddingModel(){
        return ZhipuAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .model("embedding-2")
                .build();
    }

    //创建向量数据库存储 作用：保存商品信息转换成的向量，方便后续检索
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(){
        return new InMemoryEmbeddingStore<>();
    }

}
