package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.Combo;
import com.bingzhi.vinyl.entity.ComboItem;
import com.bingzhi.vinyl.entity.Product;
import com.bingzhi.vinyl.mapper.ComboMapper;
import com.bingzhi.vinyl.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("userComboController")
@RequestMapping("/api/combo")
public class ComboController {
    @Autowired
    private ComboMapper comboMapper;

    @Autowired
    private ProductMapper productMapper;

    //查询全部
    @GetMapping("/list")
    public List<Combo> getUserCombo(){
        return comboMapper.findByStatus(1);
    }

    //根据id查询
    @GetMapping("/{id}")
    public Map<String,Object> getComboDetail(@PathVariable Long id){
        Combo combo=comboMapper.findById(id);
        List<ComboItem> items=comboMapper.findItemsByComboId(id);

        //补充商品详情
        for (ComboItem item:items){
            Product product=productMapper.findById(item.getProductId());
            item.setProductName(product.getName());
            item.setProductPrice(product.getPrice());
            item.setProductImage(product.getImage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("combo", combo);
        result.put("items", items);
        return result;
    }
}
