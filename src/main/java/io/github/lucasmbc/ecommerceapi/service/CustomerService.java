package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CustomerRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.CustomerMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.model.User;
import io.github.lucasmbc.ecommerceapi.domain.repository.CustomerRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.UserRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.CpfAlreadyExistsException;
import io.github.lucasmbc.ecommerceapi.service.exception.CustomerAlreadyExistsException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Customer create(CustomerRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(()->new NotFoundException("User not found"));

        if (customerRepository.findByUserEmail(userEmail).isPresent()) {
            throw new CustomerAlreadyExistsException(userEmail);
        }

        if(customerRepository.existsByCpf(dto.getCpf())) {
            throw new CpfAlreadyExistsException(dto.getCpf());
        }

        Customer customer = CustomerMapper.toEntity(dto, user);

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer findByEmail(String userEmail) {
        return customerRepository.findByUserEmail(userEmail).orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public Page<Customer> findAll(Pageable pageable) {
        Page<Customer> customersList = customerRepository.findAll(pageable);
        if(customersList.isEmpty()) throw new NotFoundException("Customers not found");
        return customersList;
    }

    @Transactional
    public Customer update(
            String userEmail,
            CustomerRequestDTO customer
    ) {
        Customer dbCustomer =  customerRepository.findByUserEmail(userEmail).orElseThrow(() -> new NotFoundException("Customer not found"));
        User dbUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("User not found"));

        dbCustomer.setName(customer.getName());
        dbCustomer.setPhone(customer.getPhone());
        dbCustomer.setAddress(customer.getAddress());
        dbCustomer.setUser(dbUser);

        return customerRepository.save(dbCustomer);
    }

    @Transactional
    public void delete(String userEmail) {
        Customer dbCustomer = customerRepository.findByUserEmail(userEmail).orElseThrow(() -> new NotFoundException("Customer not found"));

        customerRepository.deleteById(dbCustomer.getId());
    }
}
