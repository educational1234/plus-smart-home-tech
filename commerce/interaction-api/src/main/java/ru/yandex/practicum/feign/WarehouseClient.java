package ru.yandex.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.CartDto;

@FeignClient(name = "warehouse")
public interface WarehouseClient {
    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody CartDto cart) throws FeignException;
}