package com.electronics.store.service.checkout;

import com.electronics.store.domainvalue.DiscountType;
import com.electronics.store.exception.InsufficientQuantityException;
import com.electronics.store.exception.NoDiscountFoundForProduct;
import com.electronics.store.exception.NoSuchCartExist;
import com.electronics.store.exception.NoSuchProductInStore;
import com.electronics.store.exception.ProductNotFoundException;
import com.electronics.store.model.*;
import com.electronics.store.service.cart.CartService;
import com.electronics.store.service.discount.DiscountService;
import com.electronics.store.service.inventory.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class CheckoutServiceTest {

    private static final String USER_ID = "USER_1234";
    private static final Integer PRODUCT_QTY = 4;
    private static final String PRODUCT_ID = "1234";
    private static final String PRODUCT_NAME = "DUMMY PRODUCT";
    private static final String PRODUCT_DESCRIPTION = "This is the dummy description of the product";
    private static final Double PRODUCT_PRICE = 20.0;
    private static final String INVENTORY_ID ="INV_1234" ;
    private static final ZonedDateTime dateExpired = ZonedDateTime.parse("2020-09-09T10:15:30+08:00");
    private static final Integer MINIMUM_QTY = 2;
    private static final Integer DISCOUNT_PERCENTAGE = 20;
    private static final String PRODUCT_DISCOUNT_ID = "PRODUCT_DISCOUNT_01";
    private static final String DISCOUNT_ID = "DISCOUNT_01";
    private static final Double TOTAL_DISCOUNT = 8.0;
    private static final Double CART_VALUE = 72.0;
    private static final String CART_ID = "CART_1234";
    private static final String CART_ITEM_ID ="CART_ITEM_1234" ;
    private static final Double GRAND_DISCOUNT = 8.0;
    private static final Double GRAND_COST = 72.0;

    @Autowired
    CheckoutService checkoutService;

    @MockBean
    DiscountService discountService;

    @MockBean
    CartService cartService;

    @MockBean
    InventoryService inventoryService;

    @Test
    void performCheckout() throws ProductNotFoundException, NoSuchProductInStore, NoSuchCartExist, InsufficientQuantityException, NoDiscountFoundForProduct {
        Cart cart = getCart();
        InventoryItem inventoryItem = getInventoryItem();
        when(cartService.getCartByCartId(anyString())).thenReturn(cart);
        when(inventoryService.getInventoryDetailForProduct(anyString())).thenReturn(inventoryItem);
        when(discountService.getDiscountForProduct(anyString())).thenReturn(getProductDiscount());
        Cart resultCart = checkoutService.performCheckout(CART_ID);
        assertNotNull(resultCart);
        assertEquals(resultCart.getCartId(),CART_ID);
        assertEquals(resultCart.getCartValue(),CART_VALUE);
        assertEquals(resultCart.getTotalDiscount(),TOTAL_DISCOUNT);
    }

    // Data prepare

    private ProductDiscount getProductDiscount() {
        return ProductDiscount.builder()
        .productDiscountId(PRODUCT_DISCOUNT_ID)
        .discounts(Arrays.asList(getDiscount()))
        .productId(PRODUCT_ID).build();
    }

    private Product getProduct() {
        return Product.builder().productId(PRODUCT_ID).productPrice(PRODUCT_PRICE)
                .productDescription(PRODUCT_DESCRIPTION).productName(PRODUCT_NAME).build();
    }


    private Discount getDiscount() {
        return Discount.builder().discountId(DISCOUNT_ID).dateExpired(dateExpired)
                    .discountType(DiscountType.PERCENTAGE).minimumQty(MINIMUM_QTY).discountPercent(DISCOUNT_PERCENTAGE)
                    .dateCreated(ZonedDateTime.now()).build();
    }

    private InventoryItem getInventoryItem() {
        return InventoryItem.builder().inventoryId(INVENTORY_ID)
                .productId(PRODUCT_ID).productQuantity(PRODUCT_QTY).build();
    }
    private Cart getCart(){
        Product product = getProduct();
        Map<String,CartItem> cartItemsMap = new HashMap<>();
        CartItem cartItem = CartItem.builder().cartItemId(CART_ITEM_ID).productQty(PRODUCT_QTY).product(product)
                .discountApplied(getDiscount()).grandDiscount(GRAND_DISCOUNT).grandCost(GRAND_COST).build();
        cartItemsMap.put(PRODUCT_ID,cartItem);
        return Cart.builder().userId(USER_ID).cartId(CART_ID).cartItemMap(cartItemsMap).build();
    }
}