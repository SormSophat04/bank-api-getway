package com.lolc.api.getway.rest;

import com.lolc.api.getway.dto.response.BillResponse;
import com.lolc.api.getway.enums.BillType;
import com.lolc.api.getway.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    @GetMapping
    public ResponseEntity<BillResponse> findRecept(@Valid @RequestParam BillType billType, @Valid @RequestParam String billCode) {
        return Optional.ofNullable(billService.findRecept(billType, billCode))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<BillResponse>> getBills() {
        List<BillResponse> billServiceAll = billService.findAll();
        return ResponseEntity.ok(billServiceAll);
    }
}
