
package com.market.backend_market.api;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {}
