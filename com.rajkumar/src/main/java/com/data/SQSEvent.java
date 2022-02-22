package com.data;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


public class SQSEvent {
    private List<SqsRecord> records;

    public void setRecords(List<SqsRecord> records) {
        this.records = records;

    }
    public List<SqsRecord> getRecords(){
        return records;
    }
}
