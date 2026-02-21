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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addItem(UUID customerId, UUID productId, Integer quantity) throws CustomBadRequestException, NotFoundException {

        if (quantity == null || quantity <= 0) {
            throw new CustomBadRequestException("Quantity must be greater than zero");
        }

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException("Customer not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getStock() < quantity) {
            throw new CustomBadRequestException("Product stock less than quantity");
        }

        Cart cart = cartRepository.findByCustomerId(customerId).orElseGet(() -> createCart(customer));

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseGet(() -> createItem(cart, product));

        item.setQuantity(item.getQuantity() + quantity);

        cartItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<CartItem> getItems(UUID customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId).orElseThrow(() -> new NotFoundException("Cart not found"));
        return cart.getItems();
    }

    private CartItem createItem(Cart cart, Product product) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(0);
        item.setUnitPrice(product.getPrice());
        return item;
    }

    private Cart createCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setCreatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
}
