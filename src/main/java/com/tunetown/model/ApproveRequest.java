package com.tunetown.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveRequest {
    private int isApprove;
    private UUID hostId;
    private UUID approveUserId;
}
