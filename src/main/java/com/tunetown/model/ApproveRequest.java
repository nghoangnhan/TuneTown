package com.tunetown.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
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
    @OneToOne(fetch = FetchType.LAZY)
    private User host;
    @OneToOne(fetch = FetchType.LAZY)
    private User approveUser;
}
