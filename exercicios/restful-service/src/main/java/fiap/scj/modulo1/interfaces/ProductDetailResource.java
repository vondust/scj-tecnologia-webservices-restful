package fiap.scj.modulo1.interfaces;

import fiap.scj.modulo1.application.ProductDetailService;
import fiap.scj.modulo1.application.ProductService;
import fiap.scj.modulo1.domain.Product;
import fiap.scj.modulo1.domain.ProductDetail;
import fiap.scj.modulo1.infrastructure.ProductServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static fiap.scj.modulo1.infrastructure.ProductServiceException.*;

@RestController
@RequestMapping("/products/{productId}/details")
@Slf4j
public class ProductDetailResource {

	private final ProductService serviceProduct;
	private final ProductDetailService serviceDetail;

	@Autowired
	public ProductDetailResource(ProductService serviceProduct, ProductDetailService serviceDetail) {
		this.serviceProduct = serviceProduct;
		this.serviceDetail = serviceDetail;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(code = HttpStatus.OK)
	public List<ProductDetail> searchAll(@PathVariable Long productId) {
		log.info("Processing search request");
		try {
			return serviceDetail.searchByProduct(Product.builder().id(productId).build());
		} catch (ProductServiceException e) {
			log.error("Error processing search request", e);
			throw exceptionHandler(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<Void> create(@PathVariable Long productId, @RequestBody ProductDetail detail)
			throws ProductServiceException {
		log.info("Processing create request");
		try {
			Product product = serviceProduct.retrieve(productId);
			detail.setProduct(product);
			serviceDetail.create(detail);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(detail.getId())
					.toUri();
			return ResponseEntity.created(location).build();
		} catch (ProductServiceException e) {
			log.error("Error processing create request", e);
			throw exceptionHandler(e);
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public ProductDetail retrieve(@PathVariable Long productId, @PathVariable Long id) throws ProductServiceException {
		log.info("Processing retrieve request");
		try {
			ProductDetail detail = serviceDetail.retrieve(id);
			if (detail.getProduct().getId() == productId)
				return detail;

//			List<ProductDetail> details = serviceDetail.searchByProduct(serviceProduct.retrieve(productId));
//			for (ProductDetail productDetail : details) {
//				if (productDetail.getId() == id)
//					return productDetail;
//			}

			return null;
		} catch (ProductServiceException e) {
			log.error("Error processing retrieve request", e);
			throw exceptionHandler(e);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public ProductDetail update(@PathVariable Long productId, @PathVariable Long id,
			@RequestBody ProductDetail productDetail) throws ProductServiceException {
		log.info("Processing update request");
		try {
			ProductDetail detail = serviceDetail.retrieve(id);
			if (detail.getProduct().getId() == productId) {
				return serviceDetail.update(id, productDetail);
			}

			return null;
		} catch (ProductServiceException e) {
			log.error("Error processing update request", e);
			throw exceptionHandler(e);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public void delete(@PathVariable Long id) throws ProductServiceException {
		log.info("Processing delete request");
		try {
			serviceProduct.delete(id);
		} catch (ProductServiceException e) {
			log.error("Error processing delete request", e);
			throw exceptionHandler(e);
		}
	}

	private ResponseStatusException exceptionHandler(ProductServiceException e) {
		if (e.getOperation() == null || e.getOperation().isEmpty()) {
			return new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		if (SEARCH_OPERATION_ERROR.equals(e.getOperation()) || CREATE_OPERATION_ERROR.equals(e.getOperation())
				|| RETRIEVE_OPERATION_ERROR.equals(e.getOperation()) || UPDATE_OPERATION_ERROR.equals(e.getOperation())
				|| DELETE_OPERATION_ERROR.equals(e.getOperation())) {
			return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (PRODUCT_NOT_FOUND_ERROR.equals(e.getOperation())) {
			return new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return new ResponseStatusException(HttpStatus.BAD_REQUEST);
	}
}
