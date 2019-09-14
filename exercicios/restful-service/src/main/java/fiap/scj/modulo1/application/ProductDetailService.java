package fiap.scj.modulo1.application;

import java.io.Serializable;
import java.util.List;

import fiap.scj.modulo1.domain.Product;
import fiap.scj.modulo1.domain.ProductDetail;
import fiap.scj.modulo1.infrastructure.ProductServiceException;

public interface ProductDetailService extends Serializable {
	
	List<ProductDetail> searchByProduct(Product product) throws ProductServiceException;

    List<ProductDetail> search(String keyword) throws ProductServiceException;

    ProductDetail create(ProductDetail productDetail) throws ProductServiceException;

    ProductDetail retrieve(Long id) throws ProductServiceException;

    ProductDetail update(Long id, ProductDetail productDetail) throws ProductServiceException;

    void delete(Long id) throws ProductServiceException;
}
