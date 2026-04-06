package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.Combo;
import com.bingzhi.vinyl.mapper.ComboMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userComboController")
@RequestMapping("/api/combo")
public class ComboController {
    @Autowired
    private ComboMapper comboMapper;

    @GetMapping("/list")
    public List<Combo> getUserCombo(){
        return comboMapper.findByStatus(1);
    }
}
