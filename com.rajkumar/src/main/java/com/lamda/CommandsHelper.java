package com.lamda;


import com.CommonUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandsHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandsHelper.class);
    Party party;
    CommonUtil commonUtil;
    DynamoDbClient dynamoDbClient;
    ObjectMapper objectMapper = new ObjectMapper();

    public String processCommand(Object requestBody) {

        try {
            party = objectMapper.readValue((JsonParser) requestBody, Party.class);
        } catch (Exception e) {
            try {
                throw new UnexpectedException("Processing Exception", e);
            } catch (UnexpectedException e1) {
                e1.printStackTrace();
            }
        }
        LOGGER.info("Party Id:{}", party.getPartyIdentifier());
        LOGGER.info("party details : {} {}", party.getAge(), party.getLocation());
        final List<Map<String, AttributeValue>> transactItems = new ArrayList<>();
        final Map<String, AttributeValue> partyItem = createPartyItem(party);
        transactItems.add(partyItem);

        final List<TransactWriteItem> transactWriteItem = commonUtil.createTransactItems(transactItems);
        TransactWriteItemsRequest transactWriteItemsRequest = TransactWriteItemsRequest
                .builder()
                .transactItems(transactWriteItem)
                .returnConsumedCapacity("TOTAL")
                .build();

        dynamoDbClient.transactWriteItems(transactWriteItemsRequest);
        return "Message processed Successfully";
    }

    private Map<String, AttributeValue> createPartyItem(Party party) {
        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("pk", AttributeValue.builder().s(party.getPartyIdentifier()).build());
        item.put("sk", AttributeValue.builder().s(party.getAge()).build());
        item.put("location", AttributeValue.builder().s(party.getLocation()).build());
        return item;
    }

}
