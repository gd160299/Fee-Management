package org.pj.fee.restController;

import org.pj.fee.dto.request.FeeCommandDTO;
import org.pj.fee.dto.response.Response;
import org.pj.fee.service.feeCommand.FeeCommandManagementServiceImpl;
import org.pj.fee.service.rabbitMQ.FeeCommandProducer;
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

    private final FeeCommandManagementServiceImpl feeCommandManagementServiceImpl;

    private final FeeCommandProducer feeCommandProducer;

    public FeeCommandRestController(FeeCommandManagementServiceImpl feeCommandManagementServiceImpl, FeeCommandProducer feeCommandProducer) {
        this.feeCommandManagementServiceImpl = feeCommandManagementServiceImpl;
        this.feeCommandProducer = feeCommandProducer;
    }

    @PostMapping("/add")
    public ResponseEntity<Response<String>> addFeeCommand(@RequestBody @Valid FeeCommandDTO feeCommandDto) {
        feeCommandManagementServiceImpl.addFeeCommand(feeCommandDto);
        Response<String> response = Response.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Fee command added successfully.")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process")
    public ResponseEntity<Response<String>> processFeeCommand(@RequestBody @Valid FeeCommandDTO feeCommandDto) {
        feeCommandProducer.sendFeeCommand(feeCommandDto);
        Response<String> response = Response.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Fee command processed successfully.")
                .build();
        return ResponseEntity.ok(response);
    }
}
