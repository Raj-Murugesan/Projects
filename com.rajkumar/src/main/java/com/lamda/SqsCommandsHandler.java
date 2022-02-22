package com.lamda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.data.SQSEvent;
import com.data.SqsRecord;
import com.exception.SqsCommandException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;


public class SqsCommandsHandler implements RequestHandler<Map<String, Object>, String> {
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SqsCommandsHandler.class);
    ObjectMapper objectMapper = new ObjectMapper();
    CommandsHelper commandsHelper = new CommandsHelper();

    @Override
    public String handleRequest(final Map<String, Object> sqsMessages, final Context context){


        final SQSEvent sqsEvent;
        try{
            final String eventString = objectMapper.writeValueAsString(sqsMessages);
            sqsEvent = objectMapper.readValue(eventString, SQSEvent.class);

        } catch (Exception e){
            throw new SqsCommandException("Exception occured", e);
        }
        try{

             return sqsEvent.getRecords().stream()
                    .map(this::processSqsMessage)
                    .collect(Collectors.joining());
        } catch (RuntimeException e){
            throw new SqsCommandException("unexpected exception",e);
        } catch(Exception e){
            throw new SqsCommandException("Exception Occured",e);
        }

    }

    private String processSqsMessage(SqsRecord sqsRecord) {
        LOGGER.info("process Sqs message:{}", sqsRecord.getMessageId());
        final String messgeBody = sqsRecord.getBody();
        final Map<String, Object> sqsMessageBodyMap;
        try{
            sqsMessageBodyMap = objectMapper.readValue(messgeBody, Map.class);
        } catch (Exception e){
            throw new SqsCommandException("Deserialization failed" + sqsRecord.getMessageId());
        }

        final Object sqsInput;
        try{
            sqsInput = objectMapper.writeValueAsString(sqsMessageBodyMap);
        } catch (JsonProcessingException e){
            return "Deserialization failed";
        }
        return commandsHelper.processCommand(sqsInput);
    }
}
