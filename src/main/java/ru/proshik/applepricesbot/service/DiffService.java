package ru.proshik.applepricesbot.service;

import ru.proshik.applepricesbot.dto.ProductKey;
import ru.proshik.applepricesbot.dto.DiffProducts;
import ru.proshik.applepricesbot.storage.model.Fetch;
import ru.proshik.applepricesbot.dto.HistoryDiff;
import ru.proshik.applepricesbot.storage.model.Product;
import ru.proshik.applepricesbot.storage.model.ProductType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiffService {

    public List<HistoryDiff> historyDiff(List<Fetch> fetches, ProductType productType) {
        List<HistoryDiff> result = new ArrayList<>();

        List<Fetch> limitedFetches = fetches.stream()
                .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
                .limit(3)
                .collect(Collectors.toList());
        for (int i = 0; i < limitedFetches.size() - 1; i++) {
            List<DiffProducts> diff = findDiff(limitedFetches.get(i + 1), limitedFetches.get(i), productType);
            if (diff.isEmpty()) {
                continue;
            }
            result.add(new HistoryDiff(limitedFetches.get(i + 1).getCreatedDate(), limitedFetches.get(i).getCreatedDate(), diff));
        }

        return result;
    }

    public List<DiffProducts> findDiff(Fetch oldFetch, Fetch newFetch, ProductType productType) {
        List<DiffProducts> diff = new ArrayList<>();

        Map<ProductKey, List<Product>> groupNewFetch = newFetch.getProducts().stream()
                .collect(Collectors.groupingBy(o -> new ProductKey(o.getTitle(), o.getDescription(), o.getProductType())));
        Map<ProductKey, List<Product>> groupLastFetch = oldFetch.getProducts().stream()
                .collect(Collectors.groupingBy(o -> new ProductKey(o.getTitle(), o.getDescription(), o.getProductType())));

        List<Product> withoutDiff = new ArrayList<>();

        for (Map.Entry<ProductKey, List<Product>> newEntry : groupNewFetch.entrySet()) {
            if (productType != null && newEntry.getKey().getProductType() != productType) {
                continue;
            }
            List<Product> oldProducts = groupLastFetch.get(newEntry.getKey());
            if (oldProducts != null) {
                List<Product> newProducts = newEntry.getValue();
                newProducts.sort(Comparator.comparing(Product::getPrice));
                oldProducts.sort(Comparator.comparing(Product::getPrice));

                if (newProducts.size() == oldProducts.size()) {
                    for (int i = 0; i < newProducts.size(); i++) {
                        if (!oldProducts.get(i).getPrice().equals(newProducts.get(i).getPrice())
                                || (oldProducts.get(i).getAvailable() != null && newProducts.get(i).getAvailable() != null
                                && (!oldProducts.get(i).getAvailable().equals(newProducts.get(i).getAvailable())))) {
                            diff.add(new DiffProducts(oldProducts.get(i), newProducts.get(i)));
                        } else {
                            withoutDiff.add(oldProducts.get(i));
                        }
                    }
                } else {
                    List<Product> forAdded = new ArrayList<>();
                    if (newProducts.size() > oldProducts.size()) {
                        newProducts.retainAll(oldProducts);
                        forAdded.addAll(newProducts);
                    } else {
                        oldProducts.retainAll(newProducts);
                        forAdded.addAll(oldProducts);
                    }
                    for (Product p : forAdded) {
                        diff.add(new DiffProducts(null, p));
                    }
                }
            } else {
                for (Product p : newEntry.getValue()) {
                    diff.add(new DiffProducts(null, p));
                }
            }
        }

//        if (diff.size() > 0) {
//            List<Product> old = oldFetch.getProducts();
//
//            List<Product> olders = diff.stream()
//                    .filter(diffProducts -> diffProducts.getOldProductDesc() != null)
//                    .map(DiffProducts::getOldProductDesc)
//                    .collect(Collectors.toList());
//
//            old.removeAll(olders);
//            old.removeAll(withoutDiff);
//
//            if (old.size() > 0) {
//                for (Product p : old) {
//                    diff.add(new DiffProducts(p, null));
//                }
//            }
//        }

        return diff;
    }

}
