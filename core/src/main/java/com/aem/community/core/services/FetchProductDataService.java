package com.aem.community.core.services;

import java.util.List;
import com.aem.community.core.objects.Product;

public interface FetchProductDataService {

	List<Product> fetchSellableProducts(String productCategory, String productsFolderPath);

}
