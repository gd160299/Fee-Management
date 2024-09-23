package org.pj.fee.RestController;

import org.pj.fee.Dto.Request.FeeCommandDto;
import org.pj.fee.Dto.Response.Response;
import org.pj.fee.Service.FeeCommand.FeeCommandManagementService;
import org.pj.fee.Service.RabbitMQ.FeeCommandProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/fee-commands")
public class FeeCommandRestController {

    private final FeeCommandManagementService feeCommandManagementService;

    private final FeeCommandProducer feeCommandProducer;

    public FeeCommandRestController(FeeCommandManagementService feeCommandManagementService, FeeCommandProducer feeCommandProducer) {
        this.feeCommandManagementService = feeCommandManagementService;
        this.feeCommandProducer = feeCommandProducer;
    }

    @PostMapping("/add")
    public ResponseEntity<Response<String>> addFeeCommand(@RequestBody @Valid FeeCommandDto feeCommandDto) {
        feeCommandManagementService.addFeeCommand(feeCommandDto);
        Response<String> response = Response.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Fee command added successfully.")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process")
    public ResponseEntity<Response<String>> processFeeCommand(@RequestBody @Valid FeeCommandDto feeCommandDto) {
        feeCommandProducer.sendFeeCommand(feeCommandDto);
        Response<String> response = Response.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Fee command processed successfully.")
                .build();
        return ResponseEntity.ok(response);
    }
}
