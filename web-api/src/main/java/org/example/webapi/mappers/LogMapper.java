package org.example.webapi.mappers;

import org.example.data.model.Log;
import org.example.webapi.contract.LogDTO;
import org.springframework.stereotype.Component;

@Component
public class LogMapper {
    public LogDTO toDTO(Log log) {
        LogDTO logDTO = new LogDTO();

        logDTO.setServiceName(log.getServiceName());
        logDTO.setMessage(log.getMessage());
        logDTO.setLevel(log.getLevel());
        logDTO.setTimestamp(log.getTimestamp());

        return logDTO;
    }
}
