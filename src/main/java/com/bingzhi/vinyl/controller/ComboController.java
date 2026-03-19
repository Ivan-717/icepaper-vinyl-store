package com.bingzhi.vinyl.controller;

import com.bingzhi.vinyl.entity.Combo;
import com.bingzhi.vinyl.entity.ComboItem;
import com.bingzhi.vinyl.entity.Product;
import com.bingzhi.vinyl.mapper.ComboMapper;
import com.bingzhi.vinyl.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/combo")
public class ComboController {

    @Autowired
    private ComboMapper comboMapper;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public List<Combo> list(){
        return comboMapper.findAll();
    }

    //查询单个套餐（包含商品详情）
    @GetMapping("/{id}")
    public Combo getById(@PathVariable Long id ){
        //1.查询套餐基本信息
        Combo combo=comboMapper.findById(id);
        if(combo==null){
            return null;
        }

        //2.查询套餐包含的商品ID列表
        List<ComboItem> items=comboMapper.findItemsByComboId(id);

        //3.为每个商品填充详细信息
        for (ComboItem item:items){
            Product product =productMapper.findById(item.getProductId());
            if(product!=null){
                item.setProductName(product.getName());
                item.setProductArtist(product.getArtist());
                item.setProductPrice(product.getPrice());
            }
        }

        //4.设置商品列表
        combo.setItems(items);
        return combo;

    }

    //新增套餐
    @PostMapping
    public Map<String,Object> add(@RequestBody Combo combo){
        //插入套餐（拿会生成的id）
        comboMapper.insert(combo);

        //插入套餐商品关联
        if(combo.getItems()!=null){
            for (ComboItem item:combo.getItems()){
                item.setComboId(combo.getId());
                comboMapper.insertItem(item);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "新增成功");
        result.put("data", combo);
        return result;
    }

    //更新套餐
    @PutMapping
    public Map<String,String> update(@RequestBody Combo combo){
        //先删除所有旧关联
        comboMapper.deleteItemsByComboId(combo.getId());

        //更新套餐信息
        comboMapper.update(combo);

        //重新插入关联
        if(combo.getItems()!=null){
            for(ComboItem item:combo.getItems()){
                item.setComboId(combo.getId());
                comboMapper.insertItem(item);
            }
        }

        Map<String, String> result = new HashMap<>();
        result.put("message", "更新成功");
        return result;
    }

    //更新套餐状态
    @PutMapping("/{id}/status")
    public Map<String,String> updateStatus(@PathVariable Long id,@RequestBody Map<String,Integer> request){
        Integer status=request.get("status");
        comboMapper.updateStatus(id,status);

        Map<String,String> result=new HashMap<>();
        result.put("message","状态更新成功");
        return result;
    }

    //删除套餐
    @DeleteMapping("/{id}")
    public Map<String,String> delete(@PathVariable Long id){
        //删除关联
        comboMapper.deleteItemsByComboId(id);
        //删除套餐
        comboMapper.deleteById(id);

        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }
}
