package org.pj.fee.RestController;

import org.pj.fee.Dto.FeeCommandDto;
import org.pj.fee.Service.FeeCommandManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/fee-commands")
public class FeeCommandRestController {

    private static final Logger logger = LoggerFactory.getLogger(FeeCommandRestController.class);

    @Autowired
    private FeeCommandManagementService feeCommandManagementService;

    @PostMapping("/add")
    public ResponseEntity<String> addFeeCommand(@RequestBody @Valid FeeCommandDto feeCommandDto) {
        logger.info("Received request to add fee command with requestId: {}", feeCommandDto.getRequestId());
        feeCommandManagementService.addFeeCommand(feeCommandDto);
        logger.info("Fee command added successfully for requestId: {}", feeCommandDto.getRequestId());
        return ResponseEntity.ok("Fee command added successfully.");
    }
}
