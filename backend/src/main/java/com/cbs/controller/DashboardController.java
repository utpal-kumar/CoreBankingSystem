package com.cbs.controller;

import com.cbs.common.ApiResponse;
import com.cbs.service.DashboardService;
import com.cbs.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final LedgerService ledgerService;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.ok(dashboardService.summary());
    }

    @GetMapping("/ledger/balance/{glAccount}")
    public ApiResponse<BigDecimal> glBalance(@PathVariable String glAccount) {
        return ApiResponse.ok(ledgerService.getBalance(glAccount));
    }
}
