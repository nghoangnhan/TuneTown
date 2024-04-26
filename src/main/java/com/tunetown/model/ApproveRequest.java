package com.tunetown.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveRequest {
    private int isApprove;
    private int hostId;
    private int approveUserId;
}
