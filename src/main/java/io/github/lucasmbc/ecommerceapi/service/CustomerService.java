package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.repository.CustomerRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.CpfAlreadyExistsException;
import io.github.lucasmbc.ecommerceapi.service.exception.EmailAlreadyExistsException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer create(Customer customer) {
        if(customerRepository.existsByEmail(customer.getEmail())) {
            throw new EmailAlreadyExistsException(customer.getEmail());
        }
        if(customerRepository.existsByCpf(customer.getCpf())) {
            throw new CpfAlreadyExistsException(customer.getCpf());
        }

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer findById(UUID id) {
        return customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        List<Customer> customersList = customerRepository.findAll();
        if(customersList.isEmpty()) throw new NotFoundException("Customers not found");
        return customersList;
    }

    @Transactional
    public Customer update(UUID id, Customer customer) {
        Customer dbCustomer =  customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));

        dbCustomer.setName(customer.getName());
        dbCustomer.setPhone(customer.getPhone());
        dbCustomer.setAddress(customer.getAddress());

        return customerRepository.save(dbCustomer);
    }

    @Transactional
    public void delete(UUID id) {
        Customer dbCustomer = customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));

        customerRepository.deleteById(dbCustomer.getId());
    }
}
