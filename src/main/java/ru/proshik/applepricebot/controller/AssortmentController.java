package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.ProviderType;
import ru.proshik.applepricebot.service.AssortmentService;
import ru.proshik.applepricebot.service.ScreeningService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/assortment")
public class AssortmentController {

    private final ScreeningService screeningService;

    private final AssortmentService assortmentService;

    @Autowired
    public AssortmentController(ScreeningService screeningService, AssortmentService assortmentService) {
        this.screeningService = screeningService;
        this.assortmentService = assortmentService;
    }

    @PostMapping
    public List<Assortment> assortment(@RequestParam(required = false, defaultValue = "false") boolean store) {
        return screeningService.provideProducts(store);
    }

    @GetMapping("filter")
    public List<Assortment> filter(@RequestParam(required = false) String date,
                                   @RequestParam(required = false) FetchType providerType,
                                   @RequestParam(required = false) ProductType productType) {

        Local

        return Collections.emptyList();
    }

}
