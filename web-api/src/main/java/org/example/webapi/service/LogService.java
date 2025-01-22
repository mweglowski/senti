package org.example.webapi.service;

import org.example.data.model.Log;
import org.example.data.repository.DataCatalog;
import org.example.data.repository.LogRepository;
import org.example.webapi.contract.LogDTO;
import org.example.webapi.mappers.LogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {
    private final Logger logger = LoggerFactory.getLogger(LogService.class);
    private final DataCatalog db;
    private final LogMapper logMapper;

    public LogService(DataCatalog db, LogMapper logMapper) {
        this.db = db;
        this.logMapper = logMapper;
    }

    public void log(String level, String message, String serviceName) {
        switch (level.toUpperCase()) {
            case "INFO":
                logger.info(message);
                break;
            case "WARN":
                logger.warn(message);
                break;
            case "ERROR":
                logger.error(message);
                break;
            default:
                logger.debug(message);
        }

        Log log = new Log();
        log.setTimestamp(LocalDateTime.now());
        log.setLevel(level);
        log.setMessage(message);
        log.setServiceName(serviceName);
        db.getLogs().save(log);
    }

    public List<LogDTO> getLogs() {
        List<Log> logs = db.getLogs().findAll();

        String currentLogMessage = "Logs have been fetched successfully in getLogs()";
        log("INFO", currentLogMessage, "LogService");
        logger.info(currentLogMessage);

        return logs
                .stream()
                .map(logMapper::toDTO)
                .toList();
    }
}
