package org.pj.fee.service;

import org.pj.fee.dto.request.FeeCommandDTO;

public interface IFeeCommandManagementService {
    void addFeeCommand(FeeCommandDTO feeCommandDto);
}
