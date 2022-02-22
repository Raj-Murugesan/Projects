package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommonUtil {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommonUtil.class);
    private static final String TABLE_NAME = "test-raj";

    public List<TransactWriteItem> createTransactItems(
            final List<Map<String, AttributeValue>> itemList) {
        return itemList.stream()
                .map(this::createTransactWriteItem)
                .collect(Collectors.toList());
    }

    public TransactWriteItem createTransactWriteItem(final Map<String, AttributeValue> item) {
        return TransactWriteItem.builder()
                .put(Put.builder()
                        .tableName(TABLE_NAME)
                        .item(item)
                        .build())
                .build();
    }
}
