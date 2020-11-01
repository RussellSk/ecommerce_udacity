package com.example.demo.controllers;

import com.example.demo.SareetaApplication;
import com.example.demo.SareetaApplicationTests;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SareetaApplication.class)
public class ItemControllerTest {

    @Autowired
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Test
    public void getItems() {
        when(itemRepository.findAll()).thenReturn(createItemsList());
        Assert.assertNotNull(itemController.getItems());
        Assert.assertEquals(2, itemController.getItems().getBody().size());
    }

    @Test
    public void getItemById() {
        Optional<Item> optionalItem = Optional.of(createItemsList().get(0));
        when(itemRepository.findById(1L)).thenReturn(optionalItem);

        Assert.assertNotNull(itemController.getItemById(1L));
        Assert.assertEquals("Round Widget", itemController.getItemById(1L).getBody().getName());
    }

    @Test
    public void getItemByName() {
        List<Item> roundItems = new ArrayList<>();
        roundItems.add(createItemsList().get(0));
        when(itemRepository.findByName("Round Widget")).thenReturn(roundItems);

        Assert.assertNotNull(itemController.getItemsByName("Round Widget"));
        Assert.assertEquals("Round Widget", itemController.getItemsByName("Round Widget").getBody().get(0).getName());

        Assert.assertEquals(404, itemController.getItemsByName("Some Other Widget").getStatusCodeValue());
    }

    private List<Item> createItemsList() {
        List<Item> list = new ArrayList<>();
        list.add(SareetaApplicationTests.getItem(1L, "Item 1", "Description 1", BigDecimal.TEN));
        list.add(SareetaApplicationTests.getItem(2L, "Item 2", "Description 2", BigDecimal.valueOf(20)));
        list.add(SareetaApplicationTests.getItem(3L, "Item 3", "Description 3", BigDecimal.valueOf(30)));
        return list;
    }

}
