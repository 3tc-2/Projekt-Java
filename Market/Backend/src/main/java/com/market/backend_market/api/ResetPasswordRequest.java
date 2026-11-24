
package com.market.backend_market.api;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {}
