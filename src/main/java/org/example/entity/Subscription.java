package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entity.enums.SubStatus;
import org.example.entity.enums.SubType;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Subscription implements Serializable {

    private int userId;
    private LocalDateTime startTime;
    private LocalDateTime endDate;
    private SubType subType;
    private SubStatus status;

}
