package com.electronics.store.resource;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.electronics.store.model.Cart;
import com.electronics.store.model.CheckoutDTO;
import com.electronics.store.service.checkout.CheckoutService;

@WebMvcTest(CheckoutResource.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckoutResourceTest {
    
    private static final String CART_ID = "CART_1234";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CheckoutService checkoutService;

    @MockBean
    ModelMapper modelMapper;

    @Test
    void performCheckoutTest() throws Exception {
        // ARRANGE
        Cart cart = Cart.builder().cartId(CART_ID).build();
        when(checkoutService.performCheckout(CART_ID)).thenReturn(cart);
        
        CheckoutDTO checkoutDTO = CheckoutDTO.builder().cartId(CART_ID).build();
        when(modelMapper.map(cart, CheckoutDTO.class)).thenReturn(checkoutDTO);

         // ACT - ASSERT
         mockMvc.perform(MockMvcRequestBuilders.get("/checkout/" + CART_ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cartId").value(CART_ID));
    }
}
