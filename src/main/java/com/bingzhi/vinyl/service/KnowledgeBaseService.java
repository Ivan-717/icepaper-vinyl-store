package com.bingzhi.vinyl.service;

import com.bingzhi.vinyl.entity.Product;
import com.bingzhi.vinyl.mapper.ProductMapper;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeBaseService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    //项目启动时自动执行：把商品信息加载到向量数据库
    @PostConstruct  //做准备工作
    public void initKnowledgeBase(){
        System.out.println("🤖 开始初始化AI知识库...");

        //查询所有商品
        List<Product> products=productMapper.findAll();
        System.out.println("📦 找到 " + products.size() + " 个商品");

        //遍历商品，存入数据库
        for(Product product:products){
            //把商品信息拼接成一个文本
            String content=String.format(
                    "商品名称：%s\n艺人：%s\n描述：%s\n价格：%.2f元",
                    product.getName(),
                    product.getArtist(),
                    product.getDescription(),
                    product.getPrice()
            );

            //创建文本段
            TextSegment segment=TextSegment.from(content);

            //将文本段转化向量并存入数据库
            embeddingStore.add(embeddingModel.embed(segment).content(),segment);

            System.out.println("✅ 已加载商品：" + product.getName());
        }

        System.out.println("🎉 AI知识库初始化完成！共加载 " + products.size() + " 个商品");
    }
}
