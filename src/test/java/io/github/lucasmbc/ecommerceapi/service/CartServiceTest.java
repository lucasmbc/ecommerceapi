package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.*;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

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
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@email.com");
        user.setPassword("123456");
        user.setRole("USER");

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("John Doe");
        customer.setCpf("1234567890");
        customer.setPhone("(85)999999999");
        customer.setAddress("Rua das Flores");
        customer.setUser(user);

        cart = new Cart();
        cart.setCustomer(customer);

        product = createProduct("iPhone", BigDecimal.valueOf(3000), 10);
    }

    @Test
    @DisplayName("Should add product to cart when customer and product exist")
    void shouldAddProductToCart_WhenCustomerAndProductExist() {

        given(customerRepository.findByUserEmail(any())).willReturn(Optional.of(customer));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(cartRepository.findByCustomerUserEmail(any())).willReturn(Optional.of(cart));
        given(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).willReturn(Optional.empty());

        cartService.addItem(customer.getUser().getEmail(), product.getId(), 2);

        then(cartItemRepository).should().save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should create cart and add product when cart does not exist")
    void shouldCreateCartAndAddProduct_WhenCartDoesNotExist() {

        given(customerRepository.findByUserEmail(any())).willReturn(Optional.of(customer));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(cartRepository.findByCustomerUserEmail(any())).willReturn(Optional.empty());
        given(cartRepository.save(any(Cart.class))).willAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(UUID.randomUUID());
            return cart;
        });

        cartService.addItem(customer.getUser().getEmail(), product.getId(), 2);

        then(cartRepository).should(times(1)).save(any(Cart.class));
        then(cartItemRepository).should(times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw CustomBadRequestException when quantity is less than or equal to zero")
    void addItem_ShouldThrowCustomBadRequestException_WhenQuantityLessThanOrEqualZero() {

        CustomBadRequestException exception = assertThrows(CustomBadRequestException.class, () -> {
            cartService.addItem(customer.getUser().getEmail(), product.getId(), 0);
        });

        assertEquals("Quantity must be greater than zero", exception.getMessage());

        then(cartItemRepository).should(never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when customer not found")
    void addItem_ShouldThrowNotFoundException_WhenCustomerNotFound() {

        given(customerRepository.findByUserEmail(any())).willThrow(new NotFoundException("Customer not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            cartService.addItem(customer.getUser().getEmail(), product.getId(), 2);
        });

        assertEquals("Customer not found", exception.getMessage());

        then(cartItemRepository).should(never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when product not found")
    void addItem_ShouldThrowNotFoundException_WhenProductNotFound() {

        given(customerRepository.findByUserEmail(any())).willReturn(Optional.of(customer));
        given(productRepository.findById(any())).willThrow(new NotFoundException("Product not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            cartService.addItem(customer.getUser().getEmail(), product.getId(), 2);
        });

        assertEquals("Product not found", exception.getMessage());

        then(cartItemRepository).should(never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw CustomBadRequestException when product stock less than quantity")
    void addItem_ShouldThrowCustomBadRequestException_WhenProductStockLessThanQuantity() {

        Product newProduct = createProduct("MacBook", BigDecimal.valueOf(8000), 1);

        given(customerRepository.findByUserEmail(any())).willReturn(Optional.of(customer));
        given(productRepository.findById(any())).willReturn(Optional.of(newProduct));

        CustomBadRequestException exception = assertThrows(CustomBadRequestException.class, () -> {
            cartService.addItem(customer.getUser().getEmail(), product.getId(), 2);
        });

        assertEquals("Product stock less than quantity", exception.getMessage());

        then(cartItemRepository).should(never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should return cart items when cart exists")
    void getItems_ShouldReturnCartItems_WhenCartExists() {

        List<CartItem> cartItems = Arrays.asList(
                createCartItem("iPhone", BigDecimal.valueOf(3000), 1),
                createCartItem("MacBook", BigDecimal.valueOf(8000), 1)
        );
        cart.setItems(cartItems);

        given(cartRepository.findByCustomerUserEmail(any())).willReturn(Optional.of(cart));

        List<CartItem> saved = cartService.getItems(customer.getUser().getEmail());

        assertNotNull(saved);
        assertEquals(2, saved.size());
        assertEquals("iPhone", saved.getFirst().getProduct().getName());
        assertEquals("MacBook", saved.get(1).getProduct().getName());

        then(cartRepository).should(times(1)).findByCustomerUserEmail(customer.getUser().getEmail());
    }

    @Test
    @DisplayName("Should throw NotFoundException when cart not found")
    void getItems_ShouldThrowBusinessException_WhenCartNotFound() {
        given(cartRepository.findByCustomerUserEmail(any())).willThrow(new NotFoundException("Cart not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            cartService.getItems(customer.getUser().getEmail());
        });

        assertEquals("Cart not found", exception.getMessage());
        verify(cartRepository, times(1)).findByCustomerUserEmail(customer.getUser().getEmail());
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