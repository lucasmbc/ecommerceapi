package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.Cart;
import io.github.lucasmbc.ecommerceapi.domain.model.CartItem;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.model.Product;
import io.github.lucasmbc.ecommerceapi.domain.repository.CartItemRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.CartRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.CustomerRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.ProductRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.CustomBadRequestException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("John Doe");
        customer.setEmail("johndoe@email.com");
        customer.setPassword("johndoe");
        customer.setCpf("1234567890");

        cart = new Cart();
        cart.setCustomer(customer);

        product = createProduct("iPhone", BigDecimal.valueOf(3000), 10);
    }

    @Test
    @DisplayName("Should add product to cart when customer and product exist")
    void shouldAddProductToCart_WhenCustomerAndProductExist() {

        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomerId(any())).willReturn(Optional.of(cart));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).willReturn(Optional.empty());

        cartService.addItem(customer.getId(), product.getId(), 2);

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should create cart and add product when cart does not exist")
    void shouldCreateCartAndAddProduct_WhenCartDoesNotExist() {

        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(cartRepository.findByCustomerId(any())).willReturn(Optional.empty());
        given(cartRepository.save(any(Cart.class))).willAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(UUID.randomUUID());
            return cart;
        });

        cartService.addItem(customer.getId(), product.getId(), 2);

        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw CustomBadRequestException when quantity is less than or equal to zero")
    void addItem_ShouldThrowCustomBadRequestException_WhenQuantityLessThanOrEqualZero() {

        CustomBadRequestException exception = assertThrows(CustomBadRequestException.class, () -> {
            cartService.addItem(customer.getId(), product.getId(), 0);
        });

        assertEquals("Quantity must be greater than zero", exception.getMessage());

    }

    @Test
    @DisplayName("Should throw NotFoundException when customer not found")
    void addItem_ShouldThrowNotFoundException_WhenCustomerNotFound() {

        given(customerRepository.findById(any())).willReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            cartService.addItem(customer.getId(), product.getId(), 2);
        });

        assertEquals("Customer not found", exception.getMessage());

    }

    @Test
    @DisplayName("Should throw NotFoundException when product not found")
    void addItem_ShouldThrowNotFoundException_WhenProductNotFound() {

        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        given(productRepository.findById(any())).willReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            cartService.addItem(customer.getId(), product.getId(), 2);
        });

        assertEquals("Product not found", exception.getMessage());

    }

    @Test
    @DisplayName("Should throw CustomBadRequestException when product stock less than quantity")
    void addItem_ShouldThrowCustomBadRequestException_WhenProductStockLessThanQuantity() {

        Product newProduct = createProduct("MacBook", BigDecimal.valueOf(8000), 1);

        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        given(productRepository.findById(any())).willReturn(Optional.of(newProduct));

        CustomBadRequestException exception = assertThrows(CustomBadRequestException.class, () -> {
            cartService.addItem(customer.getId(), product.getId(), 2);
        });

        assertEquals("Product stock less than quantity", exception.getMessage());

    }

    @Test
    @DisplayName("Should return cart items when cart exists")
    void getItems_ShouldReturnCartItems_WhenCartExists() {

        List<CartItem> cartItems = Arrays.asList(
                createCartItem("iPhone", BigDecimal.valueOf(3000), 1),
                createCartItem("MacBook", BigDecimal.valueOf(8000), 1)
        );
        cart.setItems(cartItems);

        given(cartRepository.findByCustomerId(any())).willReturn(Optional.of(cart));

        List<CartItem> saved = cartService.getItems(customer.getId());

        assertNotNull(saved);
        assertEquals(2, saved.size());
        assertEquals("iPhone", saved.getFirst().getProduct().getName());
        assertEquals("MacBook", saved.get(1).getProduct().getName());

        verify(cartRepository, times(1)).findByCustomerId(customer.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException when cart not found")
    void getItems_ShouldThrowBusinessException_WhenCartNotFound() {
        given(cartRepository.findByCustomerId(any())).willThrow(new NotFoundException("Cart not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            cartService.getItems(customer.getId());
        });

        assertEquals("Cart not found", exception.getMessage());
        verify(cartRepository, times(1)).findByCustomerId(customer.getId());
    }

    private CartItem createCartItem(String productName, BigDecimal price, Integer quantity) {
        Product product = createProduct(productName, price, 10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(product.getPrice());

        return cartItem;
    }

    private Product createProduct(String name, BigDecimal price, Integer stock) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        return product;
    }
}