package org.pj.fee.service;

import org.pj.fee.dto.request.FeeCommandDTO;

public interface IFeeCommandProcessingService {
    void processFeeCommand(FeeCommandDTO feeCommandDto);
}
