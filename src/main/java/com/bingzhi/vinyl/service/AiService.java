package com.bingzhi.vinyl.service;

import com.bingzhi.vinyl.entity.Orders;
import com.bingzhi.vinyl.mapper.OrderMapper;
import com.bingzhi.vinyl.utils.JwtUtil;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class AiService {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private OrderMapper orderMapper;

    @Value("classpath:faq.txt")
    private Resource faqResource;

    private String faqContent;

    //启动时加载固定文档
    @PostConstruct
    public void loadFaq(){
        try {
            try (InputStream is = faqResource.getInputStream()) {
                faqContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("✅ 加载固定文档成功，长度：" + faqContent.length());
            }
        }catch (Exception e) {
                faqContent="";
                System.err.println("❌ 加载固定文档失败：" + e.getMessage());
        }

    }

    //获取用户订单信息
    private String getUserOrderInfo(Long userId){
        if(userId==null){
            return "用户未登录，无法查询订单信息";
        }

        List<Orders> orders=orderMapper.findByUserId(userId);
        if(orders==null || orders.isEmpty()){
            return "该用户暂无订单";
        }

        StringBuilder sb=new StringBuilder();
        sb.append("该用户的最新订单：\n");
        for (int i=0;i<Math.min(orders.size(),5);i++){
            Orders order=orders.get(i);
            sb.append("-订单号：").append(order.getOrderNo())
              .append(",状态：").append(getStatusText(order.getStatus()))
              .append(",金额：").append(order.getTotalAmount()).append("元\n");
        }

        return sb.toString();
    }

    //订单状态文字转换
    private String getStatusText(Integer status){
        if(status==null){
            return "未知";
        }
        switch (status){
            case 1:return "待付款";
            case 2:return "已支付";
            case 3:return "待发货";
            case 4:return "待收货";
            case 5:return "已完成";
            case 6:return "已取消";
            default:return "未知状态";
        }
    }

    //AI客服对方
    public String chat(String userMessage,String token,String productInfo){
        //获取用户订单信息
        String orderInfo="";
        if(token!=null && JwtUtil.validateToken(token)){
            Long userId=JwtUtil.getUserId(token);
            orderInfo=getUserOrderInfo(userId);
        }else {
            orderInfo="用户未登录";
        }

        //构建提示词
        String prompt=buildPrompt(userMessage,orderInfo,productInfo);

        return chatLanguageModel.generate(prompt);
    }

    //构建提示词
    private String buildPrompt(String userMessage, String orderInfo,String productInfo) {
        return String.format("""
                你是一个专业的黑胶唱片店AI客服助手，名叫「小冰」。请严格根据以下信息回答用户的问题。

                ========== 店铺商品信息 ==========
                %s
                ========== 店铺固定信息 ==========
                %s
                =================================

                ========== 用户订单信息 ==========
                %s
                =================================

                ========== 用户问题 ==========
                %s
                =================================

                ========== 回答规则（非常重要） ==========
                1. 【商品问题】用户问"有什么专辑"、"XX歌手有哪些专辑"、"专辑多少钱"时，只使用「店铺商品信息」回答。
                2. 【订单问题】用户问"我的订单"、"发货了吗"时，只使用「用户订单信息」回答。
                3. 【店铺问题】用户问"营业时间"、"地址"、"退货"时，只使用「店铺固定信息」回答。
                4. 【禁止混用】除非用户明确问"我的订单中有哪些商品"，否则不要主动提及用户的订单信息。
                5. 【不知道】如果信息中找不到答案，回答："抱歉，这个问题我需要咨询一下人工客服，请拨打400-888-8888。"
                6. 【简洁】回答要简洁，直接给出答案，不要啰嗦。
                7. 【价格】如果用户问价格，直接告诉价格，不要重复专辑名称。
                =================================
                """, productInfo,faqContent, orderInfo, userMessage);
    }

}
