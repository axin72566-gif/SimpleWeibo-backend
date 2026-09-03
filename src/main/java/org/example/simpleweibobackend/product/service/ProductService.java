package org.example.simpleweibobackend.product.service;

import org.example.simpleweibobackend.product.dto.PublishProductRequest;

public interface ProductService {

    Long publishProduct(PublishProductRequest request);
}
