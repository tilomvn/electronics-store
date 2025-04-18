package com.electronics.store.resource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.electronics.store.domainvalue.DiscountType;
import com.electronics.store.model.Discount;
import com.electronics.store.model.ProductDiscount;
import com.electronics.store.request.CreateDiscountRequest;
import com.electronics.store.service.discount.DiscountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DiscountResource.class)
@AutoConfigureMockMvc(addFilters = false)
class DiscountResourceTest {
    
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private DiscountService cartService;

    private static final String PRODUCT_ID = "PRODUCT_1234";
    private static final ZonedDateTime dateExpired = ZonedDateTime.parse("2025-09-09T10:15:30+08:00");
    private static final Integer MINIMUM_QTY = 2;
    private static final Integer DISCOUNT_PERCENTAGE = 20;
    
    @Test
    void createNewDiscountDealForProduct() throws Exception {
        // Arrange
        CreateDiscountRequest createDiscountRequest = CreateDiscountRequest.builder()
               .productId(PRODUCT_ID)
               .discountType(DiscountType.PERCENTAGE)
               .minimumQty(MINIMUM_QTY)
               .discountPercent(DISCOUNT_PERCENTAGE)
               //.dateExpired(dateExpired)
               .build();
        String discountJson = new ObjectMapper().writeValueAsString(createDiscountRequest);

        Discount discount = Discount.builder()
            .discountId("DISCOUNT_1234")
            .discountType(DiscountType.PERCENTAGE)
            .minimumQty(MINIMUM_QTY)
            .discountPercent(DISCOUNT_PERCENTAGE)
            .dateExpired(dateExpired)
            .dateCreated(ZonedDateTime.now())
            .build();
        List<Discount> discounts = new ArrayList<>();
        discounts.add(discount);
        ProductDiscount productDiscount = ProductDiscount.builder()
            .productDiscountId("PRODUCT_DISCOUNT_1234")
            .productId(PRODUCT_ID)
            .discounts(discounts)
            .build();
        when(cartService.createNewDiscount(any())).thenReturn(productDiscount);

        // ACT - ASSERT
        mockMvc.perform(MockMvcRequestBuilders.post("/discount")
                .content(discountJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productDiscountId").isNotEmpty());
    }
}
