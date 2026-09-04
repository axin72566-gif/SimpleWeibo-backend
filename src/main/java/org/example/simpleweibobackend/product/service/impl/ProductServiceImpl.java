package org.example.simpleweibobackend.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.product.dto.PublishProductRequest;
import org.example.simpleweibobackend.product.entity.Product;
import org.example.simpleweibobackend.product.enums.ProductStatus;
import org.example.simpleweibobackend.product.mapper.ProductMapper;
import org.example.simpleweibobackend.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Long publishProduct(PublishProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(ProductStatus.ON_SALE);
        productMapper.insert(product);
        return product.getId();
    }
}
