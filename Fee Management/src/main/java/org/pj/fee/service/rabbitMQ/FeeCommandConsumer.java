package org.pj.fee.service.rabbitMQ;

import lombok.extern.slf4j.Slf4j;
import org.pj.fee.config.RabbitMqConfig;
import org.pj.fee.dto.request.FeeCommandDTO;
import org.pj.fee.service.feeCommand.FeeCommandProcessingServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeeCommandConsumer {

    private final FeeCommandProcessingServiceImpl feeCommandProcessingServiceImpl;

    @Autowired
    public FeeCommandConsumer(FeeCommandProcessingServiceImpl feeCommandProcessingServiceImpl) {
        this.feeCommandProcessingServiceImpl = feeCommandProcessingServiceImpl;
    }

    @RabbitListener(queues = RabbitMqConfig.FEE_COMMAND_QUEUE)
    public void receiveFeeCommand(FeeCommandDTO feeCommandDto) {
        log.info("Received requestId: {} from queue", feeCommandDto.getRequestId());
        try {
            feeCommandProcessingServiceImpl.processFeeCommand(feeCommandDto);
        } catch (Exception e) {
            log.error("Error processing FeeCommand in Consumer", e);
        }
    }
}


